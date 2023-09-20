package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ws.client.WebServiceIOException;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.laa.ccms.soa.gateway.model.Notifications;
import uk.gov.laa.ccms.soa.gateway.service.NotificationService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebAppConfiguration
class NotificationControllerTest {

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private NotificationController notificationController;

  private MockMvc mockMvc;

  private Pageable pageable;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
    this.pageable = Pageable.ofSize(20);
  }

  @Test
  void testGetUserNotificationSummary_Success() throws Exception {
    // Mock input parameters

    // Create a mock notification summary
    NotificationSummary notificationSummary = new NotificationSummary();
    notificationSummary.setNotifications(1);
    notificationSummary.setStandardActions(2);
    notificationSummary.setOverdueActions(3);
    String soaGatewayUserLoginId = "user";
    String soaGatewayUserRole = "EXTERNAL";
    Integer maxRecords = 50;
    String userId = "123";
    // Mock the notificationService to return the mock notification summary
    when(notificationService.getNotificationSummary(userId, soaGatewayUserLoginId,
        soaGatewayUserRole, maxRecords))
        .thenReturn(notificationSummary);

    // Call the getUserNotificationSummary method
    mockMvc.perform(
            get("/users/{user-id}/notifications/summary?max-records={maxRecords}",
                userId, maxRecords)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole))
        .andExpect(status().isOk());

    // Verify that the notificationService method was called with the correct parameters
    verify(notificationService).getNotificationSummary(userId, soaGatewayUserLoginId,
        soaGatewayUserRole, maxRecords);
  }

  @Test
  void testGetUserNotificationSummary_Exception() throws Exception {
    // Mock input parameters
    String userId = "123";
    String soaGatewayUserLoginId = "user";
    String soaGatewayUserRole = "EXTERNAL";
    Integer maxRecords = 50;

    // Mock the notificationService to throw an exception
    when(notificationService.getNotificationSummary(userId, soaGatewayUserLoginId,
        soaGatewayUserRole, maxRecords))
        .thenThrow(new WebServiceIOException("Test exception"));

    // Call the getUserNotificationSummary method
    mockMvc.perform(
            get("/users/{user-id}/notifications/summary?max-records={maxRecords}",
                userId, maxRecords)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole))
        .andExpect(status().isInternalServerError());

    // Verify that the notificationService method was called with the correct parameters
    verify(notificationService).getNotificationSummary(userId, soaGatewayUserLoginId,
        soaGatewayUserRole, maxRecords);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "null, EXTERNAL", // SoaGateway-User-Login-Id is null
      "user, null" // SoaGateway-User-Role is null
  }, nullValues = {"null"})
  void testGetUserNotificationSummary_HeaderBadRequest(String userLoginId, String userRole)
      throws Exception {
    // Call the getUserNotificationSummary method with null headers
    MockHttpServletRequestBuilder requestBuilder =
        get("/users/{user-id}/notifications/summary?max-records={maxRecords}",
            "userId",
            "50");

    if (userLoginId != null) {
      requestBuilder.header("SoaGateway-User-Login-Id", userLoginId);
    }
    if (userRole != null) {
      requestBuilder.header("SoaGateway-User-Role", userRole);
    }

    // Call the getUserNotificationSummary method with optional headers
    mockMvc.perform(requestBuilder)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @CsvSource(value = {"user, role, null, null, user2, null, null, false, N, null, null, 10"},
      nullValues = {"null"})
  void testGetNotificationsSuccess(String soaGatewayUserLoginId, String soaGatewayUserRole,
      String caseReferenceNumber, String providerCaseReference, String assignedToUserId,
      String clientSurname, Integer feeEarnerId, Boolean includeClosed, String notificationType,
      Date dateFrom, Date dateTo, Integer maxRecords) throws Exception {
    //Create a mock Notifications object
    Notifications notifications = new Notifications();
    notifications.addContentItem(new Notification());

    //Stub the call to the service
    when(notificationService.getNotifications(any(), any(), any(), any(), any(), any()))
        .thenReturn(notifications);
    //Call the Notifications endpoint
    mockMvc.perform(
            get("/notifications?assigned-to-user-id={assignedToUserId}&maxRecords={maxRecords}",
                assignedToUserId, maxRecords)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @CsvSource(value = {"user, null, null, null, user2, null, null, false, N, null, null, 10",
      "null, role, null, null, user2, null, null, false, N, null, null, 10"},
      nullValues = {"null"})
  void testGetNotifications_HeaderBadRequest(String soaGatewayUserLoginId,
      String soaGatewayUserRole,
      String caseReferenceNumber, String providerCaseReference, String assignedToUserId,
      String clientSurname, Integer feeEarnerId, Boolean includeClosed, String notificationType,
      Date dateFrom, Date dateTo, Integer maxRecords) throws Exception {

    MockHttpServletRequestBuilder requestBuilder =
        get("/notifications?assigned-to-user-id={assignedToUserId}&maxRecords={maxRecords}",
            assignedToUserId, maxRecords);

    if (soaGatewayUserLoginId != null) {
      requestBuilder.header("SoaGateway-User-Login-Id", soaGatewayUserLoginId);
    }
    if (soaGatewayUserRole != null) {
      requestBuilder.header("SoaGateway-User-Role", soaGatewayUserRole);
    }

    //Call the Notifications endpoint
    mockMvc.perform(
            requestBuilder)
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @CsvSource(value = {"user, role, null, null, user2, null, null, false, N, null, null, 10"},
      nullValues = {"null"})
  void testGetNotifications_Exception(String soaGatewayUserLoginId, String soaGatewayUserRole,
      String caseReferenceNumber, String providerCaseReference, String assignedToUserId,
      String clientSurname, Integer feeEarnerId, Boolean includeClosed, String notificationType,
      Date dateFrom, Date dateTo, Integer maxRecords) throws Exception {
    when(notificationService.getNotifications(any(), any(), any(), any(), any(), any()))
        .thenThrow(new WebServiceIOException("Test exception"));
    mockMvc.perform(
            get("/notifications?assigned-to-user-id={assignedToUserId}&maxRecords={maxRecords}",
                assignedToUserId, maxRecords)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole))
        .andExpect(status().isInternalServerError());

    verify(notificationService, times(1)).getNotifications(any(), any(), any(), any(), any(), any());
  }

}
