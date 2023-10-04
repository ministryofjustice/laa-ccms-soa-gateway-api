package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.laa.ccms.soa.gateway.model.Note;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.laa.ccms.soa.gateway.model.Notifications;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotesElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationCntList;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationListElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

/**
 * Mapper for transforming data related to notifications.
 *
 * <p>Uses the MapStruct framework to facilitate the conversion between the Legal Services endpoint
 * data model and the internal SoA gateway's {@link NotificationSummary} and {@link Notification}
 * models.</p>
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

  /**
   * Converts a {@link NotificationCntInqRS} to a {@link NotificationSummary}.
   *
   * @param response The source data fetched from the Legal Services endpoint.
   * @return The transformed {@link NotificationSummary}.
   */
  @Mapping(target = "notifications",
      source = "notificationCntLists.notificationsCnt",
      qualifiedByName = "notificationCountTranslator")
  @Mapping(target = "standardActions",
      source = "notificationCntLists.notificationsCnt",
      qualifiedByName = "standardActionCountTranslator")
  @Mapping(target = "overdueActions",
      source = "notificationCntLists.notificationsCnt",
      qualifiedByName = "overdueActionCountTranslator")
  NotificationSummary toNotificationSummary(NotificationCntInqRS response);

  /**
   * Extracts the notification count from a list of {@link NotificationCntList}.
   *
   * @param notificationCntLists The list of notification counts.
   * @return The extracted notification count.
   * @throws RuntimeException if notification count is not found in the response.
   */
  @Named("notificationCountTranslator")
  default Integer toNotificationCount(List<NotificationCntList> notificationCntLists) {
    return getNotificationCntList(notificationCntLists)
        .map(n -> Integer.parseInt(n.getNotificationCount()))
        .orElseThrow(() -> new RuntimeException("notificationCount not found in response"));
  }

  /**
   * Extracts the standard action count from a list of {@link NotificationCntList}.
   *
   * @param notificationCntLists The list of notification counts.
   * @return The extracted standard action count.
   * @throws RuntimeException if standard action count is not found in the response.
   */
  @Named("standardActionCountTranslator")
  default Integer toStandardActionCount(List<NotificationCntList> notificationCntLists) {
    return getNotificationCntList(notificationCntLists)
        .map(n -> Integer.parseInt(n.getStandardActionCount()))
        .orElseThrow(() -> new RuntimeException("standardActionCount not found in response"));
  }

  /**
   * Extracts the overdue action count from a list of {@link NotificationCntList}.
   *
   * @param notificationCntLists The list of notification counts.
   * @return The extracted overdue action count.
   * @throws RuntimeException if overdue action count is not found in the response.
   */
  @Named("overdueActionCountTranslator")
  default Integer toOverdueActionCount(List<NotificationCntList> notificationCntLists) {
    return getNotificationCntList(notificationCntLists)
        .map(n -> Integer.parseInt(n.getOverdueActionCount()))
        .orElseThrow(() -> new RuntimeException("overdueActionCount not found in response"));
  }

  /**
   * Retrieves the first notification count list item from a list.
   *
   * @param notificationCntLists The list of notification counts.
   * @return An {@link Optional} containing the first notification count list item, if present.
   * @throws RuntimeException if notification count list is not found in the response.
   */
  default Optional<NotificationCntList> getNotificationCntList(
      List<NotificationCntList> notificationCntLists) {
    return Optional.ofNullable(notificationCntLists)
        .map(cntList -> cntList.stream().findFirst())
        .orElseThrow(() -> new RuntimeException("notificationCntList not found in response"));
  }

  // ----------------------               Notifications             --------------------------

  /**
   * Converts the {@link NotificationInqRS} object to a list of
   * {@link uk.gov.laa.ccms.soa.gateway.model.Notification} objects.
   *
   * <p>This default method takes a {@link NotificationInqRS} instance and extracts the list of
   * notifications from it. It then delegates the conversion of this list to the
   * {@link #getNotificationCntList(List)} )} method. If the {@link NotificationInqRS} instance is
   * null, or if it does not contain any notification data, an empty list is returned.</p>
   *
   * @param notificationInqRs The notificaiton inquiry response to be converted.
   * @return A list of {@link uk.gov.laa.ccms.soa.gateway.model.Notification} objects or an empty
   *     list if no notification data is available.
   */
  default List<Notification> toNotificationsList(NotificationInqRS notificationInqRs) {

    if (notificationInqRs.getNotificationList() != null) {
      NotificationInqRS.NotificationList notificationListObject
          = notificationInqRs.getNotificationList();

      if (notificationListObject.getNotifications() != null) {
        List<NotificationListElementType> notificationList
            = notificationListObject.getNotifications();
        if (notificationList != null) {
          return toNotificationList(notificationList);
        }
      }
    }
    return Collections.emptyList();
  }

  @Mapping(source = ".", target = "notifications")
  List<Notification> toNotificationList(
      List<NotificationListElementType> notificationList);

  Notifications toNotifications(Page<Notification> page);

  @Mapping(target = ".", source = "notification")
  @Mapping(target = "notes", source = "notification.notes.note")
  @Mapping(target = "availableResponses", source = "notification.availableResponses.response")
  @Mapping(target = "attachedDocuments", source = "notification.uploadedDocuments.document")
  @Mapping(target = "uploadedDocuments", source = "notification.attachedDocuments.document")
  @Mapping(target = "notificationType", source = "notification.notitificationType")
  @Mapping(target = "notificationOpenIndicator", source = "notification.notificationOpenInd")
  @Mapping(target = "notificationId", source = "notification.notificationID")
  @Mapping(target = "providerFirmId", source = "notification.providerFirmID")
  Notification toNotification(NotificationListElementType notificationListElementType);

  @Mapping(target = "documentId", source = "documentID")
  Document toDocument(DocumentElementType documentElementType);

  @Mapping(target = "notesId", source = "notesID")
  Note toNote(NotesElementType notesElementType);

  @Mapping(target = "userLoginId", source = "userLoginID")
  UserDetail toUserDetail(User user);


}
