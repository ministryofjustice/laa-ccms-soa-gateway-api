package uk.gov.laa.ccms.soa.gateway.mapper;

import org.springframework.stereotype.Component;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationCntList;

@Component
public class NotificationMapper {
    public NotificationSummary map(NotificationCntInqRS response){

        NotificationSummary notificationSummary = new NotificationSummary();

        NotificationCntList notificationCntList = response.getNotificationCntLists().getNotificationsCnt().get(0);

        notificationSummary.setNotifications(Integer.parseInt(notificationCntList.getNotificationCount()));
        notificationSummary.setStandardActions(Integer.parseInt(notificationCntList.getStandardActionCount()));
        notificationSummary.setOverdueActions(Integer.parseInt(notificationCntList.getOverdueActionCount()));

        return notificationSummary;
    }
}
