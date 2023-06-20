package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBElement;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
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

  @Mock
  Logger mockLogger;

  @Mock
  WebServiceTemplate webServiceTemplate;

  @Captor
  ArgumentCaptor<JAXBElement<NotificationCntInqRQ>> requestCaptor;

  @InjectMocks
  private NotificationClient client;

  @Test
  public void testGetNotificationCountBuildsCorrectRequest() throws Exception {
    ObjectFactory objectFactory = new ObjectFactory();

    when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class),
        any(SoapActionCallback.class))).thenReturn(objectFactory.createNotificationCntInqRS(new NotificationCntInqRS()));

    final String testLoginId = "testLogin";
    final String testUserType = "testType";
    final String searchLoginId = "searchLogin";

    NotificationCntInqRS response = client.getNotificationCount(testLoginId, testUserType, searchLoginId,
        BigInteger.TEN);

    verify(webServiceTemplate).marshalSendAndReceive(requestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<NotificationCntInqRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(testLoginId, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(testUserType, payload.getValue().getHeaderRQ().getUserRole());
    assertEquals(searchLoginId, payload.getValue().getSearchCriteria().getUserID());
    assertNotNull(response);
  }
}
