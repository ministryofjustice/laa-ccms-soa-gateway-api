package uk.gov.laa.ccms.soa.gateway.controller;

import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.NotificationsApi;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.laa.ccms.soa.gateway.model.NotificationDetail;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.laa.ccms.soa.gateway.model.Notifications;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.laa.ccms.soa.gateway.service.NotificationService;
import uk.gov.laa.ccms.soa.gateway.util.DateUtil;

/**
 * Controller responsible for managing user notifications within the system.
 *
 * <p>Acts as an implementation of the {@link NotificationsApi}, leveraging the
 * {@link NotificationService} to handle the associated business logic and data retrieval related to
 * user notifications.</p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController implements NotificationsApi {

  private final NotificationService notificationService;

  @Override
  public ResponseEntity<Notifications> getNotifications(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String caseReferenceNumber,
      final String providerCaseReference,
      final String assignedToUserId,
      final String clientSurname,
      final Integer feeEarnerId,
      final Boolean includeClosed,
      final String notificationType,
      final Date dateFrom,
      final Date dateTo,
      final Integer maxRecords,
      final Pageable pageable) {
    try {

      //Build a Notification domain bject to hold the search criteria

      Notification notification = new Notification()
          .caseReferenceNumber(caseReferenceNumber)
          .providerCaseReferenceNumber(providerCaseReference)
          .clientName(clientSurname)
          .feeEarner(Optional.ofNullable(feeEarnerId).map(String::valueOf).orElse(""))
          .user(
              new UserDetail()
                  .userLoginId(soaGatewayUserLoginId)
                  .userType(soaGatewayUserRole)
                  .userName(assignedToUserId)
          )
          .notificationDetail(
              new NotificationDetail()
                  .notificationType(notificationType)
          );
      Notifications notifications = notificationService.getNotifications(
          notification,
          includeClosed,
          DateUtil.convertDateToXmlDateOnly(dateFrom),
          DateUtil.convertDateToXmlDateOnly(dateTo),
          maxRecords,
          pageable);
      return ResponseEntity.ok(notifications);
    } catch (Exception e) {
      log.error("Notification Controller caught exception in getNotifications", e);
      return ResponseEntity.internalServerError().build();
    }

  }

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
