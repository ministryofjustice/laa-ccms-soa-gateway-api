package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withError;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.io.IOException;
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
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseInfo;

@SpringBootTest
public class CaseServicesClientIntegrationTest {

  @Autowired
  private WebServiceTemplate webServiceTemplate;

  @Autowired
  private CaseServicesClient client;

  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/CaseInqRS_valid.xml")
  Resource caseInqRS_valid;
  @Value("classpath:/payload/CaseInqRS_valid_one.xml")
  Resource caseInqRS_valid_one;

  @Value("classpath:/payload/CaseAddUpdtStatusRS_Valid.xml")
  Resource caseAddUpdtStatusRS_valid;

  private static final String HEADER_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String MSG_NS = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIM";
  private static final String CLIENT_NS = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIO";
  private static final String COMMON_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";

  private String testTransactionId = "202309260908406430348479724";

  private String testLoginId;
  private String testUserType;
  private int maxRecords;

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
    namespaces.put("client", CLIENT_NS);
    namespaces.put("common", COMMON_NS);
  }

  @Test
  public void testGetCaseDetails_ReturnsData() throws Exception {
    CaseInfo caseInfo = buildCaseInfo();

    mockServer.expect(
            xpath("/msg:CaseInqRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andExpect(
            xpath("/msg:CaseInqRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
                testLoginId))
        .andExpect(xpath("/msg:CaseInqRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
            testUserType))
        .andExpect(
            xpath("/msg:CaseInqRQ/msg:SearchCriteria/msg:CaseInfo/client:CaseReferenceNumber",
                namespaces)
                .evaluatesTo(caseInfo.getCaseReferenceNumber()))
        .andExpect(
            xpath("/msg:CaseInqRQ/msg:SearchCriteria/msg:CaseInfo/client:CaseStatus", namespaces)
                .evaluatesTo(caseInfo.getCaseStatus()))
        .andRespond(withPayload(caseInqRS_valid));

    CaseInqRS response = client.getCaseDetails(testLoginId, testUserType,
        maxRecords, caseInfo);

    assertNotNull(response.getCaseList());
    assertEquals(10, response.getCaseList().size());

    mockServer.verify();
  }

  @Test
  public void testGetCaseDetails_HandlesError() {
    CaseInfo caseInfo = buildCaseInfo();

    mockServer.expect(
            xpath("/msg:CaseInqRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andRespond(withError("Failed to call soap service"));

    assertThrows(RuntimeException.class, () -> client.getCaseDetails(
        testLoginId, testUserType, maxRecords, caseInfo));

    mockServer.verify();
  }

  @Test
  public void testGetCaseDetail_ReturnsData() throws Exception {
    final String caseReferenceNumber = "300000195071";

    mockServer.expect(
            xpath("/msg:CaseInqRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andExpect(
            xpath("/msg:CaseInqRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
                testLoginId))
        .andExpect(xpath("/msg:CaseInqRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
            testUserType))
        .andExpect(
            xpath("/msg:CaseInqRQ/msg:SearchCriteria/msg:CaseReferenceNumber",
                namespaces)
                .evaluatesTo(caseReferenceNumber))
        .andRespond(withPayload(caseInqRS_valid_one));

    CaseInqRS response = client.getCaseDetail(testLoginId, testUserType, caseReferenceNumber);

    assertNotNull(response.getCase());
    assertEquals(caseReferenceNumber, response.getCase().getCaseReferenceNumber());
    assertNotNull(response.getCase().getCaseDetails());

    mockServer.verify();
  }

  @Test
  public void testGetCaseDetail_HandlesError() {
    final String caseReferenceNumber = "300000195071";

    mockServer.expect(
            xpath("/msg:CaseInqRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andRespond(withError("Failed to call soap service"));

    assertThrows(RuntimeException.class, () -> client.getCaseDetail(
        testLoginId, testUserType, caseReferenceNumber));

    mockServer.verify();
  }

  private CaseInfo buildCaseInfo() {
    CaseInfo caseInfo = new CaseInfo();
    caseInfo.setCaseReferenceNumber("caseref");
    caseInfo.setCaseStatus("casestatus");
    caseInfo.setClientSurname("asurname");
    caseInfo.setProviderCaseReferenceNumber("provcaseref");
    caseInfo.setFeeEarnerContactID("123");
    return caseInfo;
  }
}
