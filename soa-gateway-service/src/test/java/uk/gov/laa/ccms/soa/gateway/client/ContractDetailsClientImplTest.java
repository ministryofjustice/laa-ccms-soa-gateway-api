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
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRQ;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ObjectFactory;

@ExtendWith(MockitoExtension.class)
public class ContractDetailsClientImplTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";
  @Mock Logger mockLogger;

  @Mock WebServiceTemplate webServiceTemplate;

  @Captor ArgumentCaptor<JAXBElement<ContractDetailsInqRQ>> requestCaptor;

  private ContractDetailsClientImpl client;

  @BeforeEach
  void setup() {
    this.client = new ContractDetailsClientImpl(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  @Test
  public void testGetContractDetailsBuildsCorrectRequest() {
    ObjectFactory objectFactory = new ObjectFactory();

    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createContractDetailsRS(new ContractDetailsInqRS()));

    final String searchFirmId = "searchFirmId";
    final String searchOfficeId = "searchOfficeId";
    final String testLoginId = "testLogin";
    final String testUserType = "testType";
    final Integer maxRecords = 50;

    ContractDetailsInqRS response =
        client.getContractDetails(
            searchFirmId, searchOfficeId, testLoginId, testUserType, maxRecords);

    verify(webServiceTemplate)
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<ContractDetailsInqRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(testLoginId, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(testUserType, payload.getValue().getHeaderRQ().getUserRole());
    assertEquals(searchFirmId, payload.getValue().getSearchCriteria().getFirmID());
    assertEquals(searchOfficeId, payload.getValue().getSearchCriteria().getOfficeID());
    assertNotNull(response);
  }
}
