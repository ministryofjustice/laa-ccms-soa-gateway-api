package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withError;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;

@SpringBootTest
@ContextConfiguration
public class NotificationClientIntegrationTest {

  @Autowired
  private WebServiceTemplate webServiceTemplate;

  @Autowired
  private NotificationClient client;

  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/NotificationCntInqRS_valid.xml")
  Resource notificationCntInqRS_valid;

  private static final String HEADER_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String CASE_NS = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIM";
  private static final String COMMON_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";

  @BeforeEach
  public void createServer() {
    mockServer = MockWebServiceServer.createServer(webServiceTemplate);
  }

  @Test
  public void testGetNotificationCount_ReturnsData() throws Exception {
    final String testLoginId = "testLogin";
    final String testUserType = "testType";
    final String searchLoginId = "searchLogin";

    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("ns2", HEADER_NS);
    namespaces.put("ns4", COMMON_NS);
    namespaces.put("ns5", CASE_NS);

    mockServer.expect(xpath("/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:TimeStamp", namespaces).exists())
        .andExpect(xpath("/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:TransactionRequestID", namespaces).exists())
        .andExpect(xpath("/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:UserLoginID", namespaces).evaluatesTo(testLoginId))
        .andExpect(xpath("/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:UserRole", namespaces).evaluatesTo(testUserType))
        .andExpect(xpath("/ns5:NotificationCntInqRQ/ns5:RecordCount/ns4:MaxRecordsToFetch", namespaces).evaluatesTo(10))
        .andExpect(xpath("/ns5:NotificationCntInqRQ/ns5:SearchCriteria/ns5:UserID", namespaces).evaluatesTo(searchLoginId))
        .andRespond(withPayload(notificationCntInqRS_valid));

    NotificationCntInqRS response = client.getNotificationCount(searchLoginId, testLoginId, testUserType,
        10);

    assertNotNull(response.getNotificationCntLists());
    assertEquals("10", response.getNotificationCntLists().getNotificationsCnt().get(0).getNotificationCount());
    assertEquals("20", response.getNotificationCntLists().getNotificationsCnt().get(0).getOverdueActionCount());
    assertEquals("30", response.getNotificationCntLists().getNotificationsCnt().get(0).getStandardActionCount());

    mockServer.verify();
  }

  @Test
  public void testGetNotificationCount_HandlesError() {
    final String searchLoginId = "searchLogin";
    final String testLoginId = "testLogin";
    final String testUserType = "testType";

    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("ns2", HEADER_NS);
    namespaces.put("ns4", COMMON_NS);
    namespaces.put("ns5", CASE_NS);

    mockServer.expect(xpath("/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:TransactionRequestID", namespaces).exists())
        .andRespond(withError("Failed to call soap service"));

    assertThrows(RuntimeException.class, () -> client.getNotificationCount(searchLoginId, testLoginId, testUserType,
        10));

    mockServer.verify();
  }
}
