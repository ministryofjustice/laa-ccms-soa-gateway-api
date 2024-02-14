package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withError;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ.SearchCriteria.Organization;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS;

/**
 * Integration Test for the CommonOrgClient.
 *
 */
@SpringBootTest
public class CommonOrgClientIntegrationTest {

  private static final String HEADER_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String MSG_NS = "http://legalservices.gov.uk/CCMS/Common/ReferenceData/1.0/ReferenceDataBIM";
  private static final String COMMON_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";
  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/CommonOrgInqRS_valid.xml")
  Resource commonOrgInqRs_valid;

  @Value("classpath:/payload/CommonOrgInqRS_partyId_valid.xml")
  Resource commonOrgInqRs_partyId_valid;

  @Autowired
  private WebServiceTemplate webServiceTemplate;

  @Autowired
  private CommonOrgClient client;
  private final String testTransactionId = "202309260908406430348479724";
  private String testLoginId;
  private String testUserType;
  private int maxRecords;

  private final String name = "orgname";
  private final String type = "orgtype";
  private final String city = "City";
  private final String postcode = "ABC 123";

  private Map<String, String> namespaces;

  @BeforeEach
  public void createServer() {
    mockServer = MockWebServiceServer.createServer(webServiceTemplate);

    testLoginId = "testLogin";
    testUserType = "testType";
    maxRecords = 50;

    namespaces = new HashMap<>();
    namespaces.put("header", HEADER_NS);
    namespaces.put("msg", MSG_NS);
    namespaces.put("common", COMMON_NS);
  }

  @Test
  public void testGetOrganisations_ReturnsData() throws Exception {
    Organization organization = buildOrganization();

    mockServer.expect(
        xpath("/msg:CommonOrgInqRQ/header:HeaderRQ/header:TransactionRequestID",
            namespaces).exists()).andExpect(
        xpath("/msg:CommonOrgInqRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
            testLoginId)).andExpect(
        xpath("/msg:CommonOrgInqRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
            testUserType)).andExpect(
        xpath("/msg:CommonOrgInqRQ/msg:SearchCriteria/msg:Organization/msg:OrganizationName",
            namespaces).evaluatesTo(name)).andExpect(
        xpath("/msg:CommonOrgInqRQ/msg:SearchCriteria/msg:Organization/msg:OrganizationType",
            namespaces).evaluatesTo(type))
        .andRespond(withPayload(commonOrgInqRs_valid));

    CommonOrgInqRS response = client.getOrganisations(testLoginId, testUserType, maxRecords,
        organization);

    assertNotNull(response.getOrganizationList());
    assertEquals(3, response.getOrganizationList().size());

    mockServer.verify();
  }

  @Test
  public void testGetOrganisations_HandlesError() {
    Organization organization = buildOrganization();

    mockServer.expect(
            xpath("/msg:CommonOrgInqRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andRespond(withError("Failed to call soap service"));

    assertThrows(RuntimeException.class,
        () -> client.getOrganisations(testLoginId, testUserType, maxRecords, organization));

    mockServer.verify();
  }

  @Test
  public void testGetOrganisation_ReturnsData() throws Exception {
    String partyId = "123";

    mockServer.expect(
            xpath("/msg:CommonOrgInqRQ/header:HeaderRQ/header:TransactionRequestID",
                namespaces).exists()).andExpect(
            xpath("/msg:CommonOrgInqRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
                testLoginId)).andExpect(
            xpath("/msg:CommonOrgInqRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
                testUserType)).andExpect(
            xpath("/msg:CommonOrgInqRQ/msg:SearchCriteria/msg:OrganizationPartyID",
                namespaces).evaluatesTo(partyId))
        .andRespond(withPayload(commonOrgInqRs_partyId_valid));

    CommonOrgInqRS response = client.getOrganisation(testLoginId, testUserType, maxRecords,
        partyId);

    assertNotNull(response.getOrganization());

    mockServer.verify();
  }

  private Organization buildOrganization() {
    Organization organization = new Organization();
    organization.setOrganizationName(name);
    organization.setOrganizationType(type);
    organization.setCity(city);
    organization.setPostCode(postcode);
    return organization;
  }
}
