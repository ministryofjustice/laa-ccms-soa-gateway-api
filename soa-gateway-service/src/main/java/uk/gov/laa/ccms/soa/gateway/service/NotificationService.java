package uk.gov.laa.ccms.soa.gateway.service;

import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.mapper.NotificationMapper;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.laa.ccms.soa.gateway.model.Notifications;
import uk.gov.laa.ccms.soa.gateway.util.PaginationUtil;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRS;

/**
 * Service class responsible for handling notification-related operations.
 *
 * <p>This service interfaces with the external Notification system through
 * {@link NotificationClient} and maps the results to a more friendly form using
 * {@link NotificationMapper}. The primary function of this service is to fetch notification
 * summaries based on user criteria.</p>
 */
@Service
@RequiredArgsConstructor
public class NotificationService extends AbstractSoaService {
  private final NotificationClient notificationClient;
  private final NotificationMapper notificationMapper;

  /**
   * Retrieves a list of notifications for a given user with selected search criteria.
   *
   * @param notification  The {#{@link Notification}} builder for the search criteria.
   * @param includeClosed Include closed notifications (optional) default: false.
   * @param dateFrom      The  date from which to search (optional).
   * @param dateTo        The to date for the search (optional).
   * @param maxRecords    The maximum number of records to retrieve.
   * @param pageable      The pagination details.
   * @return {@link Notification} object holding the list of {@link Notification}
   */
  public Notifications getNotifications(
      final Notification notification,
      final Boolean includeClosed,
      final XMLGregorianCalendar dateFrom,
      final XMLGregorianCalendar dateTo,
      final Integer maxRecords,
      final Pageable pageable) {

    final NotificationInqRS notificationResponse = notificationClient.getNotifications(
        notification,
        includeClosed,
        dateFrom,
        dateTo,
        maxRecords);

    final List<Notification> notificationList = notificationMapper.toNotificationsList(
        notificationResponse);

    final int listSize =
        getTotalElementsFromRecordCount(
            notificationResponse.getRecordCount(),
            notificationList.size());

    final Page<Notification> page = PaginationUtil.paginateList(
        pageable, notificationList, listSize);

    return notificationMapper.toNotifications(page);
  }
}
