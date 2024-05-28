package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.laa.ccms.soa.gateway.model.BaseDocument;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.laa.ccms.soa.gateway.service.DocumentService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebAppConfiguration
class DocumentControllerTest {
    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    public void testRegisterDocument_Success() throws Exception {
        final String soaGatewayUserLoginId = "user";
        final String soaGatewayUserRole = "EXTERNAL";
        BaseDocument baseDocument = new BaseDocument();
        ClientTransactionResponse clientTransactionResponse = new ClientTransactionResponse()
            .transactionId("trans123")
            .referenceNumber("doc123");

        when(documentService.registerDocument(soaGatewayUserLoginId, soaGatewayUserRole, baseDocument))
            .thenReturn(clientTransactionResponse);

        mockMvc.perform(
                post("/documents")
                    .content(new ObjectMapper().writeValueAsString(baseDocument))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                    .header("SoaGateway-User-Role", soaGatewayUserRole))
            .andExpect(status().isOk());

        verify(documentService).registerDocument(soaGatewayUserLoginId, soaGatewayUserRole, baseDocument);
    }

    @Test
    public void testUploadDocument_Success() throws Exception {
        final String soaGatewayUserLoginId = "user";
        final String soaGatewayUserRole = "EXTERNAL";
        final String documentId = "123";

        Document document = new Document();
        ClientTransactionResponse clientTransactionResponse = new ClientTransactionResponse()
            .transactionId("trans123")
            .referenceNumber(documentId);

        when(documentService.uploadDocument(soaGatewayUserLoginId, soaGatewayUserRole, documentId, document))
            .thenReturn(clientTransactionResponse);

        mockMvc.perform(
                put("/documents/{document-id}", clientTransactionResponse.getReferenceNumber())
                    .content(new ObjectMapper().writeValueAsString(document))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                    .header("SoaGateway-User-Role", soaGatewayUserRole))
            .andExpect(status().isOk());

        verify(documentService).uploadDocument(soaGatewayUserLoginId, soaGatewayUserRole, documentId, document);
    }

    @Test
    public void testDownloadDocument_Success() throws Exception {
        final String soaGatewayUserLoginId = "user";
        final String soaGatewayUserRole = "EXTERNAL";
        final String documentId = "doc123";

        Document document = new Document()
            .documentId(documentId);

        when(documentService.downloadDocument(soaGatewayUserLoginId, soaGatewayUserRole, documentId))
            .thenReturn(document);

        mockMvc.perform(
                get("/documents/{document-id}", documentId)
                    .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                    .header("SoaGateway-User-Role", soaGatewayUserRole))
            .andExpect(status().isOk());

        verify(documentService).downloadDocument(soaGatewayUserLoginId, soaGatewayUserRole, documentId);
    }
}