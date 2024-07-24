package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.nio.charset.StandardCharsets;
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

@SpringBootTest
public class CoverSheetClientIntegrationTest {

  @Autowired
  private WebServiceTemplate webServiceTemplate;

  @Autowired
  private CoverSheetClient client;

  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/DocumentCoverRS_valid.xml")
  Resource documentCoverRS_valid;

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
  public void testDownloadDocumentCoverSheet_ReturnsData() throws Exception {

    String documentId = "12345";

    mockServer.expect(
            xpath("/msg:DocumentCoverRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andExpect(
            xpath("/msg:DocumentCoverRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
                testLoginId))
        .andExpect(xpath("/msg:DocumentCoverRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
            testUserType))
        .andExpect(
            xpath("/msg:DocumentCoverRQ/msg:ExpectedDocumentID",
                namespaces)
                .evaluatesTo(documentId))
        .andRespond(withPayload(documentCoverRS_valid));

    byte[] response = client.downloadDocumentCoverSheet(testLoginId, testUserType,
        documentId);

    assertNotNull(response);
    assertEquals("cover-sheet-content", new String(response, StandardCharsets.UTF_8));

    mockServer.verify();
  }

}
