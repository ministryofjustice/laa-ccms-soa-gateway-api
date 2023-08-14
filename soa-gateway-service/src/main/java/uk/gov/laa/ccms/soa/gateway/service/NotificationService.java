package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.mapper.NotificationMapper;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;

/**
 * Service class responsible for handling notification-related operations.
 *
 * <p>This service interfaces with the external Notification system through
 * {@link NotificationClient} and maps the results to a more friendly form using
 * {@link NotificationMapper}. The primary function of this service is to fetch
 * notification summaries based on user criteria.</p>
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationClient notificationClient;

  private final NotificationMapper notificationMapper;

  /**
   * Retrieves a summary of notifications for a given user based on search criteria.
   *
   * <p>Fetches the notification counts for the specified search login ID and
   * SOA Gateway user credentials. It then maps the raw response into a more
   * structured form, {@link NotificationSummary}.</p>
   *
   * @param searchLoginId           The search login ID to filter the notifications.
   * @param soaGatewayUserLoginId   The SOA Gateway user login ID.
   * @param soaGatewayUserRole      The SOA Gateway user role.
   * @param maxRecords              The maximum number of records to retrieve.
   * @return                        A {@link NotificationSummary} representing the summary
   *                                of notifications.
   */
  public NotificationSummary getNotificationSummary(final String searchLoginId,
                                                    final String soaGatewayUserLoginId,
                                                    final String soaGatewayUserRole,
                                                    final Integer maxRecords) {
    NotificationCntInqRS response = notificationClient.getNotificationCount(
            searchLoginId,
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords);

    return notificationMapper.toNotificationSummary(response);
  }




}
