package uk.gov.laa.ccms.soa.gateway.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

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
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationUpdateRS;

@SpringBootTest
public class NotificationClientIntegrationTest {

  @Autowired
  private WebServiceTemplate webServiceTemplate;

  @Autowired
  private NotificationClient client;

  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/NotificationUpdateRS_valid.xml")
  Resource notificationUpdateRS_valid;

  private static final String HEADER_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String MSG_NS = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIM";

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
  }

  @Test
  public void testUpdateNotification_ReturnsData() throws Exception {

    String notificationId = "notificationId";

    Notification notification = new Notification()
        .userId("userId")
        .action("action")
        .message("message");

    mockServer.expect(
            xpath("/msg:NotificationUpdateRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andExpect(
            xpath("/msg:NotificationUpdateRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
                testLoginId))
        .andExpect(xpath("/msg:NotificationUpdateRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
            testUserType))
        .andExpect(
            xpath("/msg:NotificationUpdateRQ/msg:NotificationID",
                namespaces)
                .evaluatesTo(notificationId))
        .andExpect(
            xpath("/msg:NotificationUpdateRQ/msg:UserID",
                namespaces)
                .evaluatesTo(notification.getUserId()))
        .andExpect(
            xpath("/msg:NotificationUpdateRQ/msg:Action",
                namespaces)
                .evaluatesTo(notification.getAction()))
        .andExpect(
            xpath("/msg:NotificationUpdateRQ/msg:Message",
                namespaces)
                .evaluatesTo(notification.getMessage()))
        .andRespond(withPayload(notificationUpdateRS_valid));

    NotificationUpdateRS response = client.updateNotification(testLoginId, testUserType,
        notification, notificationId);

    assertNotNull(response.getHeaderRS());
    assertThat(response.getHeaderRS().getStatus().getStatusFreeText(),
        containsString("Task Updated Successfully."));

    mockServer.verify();
  }

}
