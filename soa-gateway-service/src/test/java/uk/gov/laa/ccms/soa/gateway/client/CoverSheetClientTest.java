package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBElement;
import org.apache.commons.codec.binary.Base64;
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
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentCoverRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentCoverRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;

@ExtendWith(MockitoExtension.class)
public class CoverSheetClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";
  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";
  private static final Integer MAX_RECORDS = 50;

  @Mock
  Logger mockLogger;

  @Mock
  WebServiceTemplate webServiceTemplate;

  private CoverSheetClient client;

  @Captor
  ArgumentCaptor<JAXBElement<DocumentCoverRQ>> coverRequestCaptor;

  @BeforeEach
  void setup() {
    this.client = new CoverSheetClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  @Test
  void testDownloadDocumentCoverSheet_constructsGetCoverSheetRequest() {
    ObjectFactory objectFactory = new ObjectFactory();

    byte[] documentContent = Base64.decodeBase64("ZG9jdW1lbnQtY29udGVudA==");

    DocumentCoverRS documentCoverRS = new DocumentCoverRS();
    documentCoverRS.setCoverSheetDoc(documentContent);

    // Mock the response of the WebServiceTemplate
    when(webServiceTemplate.marshalSendAndReceive(
        eq(SERVICE_URL),
        any(JAXBElement.class),
        any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createDocumentCoverRS(documentCoverRS));

    String documentId = "document-id";

    byte[] response = client.downloadDocumentCoverSheet(
        SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, documentId);

    // Verify interactions
    verify(webServiceTemplate, times(1)).marshalSendAndReceive(
        eq(SERVICE_URL),
        coverRequestCaptor.capture(),
        any(SoapActionCallback.class));

    JAXBElement<DocumentCoverRQ> payload = coverRequestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(SOA_GATEWAY_USER_LOGIN_ID, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(SOA_GATEWAY_USER_ROLE, payload.getValue().getHeaderRQ().getUserRole());
    assertEquals(documentId, payload.getValue().getExpectedDocumentID());
    assertNotNull(response);
    assertEquals(documentContent, response);
  }

}
