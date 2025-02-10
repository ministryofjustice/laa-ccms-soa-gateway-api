package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ws.client.WebServiceIOException;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.laa.ccms.soa.gateway.service.NotificationsService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebAppConfiguration
public class NotificationsControllerTest {

  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";

  @Mock
  private NotificationsService notificationsService;

  @InjectMocks
  private NotificationsController notificationsController;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(notificationsController).build();
  }

  @ParameterizedTest
  @CsvSource(value = {
      "message",
      "null" // message is not required
  }, nullValues={"null"})
  public void testUpdateNotification_success(String message) throws Exception {

    String notificationId = "notificationId";

    final Notification notification = new Notification()
        .userId("userId")
        .action("action")
        .message(message);

    when(notificationsService.updateNotification(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE,
        notification, notificationId))
        .thenReturn("12345");

    mockMvc.perform(
            put("/notifications/{notification-id}", notificationId)
                .content(new ObjectMapper().writeValueAsString(notification))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isOk());

    verify(notificationsService).updateNotification(SOA_GATEWAY_USER_LOGIN_ID,
        SOA_GATEWAY_USER_ROLE, notification, notificationId);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "null,   userId",
      "action, null"
  }, nullValues={"null"})
  public void testUpdateNotification_withNullRequiredFields_returnsBadRequest(String action,
      String userId) throws Exception {

    String notificationId = "notificationId";

    final Notification notification = new Notification()
        .userId(userId)
        .action(action)
        .message("message");

    when(notificationsService.updateNotification(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE,
        notification, notificationId))
        .thenReturn("67890");

    mockMvc.perform(
            put("/notifications/{notification-id}", notificationId)
                .content(new ObjectMapper().writeValueAsString(notification))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateNotification_returnsInternalServerError() throws Exception {

    String notificationId = "notificationId";

    final Notification notification = new Notification()
        .userId("userId")
        .action("action")
        .message("message");

    when(notificationsService.updateNotification(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE,
        notification, notificationId))
        .thenThrow(new WebServiceIOException("Test exception"));

    mockMvc.perform(
            put("/notifications/{notification-id}", notificationId)
                .content(new ObjectMapper().writeValueAsString(notification))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isInternalServerError());
  }

}
