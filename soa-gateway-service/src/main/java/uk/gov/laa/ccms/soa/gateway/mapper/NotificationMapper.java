package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationCntList;

/**
 * Mapper for transforming data related to notifications.
 *
 * <p>Uses the MapStruct framework to facilitate the conversion between the Legal Services endpoint
 * data model and the internal SoA gateway's {@link NotificationSummary} model.</p>
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
}
