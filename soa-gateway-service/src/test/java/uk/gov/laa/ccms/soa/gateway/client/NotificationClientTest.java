//package uk.gov.laa.ccms.soa.gateway.client;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.springframework.ws.test.client.RequestMatchers.payload;
//import static org.springframework.ws.test.client.ResponseCreators.withPayload;
//
//import java.math.BigInteger;
//import javax.xml.transform.Source;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.slf4j.Logger;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.ws.test.client.MockWebServiceServer;
//import org.springframework.xml.transform.StringSource;
//import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
//
//@ExtendWith(MockitoExtension.class)
//@ContextConfiguration
//public class NotificationClientTest {
//
//  @Mock
//  Logger mockLogger;
//
//  @InjectMocks
//  private NotificationClient client;
//
//  private static MockWebServiceServer mockServer;
//
//  @BeforeEach
//  public void createServer() {
//    mockServer = MockWebServiceServer.createServer(client);
//  }
//
//  @Test
//  public void testCreateTransactionId() throws Exception {
//    Source expectedRequestPayload =
//        new StringSource("<customerCountRequest xmlns=\"http://springframework.org/spring-ws/test\" />");
//
//    Source responsePayload = new StringSource("<customerCountResponse xmlns='http://springframework.org/spring-ws/test'>" +
//        "<customerCount>10</customerCount>" +
//        "</customerCountResponse>");
//
//    mockServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
//
//    final String testLoginId = "testLogin";
//    final String testUserType = "testType";
//    final String searchLoginId = "searchLogin";
//
//    NotificationCntInqRS response = client.getNotificationCount(testLoginId, testUserType, searchLoginId,
//        BigInteger.TEN);
//
//    assertNotNull(response.getNotificationCntLists());
//    assertEquals("10", response.getNotificationCntLists().getNotificationsCnt().get(0).getNotificationCount());
//
//    mockServer.verify();
//  }
//}
