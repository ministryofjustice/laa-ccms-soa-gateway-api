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
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;

@SpringBootTest
public class ContractDetailsClientImplIntegrationTest {

  @Autowired
  private WebServiceTemplate webServiceTemplate;

  @Autowired
  private ContractDetailsClientImpl client;

  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/ContractDetailsInqRS_valid.xml")
  Resource contractDetailsInqRS_valid;

  private static final String HEADER_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String MSG_NS = "http://legalservices.gov.uk/CCMS/Common/ReferenceData/1.0/ReferenceDataBIM";
  private static final String REFDATA_NS = "http://legalservices.gov.uk/CCMS/Common/ReferenceData/1.0/ReferenceDataBIO";

  private static final String COMMON_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";

  @BeforeEach
  public void createServer() {
    mockServer = MockWebServiceServer.createServer(webServiceTemplate);
  }

  @Test
  public void testGetNotificationCount_ReturnsData() throws Exception {
    final String testLoginId = "testLogin";
    final String testUserType = "testType";
    final int providerFirmId = 123;
    final int officeId = 345;
    final int maxRecords = 50;

    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("header", HEADER_NS);
    namespaces.put("msg", MSG_NS);
    namespaces.put("refdata", REFDATA_NS);
    namespaces.put("common", COMMON_NS);

    mockServer.expect(xpath("/msg:ContractDetailsRQ/header:HeaderRQ/header:TimeStamp", namespaces).exists())
        .andExpect(xpath("/msg:ContractDetailsRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andExpect(xpath("/msg:ContractDetailsRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(testLoginId))
        .andExpect(xpath("/msg:ContractDetailsRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(testUserType))
        .andExpect(xpath("/msg:ContractDetailsRQ/msg:RecordCount/common:MaxRecordsToFetch", namespaces).evaluatesTo(maxRecords))
        .andExpect(xpath("/msg:ContractDetailsRQ/msg:SearchCriteria/msg:FirmID", namespaces).evaluatesTo(providerFirmId))
        .andExpect(xpath("/msg:ContractDetailsRQ/msg:SearchCriteria/msg:OfficeID", namespaces).evaluatesTo(officeId))
        .andRespond(withPayload(contractDetailsInqRS_valid));

    ContractDetailsInqRS response = client.getContractDetails(Integer.toString(providerFirmId),
        Integer.toString(officeId), testLoginId, testUserType,
        maxRecords);

    assertNotNull(response.getContractDetails());
    assertEquals(4, response.getContractDetails().getContractDetail().size());
    assertEquals("MAT", response.getContractDetails().getContractDetail().get(0).getCategoryofLaw());
    assertEquals("Not Applicable", response.getContractDetails().getContractDetail().get(0).getSubCategory());

    mockServer.verify();
  }

  @Test
  public void testGetNotificationCount_HandlesError() {
    final String testLoginId = "testLogin";
    final String testUserType = "testType";
    final int providerFirmId = 123;
    final int officeId = 345;
    final Integer maxRecords = 50;

    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("header", HEADER_NS);
    namespaces.put("msg", MSG_NS);
    namespaces.put("refdata", REFDATA_NS);
    namespaces.put("common", COMMON_NS);

    mockServer.expect(xpath("/msg:ContractDetailsRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andRespond(withError("Failed to call soap service"));

    assertThrows(RuntimeException.class, () -> client.getContractDetails(
        Integer.toString(providerFirmId), Integer.toString(officeId), testLoginId, testUserType,
        maxRecords));

    mockServer.verify();
  }
}
