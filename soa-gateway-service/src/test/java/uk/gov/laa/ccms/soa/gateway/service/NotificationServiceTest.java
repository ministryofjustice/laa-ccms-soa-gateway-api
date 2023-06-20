package uk.gov.laa.ccms.soa.gateway.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.mapper.NotificationMapper;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationCntList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {


    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private NotificationMapper notificationMapper;

    @Test
    public void testGetNotificationSummary() {
        // Create a mocked instance of NotificationCntInqRS
        NotificationCntInqRS response = new NotificationCntInqRS();
        NotificationCntInqRS.NotificationCntLists notificationCntLists = new NotificationCntInqRS.NotificationCntLists();
        NotificationCntList notificationCntList = new NotificationCntList();
        notificationCntList.setNotificationCount("5");
        notificationCntList.setStandardActionCount("3");
        notificationCntList.setOverdueActionCount("2");
        notificationCntLists.getNotificationsCnt().add(notificationCntList);
        response.setNotificationCntLists(notificationCntLists);

        // Create a mocked instance of NotificationSummary
        NotificationSummary expectedSummary = new NotificationSummary();
        expectedSummary.setNotifications(5);
        expectedSummary.setStandardActions(3);
        expectedSummary.setOverdueActions(2);

        // Stub the NotificationClient to return the mocked response
        when(notificationClient.getNotificationCount(anyString(), anyString(), anyString(), any(BigInteger.class)))
                .thenReturn(response);

        // Stub the NotificationMapper to return the mocked summary
        when(notificationMapper.map(eq(response))).thenReturn(expectedSummary);

        // Call the service method
        NotificationSummary result = notificationService.getNotificationSummary(
                "searchLoginId",
                "soaGatewayUserLoginId",
                "soaGatewayUserRole",
                10
        );

        // Verify that the NotificationClient method was called with the expected arguments
        verify(notificationClient).getNotificationCount(
                "soaGatewayUserLoginId",
                "soaGatewayUserRole",
                "searchLoginId",
                BigInteger.valueOf(10)
        );

        // Verify that the map function in the NotificationMapper was called with the mocked response
        verify(notificationMapper).map(response);

        // Assert the result
        assertEquals(expectedSummary, result);
    }
}

