package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.DocumentClient;
import uk.gov.laa.ccms.soa.gateway.mapper.CommonMapper;
import uk.gov.laa.ccms.soa.gateway.mapper.DocumentMapper;
import uk.gov.laa.ccms.soa.gateway.model.BaseDocument;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentUploadRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.DocumentUploadElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentClient documentClient;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private CommonMapper commonMapper;

    @InjectMocks
    private DocumentService documentService;

    @Test
    public void testRegisterDocument() {
        String soaGatewayUserLoginId = "soaGatewayUserLoginId";
        String soaGatewayUserRole = "soaGatewayUserRole";
        BaseDocument baseDocument = new BaseDocument();
        DocumentUploadElementType documentUploadElementType = new DocumentUploadElementType();
        DocumentUploadRS documentUploadRS = new DocumentUploadRS();
        ClientTransactionResponse clientTransactionResponse = new ClientTransactionResponse();

        when(documentMapper.toDocumentUploadElementType(baseDocument))
            .thenReturn(documentUploadElementType);

        // Stub the Client to return the mocked response
        when(documentClient.registerDocument(
            soaGatewayUserLoginId, soaGatewayUserRole, documentUploadElementType))
                .thenReturn(documentUploadRS);

        // Stub the Mapper to return the mocked gateway response
        when(documentMapper.toClientTransactionResponse(eq(documentUploadRS)))
            .thenReturn(clientTransactionResponse);

        // Call the service method
        ClientTransactionResponse result = documentService.registerDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            baseDocument);

        // Verify that the NotificationClient method was called with the expected arguments
        verify(documentClient).registerDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            documentUploadElementType);

        // Verify that the map function was called with the mocked response
        verify(documentMapper).toClientTransactionResponse(documentUploadRS);

        // Assert the result
        assertEquals(clientTransactionResponse, result);
    }

    @Test
    public void testUploadDocument() {
        String soaGatewayUserLoginId = "soaGatewayUserLoginId";
        String soaGatewayUserRole = "soaGatewayUserRole";
        final String documentId = "docId123";

        Document document = new Document();
        DocumentUploadElementType documentUploadElementType = new DocumentUploadElementType();
        DocumentUploadRS documentUploadRS = new DocumentUploadRS();
        ClientTransactionResponse clientTransactionResponse = new ClientTransactionResponse();

        when(documentMapper.toDocumentUploadElementType(documentId, document))
            .thenReturn(documentUploadElementType);

        // Stub the Client to return the mocked response
        when(documentClient.registerDocument(
            soaGatewayUserLoginId, soaGatewayUserRole, documentUploadElementType))
            .thenReturn(documentUploadRS);

        // Stub the Mapper to return the mocked gateway response
        when(documentMapper.toClientTransactionResponse(eq(documentUploadRS)))
            .thenReturn(clientTransactionResponse);

        // Call the service method
        ClientTransactionResponse result = documentService.uploadDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            documentId,
            document);

        // Verify that the NotificationClient method was called with the expected arguments
        verify(documentClient).registerDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            documentUploadElementType);

        // Verify that the map function was called with the mocked response
        verify(documentMapper).toClientTransactionResponse(documentUploadRS);

        // Assert the result
        assertEquals(clientTransactionResponse, result);
    }

    @Test
    public void testDownloadDocument() {
        String soaGatewayUserLoginId = "soaGatewayUserLoginId";
        String soaGatewayUserRole = "soaGatewayUserRole";
        String documentId = "123";
        DocumentElementType documentElementType = new DocumentElementType();
        Document document = new Document();

        // Stub the Client to return the mocked response
        when(documentClient.downloadDocument(
            soaGatewayUserLoginId, soaGatewayUserRole, documentId))
            .thenReturn(documentElementType);

        // Stub the Mapper to return the mocked gateway response
        when(commonMapper.toDocument(eq(documentElementType)))
            .thenReturn(document);

        // Call the service method
        Document result = documentService.downloadDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            documentId);

        // Verify that the NotificationClient method was called with the expected arguments
        verify(documentClient).downloadDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            documentId);

        // Verify that the map function was called with the mocked response
        verify(commonMapper).toDocument(documentElementType);

        // Assert the result
        assertEquals(document, result);
    }
}

