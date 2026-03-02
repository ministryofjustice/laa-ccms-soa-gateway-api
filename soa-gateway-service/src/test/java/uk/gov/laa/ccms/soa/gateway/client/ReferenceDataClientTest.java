package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBElement;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRQ;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.KeyType;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.SearchContext;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class ReferenceDataClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";

  @Mock WebServiceTemplate webServiceTemplate;

  @Mock private JAXBElement<ReferenceDataInqRS> responseElement;

  private ReferenceDataClient client;

  @BeforeEach
  void setup() {
    this.client = new ReferenceDataClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  @Test
  public void testGetCaseReferenceBuildsCorrectRequest() {
    // Create mock response
    ReferenceDataInqRS response = new ReferenceDataInqRS();

    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(responseElement);
    when(responseElement.getValue()).thenReturn(response);

    final String loggedInUserId = "user";
    final String loggedInUserType = "EXTERNAL";

    ReferenceDataInqRS actualResponse = client.getCaseReference(loggedInUserId, loggedInUserType);

    verify(webServiceTemplate)
        .marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class));

    ArgumentCaptor<JAXBElement<ReferenceDataInqRQ>> requestCaptor =
        ArgumentCaptor.forClass(JAXBElement.class);
    verify(webServiceTemplate)
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<ReferenceDataInqRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(loggedInUserId, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(loggedInUserType, payload.getValue().getHeaderRQ().getUserRole());

    List<SearchContext> contexts = payload.getValue().getSearchCriteria();
    assertEquals(1, contexts.size());
    SearchContext context = contexts.get(0);
    List<KeyType> keyTypes = context.getContextKey();
    assertEquals(1, keyTypes.size());
    assertEquals(KeyType.CASE_REFERENCE_NUMBER, keyTypes.get(0));

    assertEquals(response, actualResponse);
  }
}
