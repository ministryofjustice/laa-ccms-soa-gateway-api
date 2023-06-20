package uk.gov.laa.ccms.soa.gateway.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationCntList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NotificationMapperTest {

    @InjectMocks
    NotificationMapper notificationMapper;

    @Test
    public void testMap() {
        NotificationCntInqRS response = new NotificationCntInqRS();
        NotificationCntInqRS.NotificationCntLists notificationCntLists = new NotificationCntInqRS.NotificationCntLists();
        response.setNotificationCntLists(notificationCntLists);

        NotificationCntList notificationCntList = new NotificationCntList();
        notificationCntList.setNotificationCount("5");
        notificationCntList.setStandardActionCount("3");
        notificationCntList.setOverdueActionCount("2");

        notificationCntLists.getNotificationsCnt().add(notificationCntList);

        uk.gov.laa.ccms.soa.gateway.model.NotificationSummary result = notificationMapper.map(response);

        assertNotNull(result);
        assertEquals(5, result.getNotifications());
        assertEquals(3, result.getStandardActions());
        assertEquals(2, result.getOverdueActions());
    }
}