package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;
import static uk.gov.laa.ccms.soa.gateway.client.DocumentClient.NO_RELATED_NOTIFICATION;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentUploadRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.DocumentUploadElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;

@SpringBootTest
public class DocumentClientIntegrationTest {

  @Autowired
  private WebServiceTemplate webServiceTemplate;

  @Autowired
  private DocumentClient client;

  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/DocumentUploadRS_valid.xml")
  Resource documentUploadRS_valid;

  @Value("classpath:/payload/DocumentDownloadRS_valid.xml")
  Resource documentDownloadRS_valid;

  private static final String HEADER_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String MSG_NS = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIM";
  private static final String DOC_NS = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIO";
  private static final String COMMON_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";

  private String testLoginId;
  private String testUserType;

  private Map<String, String> namespaces;

  @BeforeEach
  public void createServer() {
    mockServer = MockWebServiceServer.createServer(webServiceTemplate);

    testLoginId = "testLogin";
    testUserType = "testType";

    namespaces = new HashMap<>();
    namespaces.put("header", HEADER_NS);
    namespaces.put("msg", MSG_NS);
    namespaces.put("doc", DOC_NS);
    namespaces.put("common", COMMON_NS);
  }

  @Test
  public void testRegisterDocument_ReturnsData() throws Exception {
    DocumentUploadElementType documentUploadElementType = buildDocumentUploadElementType();

    mockServer.expect(
            xpath("/msg:DocumentUploadRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andExpect(
            xpath("/msg:DocumentUploadRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
                testLoginId))
        .andExpect(xpath("/msg:DocumentUploadRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
            testUserType))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:NotificationID",
                namespaces)
                .evaluatesTo(NO_RELATED_NOTIFICATION))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:Document/doc:DocumentType", namespaces)
                .evaluatesTo(documentUploadElementType.getDocumentType()))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:Document/doc:FileExtension", namespaces)
                .evaluatesTo(documentUploadElementType.getFileExtension()))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:Document/doc:Text", namespaces)
                .evaluatesTo(documentUploadElementType.getText()))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:Document/doc:Channel", namespaces)
                .evaluatesTo(documentUploadElementType.getChannel()))
        .andRespond(withPayload(documentUploadRS_valid));

    DocumentUploadRS response = client.registerDocument(testLoginId, testUserType,
        documentUploadElementType, null);

    assertNotNull(response.getDocumentID());

    mockServer.verify();
  }

  @Test
  public void testUploadDocument_success() throws Exception {
    final String notificationReference = "notRef";

    DocumentUploadElementType documentUploadElementType = buildDocumentUploadElementType();

    mockServer.expect(
            xpath("/msg:DocumentUploadRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andExpect(
            xpath("/msg:DocumentUploadRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
                testLoginId))
        .andExpect(xpath("/msg:DocumentUploadRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
            testUserType))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:NotificationID",
                namespaces)
                .evaluatesTo(notificationReference))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:Document/doc:DocumentType", namespaces)
                .evaluatesTo(documentUploadElementType.getDocumentType()))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:Document/doc:FileExtension", namespaces)
                .evaluatesTo(documentUploadElementType.getFileExtension()))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:Document/doc:Text", namespaces)
                .evaluatesTo(documentUploadElementType.getText()))
        .andExpect(
            xpath("/msg:DocumentUploadRQ/msg:Document/doc:Channel", namespaces)
                .evaluatesTo(documentUploadElementType.getChannel()))
        .andRespond(withPayload(documentUploadRS_valid));

    DocumentUploadRS response = client.uploadDocument(
        testLoginId,
        testUserType,
        notificationReference,
        documentUploadElementType);

    assertNotNull(response.getDocumentID());

    mockServer.verify();
  }

  @Test
  public void testDownloadDocument_success() throws Exception {
    final String documentId = "123";

    DocumentUploadElementType documentUploadElementType = buildDocumentUploadElementType();

    mockServer.expect(
            xpath("/msg:DocumentDownloadRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andExpect(
            xpath("/msg:DocumentDownloadRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
                testLoginId))
        .andExpect(xpath("/msg:DocumentDownloadRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
            testUserType))
        .andExpect(
            xpath("/msg:DocumentDownloadRQ/msg:DocumentID",
                namespaces)
                .evaluatesTo(documentId))
        .andRespond(withPayload(documentDownloadRS_valid));

    DocumentElementType response = client.downloadDocument(
        testLoginId,
        testUserType,
        documentId);

    assertEquals(documentId, response.getDocumentID());
    assertNotNull(response.getBinData());
    assertEquals("E", response.getChannel());
    assertEquals("alink", response.getDocumentLink());
    assertEquals("pdf", response.getFileExtension());
    assertEquals("An evidence document", response.getText());

    mockServer.verify();
  }


  private DocumentUploadElementType buildDocumentUploadElementType() {
    DocumentUploadElementType documentUploadElementType = new DocumentUploadElementType();
    documentUploadElementType.setDocumentType("type");
    documentUploadElementType.setFileExtension("ext");
    documentUploadElementType.setText("description");
    documentUploadElementType.setChannel("E");

    return documentUploadElementType;
  }
}
