package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationUpdateRS;

/** Service responsible for handling notification operations. */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsService {

  private final NotificationClient notificationClient;

  /**
   * Update a notification in EBS.
   *
   * @param soaGatewayUserLoginId The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole The user role in the SOA Gateway.
   * @param notification The notification response details.
   * @param notificationId The ID of the notification to update.
   * @return The transaction id.
   */
  public String updateNotification(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final Notification notification,
      final String notificationId) {

    final NotificationUpdateRS response =
        notificationClient.updateNotification(
            soaGatewayUserLoginId, soaGatewayUserRole, notification, notificationId);

    return response.getHeaderRS().getTransactionID();
  }
}
