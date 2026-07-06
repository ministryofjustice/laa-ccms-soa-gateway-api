package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.CaseSOAInqRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.CaseSOAInqRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.ObjectFactory;

@ExtendWith(MockitoExtension.class)
class CaseStatementOfAccountClientTest {

  public static final String SERVICE_NAME =
      "http://legalservices.gov.uk/CCMS/Finance/Payables/1.0/GetCaseStmtOfAccount";
  public static final String SERVICE_URL = "myUrl";
  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";

  @Mock WebServiceTemplate webServiceTemplate;

  @Captor ArgumentCaptor<JAXBElement<CaseSOAInqRQ>> requestCaptor;

  @Captor ArgumentCaptor<SoapActionCallback> soapActionCaptor;

  private CaseStatementOfAccountClient client;

  @BeforeEach
  void setup() {
    this.client = new CaseStatementOfAccountClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  @Test
  public void getCaseStatementOfAccountBuildsCorrectRequest() {
    ObjectFactory objectFactory = new ObjectFactory();

    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createCaseSOAInqRS(new CaseSOAInqRS()));

    CaseSOAInqRS response =
        client.getCaseStatementOfAccount(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, "300000123456");

    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), soapActionCaptor.capture());

    JAXBElement<CaseSOAInqRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(SOA_GATEWAY_USER_LOGIN_ID, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(SOA_GATEWAY_USER_ROLE, payload.getValue().getHeaderRQ().getUserRole());
    assertEquals("300000123456", payload.getValue().getCaseReferenceNumber());
    assertNotNull(payload.getValue().getRecordCount());
    // The Finance WSDL's soapAction is the bare service namespace (operation is generically
    // named "process"), so the callback is constructed with the service name, no operation suffix.
    assertNotNull(soapActionCaptor.getValue());
    assertNotNull(response);
  }
}
