package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import uk.gov.laa.ccms.soa.gateway.model.OpaAttribute;
import uk.gov.laa.ccms.soa.gateway.model.OpaEntity;
import uk.gov.laa.ccms.soa.gateway.model.OpaInstance;
import uk.gov.laa.ccms.soa.gateway.service.InvoicesService;

/**
 * Integration test for the GetInvoiceData SOAP call.
 *
 * <p>This drives the client through {@link InvoicesService} so that both the marshalled request and
 * the unmarshalled entities are checked as EBS actually exchanges them.
 */
@SpringBootTest
public class GetInvoiceDetailsClientIntegrationTest {

  @Autowired private WebServiceTemplate webServiceTemplate;

  @Autowired private InvoicesService invoicesService;

  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/GetInvoiceDetailsRS_valid.xml")
  Resource getInvoiceDetailsRS_valid;

  private static final String HEADER_NS =
      "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String MSG_NS =
      "http://legalservices.gov.uk/CCMS/Finance/Payables/1.0/BillingBIM";
  private static final String BILLING_NS =
      "http://legalservices.gov.uk/CCMS/Finance/Payables/1.0/BillingBIO";

  private static final String BILLING_ID = "1234567890";

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
    namespaces.put("billing", BILLING_NS);
  }

  @Test
  public void testGetInvoiceData_MarshalsRequestAndReadsEntities() throws Exception {
    mockServer
        .expect(
            xpath(
                    "/msg:GetInvoiceDetailsRQ/header:HeaderRQ/header:TransactionRequestID",
                    namespaces)
                .exists())
        .andExpect(
            xpath("/msg:GetInvoiceDetailsRQ/header:HeaderRQ/header:UserLoginID", namespaces)
                .evaluatesTo(testLoginId))
        .andExpect(
            xpath("/msg:GetInvoiceDetailsRQ/header:HeaderRQ/header:UserRole", namespaces)
                .evaluatesTo(testUserType))
        .andExpect(
            xpath("/msg:GetInvoiceDetailsRQ/msg:SearchCondition/billing:BillingId", namespaces)
                .evaluatesTo(BILLING_ID))
        // BasicAssessmentData is the only value the schema permits.
        .andExpect(
            xpath("/msg:GetInvoiceDetailsRQ/msg:ReturnData", namespaces)
                .evaluatesTo("BasicAssessmentData"))
        .andRespond(withPayload(getInvoiceDetailsRS_valid));

    final List<OpaEntity> opaResponse =
        invoicesService.getInvoiceData(testLoginId, testUserType, BILLING_ID);

    assertEquals(1, opaResponse.size());

    final OpaEntity entity = opaResponse.getFirst();
    assertEquals("global", entity.getEntityName());
    assertEquals(1, entity.getInstances().size());

    final OpaInstance instance = entity.getInstances().getFirst();
    assertEquals("global", instance.getInstanceLabel());
    assertEquals(1, instance.getAttributes().size());

    // EBS uses Name/Value/Type here, unlike the attribute submitted with an invoice.
    final OpaAttribute attribute = instance.getAttributes().getFirst();
    assertEquals("BILLING_IS_COMPLETE", attribute.getAttribute());
    assertEquals("true", attribute.getResponseValue());
    assertEquals("boolean", attribute.getResponseType());

    mockServer.verify();
  }
}
