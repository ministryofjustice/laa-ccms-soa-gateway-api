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
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ.SearchCriteria.Organization;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ObjectFactory;

@ExtendWith(MockitoExtension.class)
class CommonOrgClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";
  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";
  private static final Integer MAX_RECORDS = 50;

  @Mock Logger mockLogger;

  @Mock WebServiceTemplate webServiceTemplate;

  @Captor ArgumentCaptor<JAXBElement<CommonOrgInqRQ>> requestCaptor;

  private CommonOrgClient client;

  @BeforeEach
  void setup() {
    this.client = new CommonOrgClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  @Test
  public void testGetOrganisationsBuildsCorrectRequest() {
    ObjectFactory objectFactory = new ObjectFactory();

    // Mock the response of the WebServiceTemplate
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createCommonOrgInqRS(new CommonOrgInqRS()));

    Organization organisation = buildOrganisation();

    CommonOrgInqRS response =
        client.getOrganisations(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, MAX_RECORDS, organisation);

    // Verify interactions
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<CommonOrgInqRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(SOA_GATEWAY_USER_LOGIN_ID, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(SOA_GATEWAY_USER_ROLE, payload.getValue().getHeaderRQ().getUserRole());
    Organization payloadOrg = payload.getValue().getSearchCriteria().getOrganization();
    assertEquals(organisation.getOrganizationName(), payloadOrg.getOrganizationName());
    assertEquals(organisation.getOrganizationType(), payloadOrg.getOrganizationType());
    assertEquals(organisation.getCity(), payloadOrg.getCity());
    assertEquals(organisation.getPostCode(), payloadOrg.getPostCode());
    assertNotNull(response);
  }

  @Test
  public void testGetOrganisationBuildsCorrectRequest() {
    ObjectFactory objectFactory = new ObjectFactory();

    // Mock the response of the WebServiceTemplate
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createCommonOrgInqRS(new CommonOrgInqRS()));

    final String partyId = "123";

    CommonOrgInqRS response =
        client.getOrganisation(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, MAX_RECORDS, partyId);

    // Verify interactions
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<CommonOrgInqRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(SOA_GATEWAY_USER_LOGIN_ID, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(SOA_GATEWAY_USER_ROLE, payload.getValue().getHeaderRQ().getUserRole());
    assertEquals(partyId, payload.getValue().getSearchCriteria().getOrganizationPartyID());
    assertNotNull(response);
  }

  private Organization buildOrganisation() {
    Organization organization = new Organization();
    organization.setOrganizationName("name");
    organization.setOrganizationType("type");
    organization.setCity("thecity");
    organization.setPostCode("thepostcode");
    return organization;
  }
}
