package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationCntList;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
  @Mapping(target = "notifications", source = "notificationCntLists.notificationsCnt", qualifiedByName = "notificationCountTranslator")
  @Mapping(target = "standardActions", source = "notificationCntLists.notificationsCnt", qualifiedByName = "standardActionCountTranslator")
  @Mapping(target = "overdueActions", source = "notificationCntLists.notificationsCnt", qualifiedByName = "overdueActionCountTranslator")
  NotificationSummary toNotificationSummary(NotificationCntInqRS response);

  @Named("notificationCountTranslator")
  default Integer toNotificationCount(List<NotificationCntList> notificationCntLists) {
    return getNotificationCntList(notificationCntLists).map(n -> Integer.parseInt(n.getNotificationCount()))
        .orElseThrow(() -> new RuntimeException("notificationCount not found in response"));
  }

  @Named("standardActionCountTranslator")
  default Integer toStandardActionCount(List<NotificationCntList> notificationCntLists) {
    return getNotificationCntList(notificationCntLists).map(n -> Integer.parseInt(n.getStandardActionCount()))
        .orElseThrow(() -> new RuntimeException("standardActionCount not found in response"));
  }

  @Named("overdueActionCountTranslator")
  default Integer toOverdueActionCount(List<NotificationCntList> notificationCntLists) {
    return getNotificationCntList(notificationCntLists).map(n -> Integer.parseInt(n.getOverdueActionCount()))
        .orElseThrow(() -> new RuntimeException("overdueActionCount not found in response"));
  }

  default Optional<NotificationCntList> getNotificationCntList(List<NotificationCntList> notificationCntLists) {
    return Optional.ofNullable(notificationCntLists)
        .map(cntList -> cntList.stream().findFirst())
        .orElseThrow(() -> new RuntimeException("notificationCntList not found in response"));
  }
}
