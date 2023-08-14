package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.NotificationsApi;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.laa.ccms.soa.gateway.service.NotificationService;

/**
 * Controller responsible for managing user notifications within the system.
 *
 * <p>Acts as an implementation of the {@link NotificationsApi}, leveraging the
 * {@link NotificationService} to handle the associated business logic and data retrieval
 * related to user notifications.</p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController implements NotificationsApi {

  private final NotificationService notificationService;

  @Override
  public ResponseEntity<NotificationSummary> getUserNotificationSummary(
          final String userId,
          final String soaGatewayUserLoginId,
          final String soaGatewayUserRole,
          final Integer maxRecords) {

    try {
      NotificationSummary notificationSummary = notificationService.getNotificationSummary(
              userId,
              soaGatewayUserLoginId,
              soaGatewayUserRole,
              maxRecords);
      return ResponseEntity.ok(notificationSummary);
    } catch (Exception e) {
      log.error("Notification Controller caught exception", e);
      return ResponseEntity.internalServerError().build();
    }

  }
}
