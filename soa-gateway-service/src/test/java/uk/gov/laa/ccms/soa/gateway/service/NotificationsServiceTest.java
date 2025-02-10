package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationUpdateRS;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;

@ExtendWith(MockitoExtension.class)
public class NotificationsServiceTest {

  @Mock
  private NotificationClient notificationClient;

  @InjectMocks
  private NotificationsService notificationsService;

  @Test
  public void testUpdateNotification() {

    String soaGatewayUserLoginId = "soaGatewayUserLoginId";
    String soaGatewayUserRole = "soaGatewayUserRole";

    String notificationId = "notificationId";

    Notification notification = new Notification()
        .userId("userId")
        .action("action")
        .message("message");

    HeaderRSType headerRSType = new HeaderRSType();
    headerRSType.setTransactionID("12345");

    NotificationUpdateRS notificationUpdateRS = new NotificationUpdateRS();
    notificationUpdateRS.setHeaderRS(headerRSType);

    when(notificationClient.updateNotification(soaGatewayUserLoginId, soaGatewayUserRole,
        notification, notificationId))
        .thenReturn(notificationUpdateRS);

    String transactionId = notificationsService.updateNotification(soaGatewayUserLoginId,
        soaGatewayUserRole, notification, notificationId);

    assertEquals("12345", transactionId);

    verify(notificationClient).updateNotification(soaGatewayUserLoginId, soaGatewayUserRole,
        notification, notificationId);

  }

}
