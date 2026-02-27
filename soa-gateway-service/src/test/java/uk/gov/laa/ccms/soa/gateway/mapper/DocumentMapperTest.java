package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.model.BaseDocument;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentUploadRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.DocumentUploadElementType;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;

@ExtendWith(MockitoExtension.class)
public class DocumentMapperTest {

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  CommonMapper commonMapper;

  @InjectMocks DocumentMapper documentMapper = new DocumentMapperImpl();

  @Test
  public void testToClientTransactionResponse() {
    // Create a mocked instance of soa response object
    DocumentUploadRS documentUploadRS = new DocumentUploadRS();
    documentUploadRS.setDocumentID("docId");
    documentUploadRS.setHeaderRS(new HeaderRSType());
    documentUploadRS.getHeaderRS().setTransactionID("transId");

    ClientTransactionResponse result = documentMapper.toClientTransactionResponse(documentUploadRS);

    assertNotNull(result);
    assertEquals(documentUploadRS.getDocumentID(), result.getReferenceNumber());
    assertEquals(documentUploadRS.getHeaderRS().getTransactionID(), result.getTransactionId());
  }

  @Test
  public void testToDocumentUploadElementType() {
    final String documentId = "thedocid";

    // Create a mocked instance of soa response object
    Document document = new Document();
    document.setFileData("dGhlIGZpbGUgZGF0YQ==");
    document.setDocumentId("docId");
    document.setDocumentType("docType");
    document.setFileExtension("fileExt");
    document.setText("thetext");
    document.setChannel("E");

    DocumentUploadElementType result =
        documentMapper.toDocumentUploadElementType(documentId, document);

    assertNotNull(result);
    assertEquals(document.getFileData(), Base64.getEncoder().encodeToString(result.getBinData()));
    assertEquals(documentId, result.getCCMSDocumentID());
    assertEquals("E", result.getChannel());
    assertEquals(document.getDocumentLink(), result.getDocumentLink());
    assertEquals(document.getDocumentType(), result.getDocumentType());
    assertEquals(document.getFileExtension(), result.getFileExtension());
    assertEquals(document.getText(), result.getText());
  }

  @Test
  public void testToDocumentUploadElementType_noId() {
    // Create a mocked instance of soa response object
    Document document = new Document();
    document.setFileData("dGhlIGZpbGUgZGF0YQ==");
    document.setDocumentId("docId");
    document.setDocumentType("docType");
    document.setFileExtension("fileExt");
    document.setText("thetext");
    document.setChannel("E");

    DocumentUploadElementType result = documentMapper.toDocumentUploadElementType(document);

    assertNotNull(result);
    assertNull(result.getCCMSDocumentID());
    assertEquals(document.getFileData(), Base64.getEncoder().encodeToString(result.getBinData()));
    assertEquals("E", result.getChannel());
    assertEquals(document.getDocumentLink(), result.getDocumentLink());
    assertEquals(document.getDocumentType(), result.getDocumentType());
    assertEquals(document.getFileExtension(), result.getFileExtension());
    assertEquals(document.getText(), result.getText());
  }

  @Test
  public void testToDocumentUploadElementTypeBase() {
    // Create a mocked instance of soa response object
    BaseDocument document = new BaseDocument();
    document.setDocumentType("docType");
    document.setFileExtension("fileExt");
    document.setText("thetext");

    DocumentUploadElementType result = documentMapper.toDocumentUploadElementType(document);

    assertNotNull(result);
    assertEquals("E", result.getChannel());
    assertEquals(document.getDocumentType(), result.getDocumentType());
    assertEquals(document.getFileExtension(), result.getFileExtension());
    assertEquals(document.getText(), result.getText());
  }
}
