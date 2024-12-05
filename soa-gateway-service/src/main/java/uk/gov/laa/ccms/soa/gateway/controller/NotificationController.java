package uk.gov.laa.ccms.soa.gateway.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.NotificationsApi;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
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

  /**
   * Get a list of notifications based on the supplied search criteria.
   *
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param caseReferenceNumber  (optional) - the case reference number.
   * @param providerCaseReference  (optional) - the provider case reference.
   * @param assignedToUserId  (optional) - the assigned to user id.
   * @param clientSurname  (optional) - the client surname.
   * @param feeEarnerId  (optional) - the fee earner id.
   * @param includeClosed  (optional) - whether the query should include closed notifications.
   * @param notificationType  (optional) - the notification type.
   * @param dateFrom  (optional) - the date from which notifications should be searched.
   * @param dateTo  (optional) - the date up to which notifications should be searched.
   * @param sort  (optional) - the sort field.
   * @param maxRecords  (optional, default to 100) - the maximum records to query.
   * @param pageable - the page settings.
   * @return ResponseEntity wrapping the notification list results.
   */
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
      final List<String> sort,
      final Integer maxRecords,
      final Pageable pageable) {
    try {

      //Build a Notification domain object to hold the search criteria
      log.info("GET / notifications");
      Notification notification = new Notification()
          .caseReferenceNumber(caseReferenceNumber)
          .providerCaseReferenceNumber(providerCaseReference)
          .clientName(clientSurname)
          .feeEarner(Optional.ofNullable(feeEarnerId).map(String::valueOf).orElse(""))
          .notificationType(notificationType)
          .user(
              new UserDetail()
                  .userLoginId(soaGatewayUserLoginId)
                  .userType(soaGatewayUserRole)
                  .userName(assignedToUserId)
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

}
