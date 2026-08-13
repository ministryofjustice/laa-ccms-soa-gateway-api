package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.GetInvoiceDetailsRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.GetInvoiceDetailsRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.ObjectFactory;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.AssessmentResultsElementType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.OPAEntityType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.ReturnDataType;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.Status;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;

@ExtendWith(MockitoExtension.class)
class GetInvoiceDetailsClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";
  public static final String BILLING_ID = "1234567890";

  @Mock WebServiceTemplate webServiceTemplate;

  @Captor ArgumentCaptor<JAXBElement<GetInvoiceDetailsRQ>> requestCaptor;

  @Captor ArgumentCaptor<SoapActionCallback> soapActionCaptor;

  private GetInvoiceDetailsClient client;

  private final String soaGatewayUserLoginId = "user";
  private final String soaGatewayUserRole = "EXTERNAL";

  @BeforeEach
  void setup() {
    this.client = new GetInvoiceDetailsClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  private static JAXBElement<GetInvoiceDetailsRS> response(
      final StatusTextType status, final boolean withResults) {
    final GetInvoiceDetailsRS getInvoiceDetailsRs = new GetInvoiceDetailsRS();

    if (withResults) {
      final AssessmentResultsElementType results = new AssessmentResultsElementType();
      final OPAEntityType entity = new OPAEntityType();
      entity.setEntityName("global");
      results.getEntity().add(entity);
      getInvoiceDetailsRs.setAssessmentResultsElement(results);
    }

    final HeaderRSType headerRs = new HeaderRSType();
    final Status statusRs = new Status();
    statusRs.setStatus(status);
    statusRs.setStatusFreeText("the failure reason");
    headerRs.setStatus(statusRs);
    getInvoiceDetailsRs.setHeaderRS(headerRs);

    return new ObjectFactory().createGetInvoiceDetailsRS(getInvoiceDetailsRs);
  }

  @Test
  @DisplayName("getInvoiceData - Successful SOAP Call")
  void testGetInvoiceData_Success() {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(response(StatusTextType.SUCCESS, true));

    final GetInvoiceDetailsRS actualResponse =
        client.getInvoiceData(soaGatewayUserLoginId, soaGatewayUserRole, BILLING_ID);

    assertNotNull(actualResponse.getAssessmentResultsElement());
    assertEquals(1, actualResponse.getAssessmentResultsElement().getEntity().size());
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class));
  }

  @Test
  @DisplayName("getInvoiceData - Sends The Billing Id And BasicAssessmentData")
  void testGetInvoiceData_CaptureRequest() {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(response(StatusTextType.SUCCESS, true));

    client.getInvoiceData(soaGatewayUserLoginId, soaGatewayUserRole, BILLING_ID);

    verify(webServiceTemplate)
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));
    final GetInvoiceDetailsRQ capturedRequest = requestCaptor.getValue().getValue();

    assertNotNull(capturedRequest);
    assertEquals(BILLING_ID, capturedRequest.getSearchCondition().getBillingId());
    // BasicAssessmentData is the only value the schema permits.
    assertEquals(ReturnDataType.BASIC_ASSESSMENT_DATA, capturedRequest.getReturnData());
    assertNotNull(capturedRequest.getHeaderRQ());
    assertEquals(soaGatewayUserLoginId, capturedRequest.getHeaderRQ().getUserLoginID());
    assertEquals(soaGatewayUserRole, capturedRequest.getHeaderRQ().getUserRole());
  }

  @Test
  @DisplayName("getInvoiceData - Sends The Operation Name As The SoapAction")
  void testGetInvoiceData_SoapAction() throws Exception {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(response(StatusTextType.SUCCESS, true));

    client.getInvoiceData(soaGatewayUserLoginId, soaGatewayUserRole, BILLING_ID);

    verify(webServiceTemplate)
        .marshalSendAndReceive(eq(SERVICE_URL), any(JAXBElement.class), soapActionCaptor.capture());

    // Unlike CreateInvoice, this WSDL declares the bare operation name as the soapAction, not the
    // service namespace.
    final SoapMessage soapMessage = mock(SoapMessage.class);
    soapActionCaptor.getValue().doWithMessage(soapMessage);

    verify(soapMessage).setSoapAction("GetInvoiceData");
  }

  @Test
  @DisplayName("getInvoiceData - Returns No Assessment Results For An Unknown Billing Id")
  void testGetInvoiceData_NoResults() {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(response(StatusTextType.SUCCESS, false));

    final GetInvoiceDetailsRS actualResponse =
        client.getInvoiceData(soaGatewayUserLoginId, soaGatewayUserRole, BILLING_ID);

    assertNull(actualResponse.getAssessmentResultsElement());
  }

  @Test
  @DisplayName("getInvoiceData - Unsuccessful SOA Status")
  void testGetInvoiceData_UnsuccessfulStatus() {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(response(StatusTextType.ERROR, false));

    final RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> client.getInvoiceData(soaGatewayUserLoginId, soaGatewayUserRole, BILLING_ID));

    assertEquals(
        "Failure in SOA call myService. Status Code: Error. Status Text: the failure reason",
        exception.getMessage());
  }

  @Test
  @DisplayName("getInvoiceData - SOAP Call Failure")
  void testGetInvoiceData_Failure() {
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenThrow(new RuntimeException("SOAP request failed"));

    final RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> client.getInvoiceData(soaGatewayUserLoginId, soaGatewayUserRole, BILLING_ID));

    assertEquals("SOAP request failed", exception.getMessage());
  }
}
