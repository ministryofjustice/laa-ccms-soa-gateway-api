package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.ccms.soa.gateway.client.DocumentClient.NO_RELATED_NOTIFICATION;

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
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentDownloadRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentDownloadRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentUploadRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentUploadRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.DocumentUploadElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;

@ExtendWith(MockitoExtension.class)
class DocumentClientTest {

    public static final String SERVICE_NAME = "myService";
    public static final String SERVICE_URL = "myUrl";
    @Mock
    Logger mockLogger;

    @Mock
    WebServiceTemplate webServiceTemplate;

    @Captor
    ArgumentCaptor<JAXBElement<DocumentUploadRQ>> requestCaptorUpload;

    @Captor
    ArgumentCaptor<JAXBElement<DocumentDownloadRQ>> requestCaptorDownload;

    private DocumentClient client;

    private final String soaGatewayUserLoginId = "user";
    private final String soaGatewayUserRole = "EXTERNAL";

    @BeforeEach
    void setup() {
        this.client = new DocumentClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
    }

    @Test
    public void testRegisterDocumentBuildsRegisterDocumentRequest() throws Exception {
        ObjectFactory objectFactory = new ObjectFactory();

        // Mock the response of the WebServiceTemplate
        when(webServiceTemplate.marshalSendAndReceive(
                eq(SERVICE_URL),
                any(JAXBElement.class),
                any(SoapActionCallback.class)))
                .thenReturn(objectFactory.createDocumentUploadRS(new DocumentUploadRS()));

        DocumentUploadElementType documentUploadElementType = new DocumentUploadElementType();

        DocumentUploadRS response = client.registerDocument(
                soaGatewayUserLoginId, soaGatewayUserRole, documentUploadElementType, null);

        // Verify interactions
        verify(webServiceTemplate, times(1)).marshalSendAndReceive(
                eq(SERVICE_URL),
                requestCaptorUpload.capture(),
                any(SoapActionCallback.class));

        JAXBElement<DocumentUploadRQ> payload = requestCaptorUpload.getValue();
        assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
        assertEquals(soaGatewayUserLoginId, payload.getValue().getHeaderRQ().getUserLoginID());
        assertEquals(soaGatewayUserRole, payload.getValue().getHeaderRQ().getUserRole());
        assertEquals(documentUploadElementType, payload.getValue().getDocument());
        assertEquals(NO_RELATED_NOTIFICATION, payload.getValue().getNotificationID());
        assertNotNull(response);
    }

    @Test
    public void testRegisterDocumentForNotificationAttachmentBuildsRegisterDocumentRequest() throws Exception {
        ObjectFactory objectFactory = new ObjectFactory();

        // Mock the response of the WebServiceTemplate
        when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL),
            any(JAXBElement.class),
            any(SoapActionCallback.class)))
            .thenReturn(objectFactory.createDocumentUploadRS(new DocumentUploadRS()));

        DocumentUploadElementType documentUploadElementType = new DocumentUploadElementType();

        DocumentUploadRS response = client.registerDocument(
            soaGatewayUserLoginId, soaGatewayUserRole, documentUploadElementType, "12345");

        // Verify interactions
        verify(webServiceTemplate, times(1)).marshalSendAndReceive(
            eq(SERVICE_URL),
            requestCaptorUpload.capture(),
            any(SoapActionCallback.class));

        JAXBElement<DocumentUploadRQ> payload = requestCaptorUpload.getValue();
        assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
        assertEquals(soaGatewayUserLoginId, payload.getValue().getHeaderRQ().getUserLoginID());
        assertEquals(soaGatewayUserRole, payload.getValue().getHeaderRQ().getUserRole());
        assertEquals(documentUploadElementType, payload.getValue().getDocument());
        assertEquals("12345", payload.getValue().getNotificationID());
        assertNotNull(response);
    }

    @Test
    public void testUploadDocumentBuildsUploadRequest() throws Exception {
        ObjectFactory objectFactory = new ObjectFactory();

        final String notificationReference = "123";

        // Mock the response of the WebServiceTemplate
        when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL),
            any(JAXBElement.class),
            any(SoapActionCallback.class)))
            .thenReturn(objectFactory.createDocumentUploadRS(new DocumentUploadRS()));

        DocumentUploadElementType documentUploadElementType = new DocumentUploadElementType();


        DocumentUploadRS response = client.uploadDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            notificationReference,
            documentUploadElementType);

        // Verify interactions
        verify(webServiceTemplate, times(1)).marshalSendAndReceive(
            eq(SERVICE_URL),
            requestCaptorUpload.capture(),
            any(SoapActionCallback.class));

        JAXBElement<DocumentUploadRQ> payload = requestCaptorUpload.getValue();
        assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
        assertEquals(soaGatewayUserLoginId, payload.getValue().getHeaderRQ().getUserLoginID());
        assertEquals(soaGatewayUserRole, payload.getValue().getHeaderRQ().getUserRole());
        assertEquals(documentUploadElementType, payload.getValue().getDocument());
        assertEquals(notificationReference, payload.getValue().getNotificationID());
        assertNotNull(response);
    }

    @Test
    public void testDownloadDocumentBuildsRequest() throws Exception {
        ObjectFactory objectFactory = new ObjectFactory();

        final String documentId = "123";

        // Mock the response of the WebServiceTemplate
        DocumentDownloadRS documentDownloadRS = new DocumentDownloadRS();
        documentDownloadRS.setDocument(new DocumentElementType());

        when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL),
            any(JAXBElement.class),
            any(SoapActionCallback.class)))
            .thenReturn(objectFactory.createDocumentDownloadRS(documentDownloadRS));

        DocumentElementType response = client.downloadDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            documentId);

        // Verify interactions
        verify(webServiceTemplate, times(1)).marshalSendAndReceive(
            eq(SERVICE_URL),
            requestCaptorDownload.capture(),
            any(SoapActionCallback.class));

        JAXBElement<DocumentDownloadRQ> payload = requestCaptorDownload.getValue();
        assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
        assertEquals(soaGatewayUserLoginId, payload.getValue().getHeaderRQ().getUserLoginID());
        assertEquals(soaGatewayUserRole, payload.getValue().getHeaderRQ().getUserRole());
        assertEquals(documentId, payload.getValue().getDocumentID());
        assertEquals(documentDownloadRS.getDocument(), response);
    }
}