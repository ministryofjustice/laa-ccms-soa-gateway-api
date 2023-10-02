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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRS;

/**
 * Integration tests for {@link NotificationClient}.
 */
@SpringBootTest
@EnableAutoConfiguration(exclude = {
    org.springdoc.core.configuration.SpringDocSecurityConfiguration.class
})
public class NotificationClientIntegrationTest {

  private static final String HEADER_NS
      = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String CASE_NS
      = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIM";
  private static final String COMMON_NS
      = "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";
  private static MockWebServiceServer mockServer;
  @Value("classpath:/payload/NotificationCntInqRS_valid.xml")
  Resource notificationCntInqRs_valid;
  @Value("classpath:/payload/NotificationInqRS_valid.xml")
  Resource notificationInqRs_valid;
  @Autowired
  private WebServiceTemplate webServiceTemplate;
  @Autowired
  private NotificationClient client;

  private String testLoginId;

  private String testUserType;

  @BeforeEach
  public void createServer() {
    mockServer = MockWebServiceServer.createServer(webServiceTemplate);
    testLoginId = "testLogin";
    testUserType = "testType";
  }

  @Test
  public void testGetNotificationCount_ReturnsData() throws Exception {
  final String searchLoginId = "searchLogin";

    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("ns2", HEADER_NS);
    namespaces.put("ns4", COMMON_NS);
    namespaces.put("ns5", CASE_NS);

    mockServer.expect(
            xpath("/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:TimeStamp",
                namespaces).exists())
        .andExpect(xpath(
            "/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:TransactionRequestID",
            namespaces).exists())
        .andExpect(xpath(
                "/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:UserLoginID",
                namespaces).evaluatesTo(testLoginId))
        .andExpect(xpath(
                "/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:UserRole", namespaces)
                .evaluatesTo(testUserType))
        .andExpect(xpath(
            "/ns5:NotificationCntInqRQ/ns5:RecordCount/ns4:MaxRecordsToFetch",
            namespaces).evaluatesTo(10))
        .andExpect(xpath("/ns5:NotificationCntInqRQ/ns5:SearchCriteria/ns5:UserID",
            namespaces).evaluatesTo(searchLoginId))
        .andRespond(withPayload(notificationCntInqRs_valid));

    NotificationCntInqRS response = client.getNotificationCount(searchLoginId, testLoginId,
        testUserType,
        10);

    assertNotNull(response.getNotificationCntLists());
    assertEquals("10",
        response.getNotificationCntLists().getNotificationsCnt().get(0).getNotificationCount());
    assertEquals("20",
        response.getNotificationCntLists().getNotificationsCnt().get(0).getOverdueActionCount());
    assertEquals("30",
        response.getNotificationCntLists().getNotificationsCnt().get(0).getStandardActionCount());

    mockServer.verify();
  }

  @Test
  public void testGetNotificationCount_HandlesError() {
    final String searchLoginId = "searchLogin";

    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("ns2", HEADER_NS);
    namespaces.put("ns4", COMMON_NS);
    namespaces.put("ns5", CASE_NS);

    mockServer.expect(xpath(
        "/ns5:NotificationCntInqRQ/ns2:HeaderRQ/ns2:TransactionRequestID",
            namespaces).exists())
        .andRespond(withError("Failed to call soap service"));

    assertThrows(RuntimeException.class,
        () -> client.getNotificationCount(searchLoginId, testLoginId, testUserType,
            10));

    mockServer.verify();
  }

  @Test
  public void testGetNotifications_ReturnsData() throws Exception {
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("ns2", HEADER_NS);
    namespaces.put("ns4", COMMON_NS);
    namespaces.put("ns5", CASE_NS);

    mockServer.expect(
            xpath(
                "/ns5:NotificationInqRQ/ns2:HeaderRQ/ns2:TimeStamp",
                namespaces).exists())
        .andExpect(xpath(
            "/ns5:NotificationInqRQ/ns2:HeaderRQ/ns2:TransactionRequestID",
            namespaces).exists())
        .andExpect(xpath(
            "/ns5:NotificationInqRQ/ns2:HeaderRQ/ns2:UserLoginID",
            namespaces).evaluatesTo(testLoginId))
        .andExpect(xpath(
            "/ns5:NotificationInqRQ/ns2:HeaderRQ/ns2:UserRole",
            namespaces).evaluatesTo(testUserType))
        .andExpect(xpath(
            "/ns5:NotificationInqRQ/ns5:RecordCount/ns4:MaxRecordsToFetch", namespaces)
            .evaluatesTo(10))
        .andExpect(xpath(
            "/ns5:NotificationInqRQ/ns5:SearchCriteria/ns5:AssignedToUserID",
            namespaces).evaluatesTo(testLoginId))
        .andRespond(withPayload(notificationInqRs_valid));

    NotificationInqRS response = client.getNotifications(
        new uk.gov.laa.ccms.soa.gateway.model.Notification()
            .user(
                new uk.gov.laa.ccms.soa.gateway.model.UserDetail()
                    .userLoginId(testLoginId)
                    .userType(testUserType)
                    .userName(testLoginId)
            )
            .caseReferenceNumber(null),
        false, null, null,
        10);

    assertNotNull(response.getNotificationList());
    assertEquals(1, response.getNotificationList().getNotifications().size());
    assertEquals("300001416143",
        response.getNotificationList().getNotifications().get(0).getCaseReferenceNumber());
    assertEquals("ANDREA SPROU",
        response.getNotificationList().getNotifications().get(0).getFeeEarner());
    assertEquals("Action Reqd",
        response.getNotificationList().getNotifications().get(0).getNotification().getStatus());

    mockServer.verify();
  }

  @Test
  public void testGetNotifications_HandlesError() {

    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("ns2", HEADER_NS);
    namespaces.put("ns4", COMMON_NS);
    namespaces.put("ns5", CASE_NS);

    mockServer.expect(
            xpath("/ns5:NotificationInqRQ/ns2:HeaderRQ/ns2:TransactionRequestID",
                namespaces).exists())
        .andRespond(withError("Failed to call soap service"));

    assertThrows(RuntimeException.class,
        () -> client.getNotifications(new uk.gov.laa.ccms.soa.gateway.model.Notification()
                .user(
                    new uk.gov.laa.ccms.soa.gateway.model.UserDetail()
                        .userLoginId(testLoginId)
                        .userType(testUserType)
                        .userName("ding ding")
                )
                .caseReferenceNumber(null),
            false, null, null,
            10));

    mockServer.verify();
  }
}
