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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.CoverSheet;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.laa.ccms.soa.gateway.service.DocumentService;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {
  @Mock private DocumentService documentService;

  @InjectMocks private DocumentController documentController;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
  }

  @Test
  public void testRegisterDocument_Success() throws Exception {
    final String soaGatewayUserLoginId = "user";
    final String soaGatewayUserRole = "EXTERNAL";
    Document document = new Document();
    ClientTransactionResponse clientTransactionResponse =
        new ClientTransactionResponse().transactionId("trans123").referenceNumber("doc123");

    when(documentService.registerDocument(
            soaGatewayUserLoginId, soaGatewayUserRole, document, null, null))
        .thenReturn(clientTransactionResponse);

    mockMvc
        .perform(
            post("/documents")
                .content(new ObjectMapper().writeValueAsString(document))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole))
        .andExpect(status().isOk());

    verify(documentService)
        .registerDocument(soaGatewayUserLoginId, soaGatewayUserRole, document, null, null);
  }

  @ParameterizedTest
  @CsvSource({
    "CASE-12345, NOTIF-98765", // Both case reference and notification reference provided
    "CASE-12345, ", // Only case reference provided
    ", NOTIF-98765", // Only notification reference provided
    ", " // Neither case reference nor notification reference provided
  })
  public void testUploadDocument_Success(
      final String caseReference, final String notificationReference) throws Exception {
    final String soaGatewayUserLoginId = "user";
    final String soaGatewayUserRole = "EXTERNAL";
    final String documentId = "123";

    final Document document = new Document();
    final ClientTransactionResponse clientTransactionResponse =
        new ClientTransactionResponse().transactionId("trans123").referenceNumber(documentId);

    // Mocking the service call with the dynamic case reference and notification reference
    when(documentService.uploadDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            documentId,
            document,
            notificationReference,
            caseReference))
        .thenReturn(clientTransactionResponse);

    // Perform the mockMvc request with conditional headers based on case and notification
    // references
    mockMvc
        .perform(
            put("/documents/{document-id}", clientTransactionResponse.getReferenceNumber())
                .content(new ObjectMapper().writeValueAsString(document))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole)
                .param("notification-reference", notificationReference)
                .param("case-reference-number", caseReference))
        .andExpect(status().isOk());

    // Verifying the service call with the appropriate parameters including case and notification
    // references
    verify(documentService)
        .uploadDocument(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            documentId,
            document,
            notificationReference,
            caseReference);
  }

  @Test
  public void testDownloadDocument_Success() throws Exception {
    final String soaGatewayUserLoginId = "user";
    final String soaGatewayUserRole = "EXTERNAL";
    final String documentId = "doc123";

    Document document = new Document().documentId(documentId);

    when(documentService.downloadDocument(soaGatewayUserLoginId, soaGatewayUserRole, documentId))
        .thenReturn(document);

    mockMvc
        .perform(
            get("/documents/{document-id}", documentId)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole))
        .andExpect(status().isOk());

    verify(documentService).downloadDocument(soaGatewayUserLoginId, soaGatewayUserRole, documentId);
  }

  @Test
  public void testDownloadDocumentCoverSheet_Success() throws Exception {
    final String soaGatewayUserLoginId = "user";
    final String soaGatewayUserRole = "EXTERNAL";
    final String documentId = "doc123";

    CoverSheet coverSheet = new CoverSheet().documentId(documentId);

    when(documentService.downloadDocumentCoverSheet(
            soaGatewayUserLoginId, soaGatewayUserRole, documentId))
        .thenReturn(coverSheet);

    mockMvc
        .perform(
            get("/documents/{document-id}/cover-sheet", documentId)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole))
        .andExpect(status().isOk());

    verify(documentService)
        .downloadDocumentCoverSheet(soaGatewayUserLoginId, soaGatewayUserRole, documentId);
  }
}
