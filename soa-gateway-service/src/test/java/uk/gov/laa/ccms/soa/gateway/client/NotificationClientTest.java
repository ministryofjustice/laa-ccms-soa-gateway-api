package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;

@ExtendWith(MockitoExtension.class)
public class NotificationClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";

  @Mock
  Logger mockLogger;

  @Mock
  WebServiceTemplate webServiceTemplate;

  @Captor
  ArgumentCaptor<JAXBElement<NotificationCntInqRQ>> requestCaptor;

  private NotificationClient client;

  @BeforeEach
  void setup() {
    this.client = new NotificationClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  @Test
  public void testGetNotificationCountBuildsCorrectRequest() {
    ObjectFactory objectFactory = new ObjectFactory();

    when(webServiceTemplate.marshalSendAndReceive(
        eq(SERVICE_URL),
        any(JAXBElement.class),
        any(SoapActionCallback.class))).thenReturn(
            objectFactory.createNotificationCntInqRS(new NotificationCntInqRS()));

    final String searchLoginId = "searchLogin";
    final String testLoginId = "testLogin";
    final String testUserType = "testType";

    NotificationCntInqRS response = client.getNotificationCount(searchLoginId, testLoginId,
        testUserType, 10);

    verify(webServiceTemplate).marshalSendAndReceive(
        eq(SERVICE_URL),
        requestCaptor.capture(),
        any(SoapActionCallback.class));

    JAXBElement<NotificationCntInqRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(testLoginId, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(testUserType, payload.getValue().getHeaderRQ().getUserRole());
    assertEquals(searchLoginId, payload.getValue().getSearchCriteria().getUserID());
    assertNotNull(response);
  }
}
