package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.NotificationsApi;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.laa.ccms.soa.gateway.service.NotificationsService;

/**
 * Controller for handling notification related requests.
 *
 * <p>Provides an endpoint for adding responses to existing notifications.
 * Implements the {@link NotificationsApi} for consistent behavior with other
 * API implementations.</p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationsController implements NotificationsApi {

  private final NotificationsService notificationsService;

  /**
   * Update a notification.
   *
   * @param notificationId  (required) - the id of the notification to update.
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param notification Notification (required) - the notification details to update.
   * @return {@link ResponseEntity} wrapping the transaction id.
   */
  @Override
  public ResponseEntity<ClientTransactionResponse> updateNotification(String notificationId,
      String soaGatewayUserLoginId, String soaGatewayUserRole, Notification notification) {
    log.info("PUT /notifications/{}", notificationId);
    try {
      String transactionId = notificationsService.updateNotification(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          notification,
          notificationId);

      return ResponseEntity.ok(new ClientTransactionResponse().transactionId(transactionId));
    } catch (Exception e) {
      log.error("NotificationsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

}
