package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationUpdateRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationUpdateRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;

@ExtendWith(MockitoExtension.class)
public class NotificationClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";
  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";

  @Mock Logger mockLogger;

  @Mock WebServiceTemplate webServiceTemplate;

  @Captor ArgumentCaptor<JAXBElement<NotificationUpdateRQ>> requestCaptor;

  private NotificationClient client;

  @BeforeEach
  void setup() {
    this.client = new NotificationClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  @Test
  public void testUpdateNotification() {
    ObjectFactory objectFactory = new ObjectFactory();

    final String notificationId = "12345";

    final Notification notification =
        new Notification().userId("userId").action("action").message("message");

    HeaderRSType headerRSType = new HeaderRSType();
    headerRSType.setTransactionID("12345");

    NotificationUpdateRS notificationUpdateRS = new NotificationUpdateRS();
    notificationUpdateRS.setHeaderRS(headerRSType);

    // Mock the response of the WebServiceTemplate
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createNotificationUpdateRS(notificationUpdateRS));

    NotificationUpdateRS actual =
        client.updateNotification(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, notification, notificationId);

    // Verify interactions
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<NotificationUpdateRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(SOA_GATEWAY_USER_LOGIN_ID, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(SOA_GATEWAY_USER_ROLE, payload.getValue().getHeaderRQ().getUserRole());
    assertEquals(notificationId, payload.getValue().getNotificationID());
    assertEquals(notification.getUserId(), payload.getValue().getUserID());
    assertEquals(notification.getAction(), payload.getValue().getAction());
    assertEquals(notification.getMessage(), payload.getValue().getMessage());
    assertNotNull(actual);
    assertEquals("12345", actual.getHeaderRS().getTransactionID());
  }
}
