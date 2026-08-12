package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.ObjectFactory;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.POAElementType;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.Status;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;

@ExtendWith(MockitoExtension.class)
class CreateInvoiceClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";

  @Mock WebServiceTemplate webServiceTemplate;

  @Captor ArgumentCaptor<JAXBElement<InvoiceAddRQ>> requestCaptor;

  @Captor ArgumentCaptor<SoapActionCallback> soapActionCaptor;

  private CreateInvoiceClient client;

  private final String soaGatewayUserLoginId = "user";
  private final String soaGatewayUserRole = "EXTERNAL";

  @BeforeEach
  void setup() {
    this.client = new CreateInvoiceClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  private static InvoiceAddRQ.InvoiceDetails poaInvoiceDetails() {
    final InvoiceAddRQ.InvoiceDetails invoiceDetails = new InvoiceAddRQ.InvoiceDetails();
    final POAElementType poa = new POAElementType();
    poa.setCaseReferenceNumber("300000123456");
    invoiceDetails.getPOA().add(poa);
    return invoiceDetails;
  }

  private static JAXBElement<InvoiceAddRS> response(final StatusTextType status) {
    final InvoiceAddRS invoiceAddRs = new InvoiceAddRS();
    invoiceAddRs.setInvoiceReferenceID("invoice123");

    final HeaderRSType headerRs = new HeaderRSType();
    final Status statusRs = new Status();
    statusRs.setStatus(status);
    statusRs.setStatusFreeText("the failure reason");
    headerRs.setStatus(statusRs);
    invoiceAddRs.setHeaderRS(headerRs);

    return new ObjectFactory().createInvoiceAddRS(invoiceAddRs);
  }

  @Test
  @DisplayName("createInvoice - Successful SOAP Call")
  void testCreateInvoice_Success() {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(response(StatusTextType.SUCCESS));

    final InvoiceAddRS actualResponse =
        client.createInvoice(soaGatewayUserLoginId, soaGatewayUserRole, poaInvoiceDetails());

    assertEquals("invoice123", actualResponse.getInvoiceReferenceID());
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class));
  }

  @Test
  @DisplayName("createInvoice - Captures Request Details")
  void testCreateInvoice_CaptureRequest() {
    final InvoiceAddRQ.InvoiceDetails invoiceDetails = poaInvoiceDetails();

    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(response(StatusTextType.SUCCESS));

    client.createInvoice(soaGatewayUserLoginId, soaGatewayUserRole, invoiceDetails);

    verify(webServiceTemplate)
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));
    final InvoiceAddRQ capturedRequest = requestCaptor.getValue().getValue();

    assertNotNull(capturedRequest);
    assertEquals(invoiceDetails, capturedRequest.getInvoiceDetails());
    assertNotNull(capturedRequest.getHeaderRQ());
    assertEquals(soaGatewayUserLoginId, capturedRequest.getHeaderRQ().getUserLoginID());
    assertEquals(soaGatewayUserRole, capturedRequest.getHeaderRQ().getUserRole());
  }

  @Test
  @DisplayName("createInvoice - Sends The Service Name As The SoapAction")
  void testCreateInvoice_SoapAction() throws Exception {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(response(StatusTextType.SUCCESS));

    client.createInvoice(soaGatewayUserLoginId, soaGatewayUserRole, poaInvoiceDetails());

    verify(webServiceTemplate)
        .marshalSendAndReceive(eq(SERVICE_URL), any(JAXBElement.class), soapActionCaptor.capture());

    // SoapActionCallback exposes its action only by applying it to a message. The CreateInvoice
    // WSDL declares its soapAction as the bare service namespace, with no operation suffix.
    final SoapMessage soapMessage = mock(SoapMessage.class);
    soapActionCaptor.getValue().doWithMessage(soapMessage);

    verify(soapMessage).setSoapAction(SERVICE_NAME);
  }

  @Test
  @DisplayName("createInvoice - Unsuccessful SOA Status")
  void testCreateInvoice_UnsuccessfulStatus() {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(response(StatusTextType.ERROR));

    final InvoiceAddRQ.InvoiceDetails invoiceDetails = poaInvoiceDetails();

    final RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> client.createInvoice(soaGatewayUserLoginId, soaGatewayUserRole, invoiceDetails));

    assertEquals(
        "Failure in SOA call myService. Status Code: Error. Status Text: the failure reason",
        exception.getMessage());
  }

  @Test
  @DisplayName("createInvoice - SOAP Call Failure")
  void testCreateInvoice_Failure() {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenThrow(new RuntimeException("SOAP request failed"));

    final InvoiceAddRQ.InvoiceDetails invoiceDetails = poaInvoiceDetails();

    final RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> client.createInvoice(soaGatewayUserLoginId, soaGatewayUserRole, invoiceDetails));

    assertEquals("SOAP request failed", exception.getMessage());
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class));
  }
}
