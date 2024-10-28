package uk.gov.laa.ccms.soa.gateway.client;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.math.BigDecimal;
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
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbim.UpdateUserRS;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbio.CCMSUser;

@SpringBootTest
public class UserClientIntergrationTest {

  @Autowired
  private WebServiceTemplate webServiceTemplate;

  @Autowired
  private UserClient client;

  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/UpdateUserRS_valid.xml")
  Resource updateUserRS_valid;

  private static final String HEADER_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String MSG_NS = "http://legalaid.gsi.gov.uk/CCMS/Common/UserManagement/1.0/UserManagementBIM";
  private static final String USER_NS = "http://legalaid.gsi.gov.uk/CCMS/Common/UserManagement/1.0/UserManagementBIO";
  private static final String COMMON_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";

  private String testLoginId;
  private String testUserType;

  private Map<String, String> namespaces;

  @BeforeEach
  public void createServer() {
    mockServer = MockWebServiceServer.createServer(webServiceTemplate);

    testLoginId = "testLogin";
    testUserType = "testType";

    namespaces = new HashMap<>();
    namespaces.put("header", HEADER_NS);
    namespaces.put("msg", MSG_NS);
    namespaces.put("user", USER_NS);
    namespaces.put("common", COMMON_NS);
  }

  @Test
  public void testGetCaseDetails_ReturnsData() throws Exception {
    CCMSUser ccmsUser = buildCcmsUser();

    mockServer.expect(
            xpath("/msg:UpdateUserRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
        .andExpect(
            xpath("/msg:UpdateUserRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(
                testLoginId))
        .andExpect(xpath("/msg:UpdateUserRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(
            testUserType))
        .andExpect(
            xpath("/msg:UpdateUserRQ/user:CCMSUser/user:LoginID",
                namespaces)
                .evaluatesTo(ccmsUser.getLoginID()))
        .andExpect(
            xpath("/msg:UpdateUserRQ/user:CCMSUser/user:ProviderFirmID",
                namespaces)
                .evaluatesTo(ccmsUser.getProviderFirmID().toString()))
        .andRespond(withPayload(updateUserRS_valid));

    UpdateUserRS response = client.updateUser(testLoginId, testUserType,
        ccmsUser);

    assertNotNull(response.getHeaderRS());
    assertThat(response.getHeaderRS().getStatus().getStatusFreeText(),
        containsString("Profile option XXCCMS_PUI_FIRM value updated with provider firm id as 12345"));

    mockServer.verify();
  }

  private CCMSUser buildCcmsUser() {
    CCMSUser ccmsUser = new CCMSUser();
    ccmsUser.setLoginID(testLoginId);
    ccmsUser.setProviderFirmID(new BigDecimal(12345));
    return ccmsUser;
  }


}
