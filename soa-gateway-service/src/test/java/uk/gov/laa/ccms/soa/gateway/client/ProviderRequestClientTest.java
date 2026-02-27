package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.slf4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ProviderRequestAddRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ProviderRequestAddRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderRequestElementType;

@ExtendWith(MockitoExtension.class)
class ProviderRequestClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";

  @Mock Logger mockLogger;

  @Mock WebServiceTemplate webServiceTemplate;

  @Captor ArgumentCaptor<JAXBElement<ProviderRequestAddRQ>> requestCaptor;

  private ProviderRequestClient client;

  private final String soaGatewayUserLoginId = "user";
  private final String soaGatewayUserRole = "EXTERNAL";

  @BeforeEach
  void setup() {
    this.client = new ProviderRequestClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  @Test
  @DisplayName("submitProviderRequest - Successful SOAP Call")
  void testSubmitProviderRequest_Success() {
    final ObjectFactory objectFactory = new ObjectFactory();

    final ProviderRequestElementType providerRequestElementType = new ProviderRequestElementType();
    final ProviderRequestAddRS mockResponse = new ProviderRequestAddRS();
    mockResponse.setNotificationID("notif123");

    final JAXBElement<ProviderRequestAddRS> responseJaxbElement =
        objectFactory.createProviderRequestAddRS(mockResponse);

    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(responseJaxbElement);

    final ProviderRequestAddRS actualResponse =
        client.submitProviderRequest(
            soaGatewayUserLoginId, soaGatewayUserRole, providerRequestElementType);

    assertEquals("notif123", actualResponse.getNotificationID());
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class));
  }

  @Test
  @DisplayName("submitProviderRequest - Captures Request Details")
  void testSubmitProviderRequest_CaptureRequest() {
    final ObjectFactory objectFactory = new ObjectFactory();

    final ProviderRequestElementType providerRequestElementType = new ProviderRequestElementType();
    final ProviderRequestAddRS mockResponse = new ProviderRequestAddRS();
    mockResponse.setNotificationID("notif123");

    final JAXBElement<ProviderRequestAddRS> responseJaxbElement =
        objectFactory.createProviderRequestAddRS(mockResponse);

    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(responseJaxbElement);

    client.submitProviderRequest(
        soaGatewayUserLoginId, soaGatewayUserRole, providerRequestElementType);

    verify(webServiceTemplate)
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));
    final ProviderRequestAddRQ capturedRequest = requestCaptor.getValue().getValue();

    assertNotNull(capturedRequest);
    assertEquals(providerRequestElementType, capturedRequest.getRequestDetails());
  }

  @Test
  @DisplayName("submitProviderRequest - SOAP Call Failure")
  void testSubmitProviderRequest_Failure() {
    final ProviderRequestElementType providerRequestElementType = new ProviderRequestElementType();

    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenThrow(new RuntimeException("SOAP request failed"));

    final RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () ->
                client.submitProviderRequest(
                    soaGatewayUserLoginId, soaGatewayUserRole, providerRequestElementType));

    assertEquals("SOAP request failed", exception.getMessage());
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class));
  }
}
