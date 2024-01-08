package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.slf4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseInfo;

@ExtendWith(MockitoExtension.class)
class CaseServicesClientTest {

    public static final String SERVICE_NAME = "myService";
    public static final String SERVICE_URL = "myUrl";
    private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
    private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";
    private static final Integer MAX_RECORDS = 50;

    @Mock
    Logger mockLogger;

    @Mock
    WebServiceTemplate webServiceTemplate;

    @Captor
    ArgumentCaptor<JAXBElement<CaseInqRQ>> requestCaptor;

    private CaseServicesClient client;

    @BeforeEach
    void setup() {
        this.client = new CaseServicesClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
    }


    @Test
    public void testGetCaseDetailsBuildsCorrectRequest() {
        ObjectFactory objectFactory = new ObjectFactory();

        // Mock the response of the WebServiceTemplate
        when(webServiceTemplate.marshalSendAndReceive(
                eq(SERVICE_URL),
                any(JAXBElement.class),
                any(SoapActionCallback.class)))
                .thenReturn(objectFactory.createCaseInqRS(new CaseInqRS()));

        CaseInfo caseInfo = buildCaseInfo();


        CaseInqRS response = client.getCaseDetails(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, MAX_RECORDS, caseInfo
        );

        // Verify interactions
        verify(webServiceTemplate, times(1)).marshalSendAndReceive(
                eq(SERVICE_URL),
                requestCaptor.capture(),
                any(SoapActionCallback.class));

        JAXBElement<CaseInqRQ> payload = requestCaptor.getValue();
        assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
        assertEquals(SOA_GATEWAY_USER_LOGIN_ID, payload.getValue().getHeaderRQ().getUserLoginID());
        assertEquals(SOA_GATEWAY_USER_ROLE, payload.getValue().getHeaderRQ().getUserRole());
        CaseInfo payloadCaseInfo = payload.getValue().getSearchCriteria().getCaseInfo();
        assertEquals(caseInfo.getCaseStatus(), payloadCaseInfo.getCaseStatus());
        assertEquals(caseInfo.getCaseReferenceNumber(), payloadCaseInfo.getCaseReferenceNumber());
        assertEquals(caseInfo.getClientSurname(), payloadCaseInfo.getClientSurname());
        assertEquals(caseInfo.getProviderCaseReferenceNumber(), payloadCaseInfo.getProviderCaseReferenceNumber());
        assertEquals(caseInfo.getOfficeID(), payloadCaseInfo.getOfficeID());
        assertEquals(caseInfo.getFeeEarnerContactID(), payloadCaseInfo.getFeeEarnerContactID());
        assertNotNull(response);
    }

    @Test
    public void testGetCaseDetailBuildsCorrectRequest() {
        ObjectFactory objectFactory = new ObjectFactory();

        // Mock the response of the WebServiceTemplate
        when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL),
            any(JAXBElement.class),
            any(SoapActionCallback.class)))
            .thenReturn(objectFactory.createCaseInqRS(new CaseInqRS()));

        CaseInqRS response = client.getCaseDetail(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, "123"
        );

        // Verify interactions
        verify(webServiceTemplate, times(1)).marshalSendAndReceive(
            eq(SERVICE_URL),
            requestCaptor.capture(),
            any(SoapActionCallback.class));

        JAXBElement<CaseInqRQ> payload = requestCaptor.getValue();
        assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
        assertEquals(SOA_GATEWAY_USER_LOGIN_ID, payload.getValue().getHeaderRQ().getUserLoginID());
        assertEquals(SOA_GATEWAY_USER_ROLE, payload.getValue().getHeaderRQ().getUserRole());
        assertNull(payload.getValue().getSearchCriteria().getCaseInfo());
        assertEquals("123", payload.getValue().getSearchCriteria().getCaseReferenceNumber());
        assertNotNull(response);
    }

    @Test
    public void testGetCaseStatusBuildsCorrectRequest() throws Exception {
        // Mocking expected values
        String transactionId = "txn12345";

        uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory objectFactory =
            new uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory();
        CaseAddUpdtStatusRS mockResponse = objectFactory.createCaseAddUpdtStatusRS();

        when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL),
            any(JAXBElement.class),
            any(SoapActionCallback.class)))
            .thenReturn(objectFactory.createCaseAddUpdtStatusRS(mockResponse));

        CaseAddUpdtStatusRS response = client.getCaseTransactionStatus(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, transactionId
        );

        // Verify interactions
        verify(webServiceTemplate, times(1)).marshalSendAndReceive(
            eq(SERVICE_URL),
            requestCaptor.capture(),
            any(SoapActionCallback.class));

        JAXBElement<?> payload = requestCaptor.getValue();
        assertEquals(transactionId, ((CaseAddUpdtStatusRQ) payload.getValue()).getTransactionID());
        assertNotNull(response);
    }

    private CaseInfo buildCaseInfo(){
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setCaseReferenceNumber("caseref");
        caseInfo.setCaseStatus("casestatus");
        caseInfo.setClientSurname("asurname");
        caseInfo.setProviderCaseReferenceNumber("provcaseref");
        caseInfo.setFeeEarnerContactID("123");
        return caseInfo;
    }
}