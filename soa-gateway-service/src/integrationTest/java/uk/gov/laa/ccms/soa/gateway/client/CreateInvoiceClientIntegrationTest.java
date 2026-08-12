package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
import uk.gov.laa.ccms.soa.gateway.model.AssessmentResult;
import uk.gov.laa.ccms.soa.gateway.model.BillDetail;
import uk.gov.laa.ccms.soa.gateway.model.InvoiceDetail;
import uk.gov.laa.ccms.soa.gateway.model.PaymentOnAccountDetail;
import uk.gov.laa.ccms.soa.gateway.service.InvoicesService;

/**
 * Integration test for the CreateInvoice SOAP call.
 *
 * <p>This drives the client through {@link InvoicesService} so that the REST invoice model is
 * marshalled all the way to the wire, letting the request XPath assertions check the mapping as EBS
 * actually receives it. In particular the {@code common:Dt} fields must go out as bare {@code
 * xsd:date} values, with no time and no offset.
 */
@SpringBootTest
public class CreateInvoiceClientIntegrationTest {

  @Autowired private WebServiceTemplate webServiceTemplate;

  @Autowired private InvoicesService invoicesService;

  private static MockWebServiceServer mockServer;

  @Value("classpath:/payload/InvoiceAddRS_valid.xml")
  Resource invoiceAddRS_valid;

  private static final String HEADER_NS =
      "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
  private static final String MSG_NS =
      "http://legalservices.gov.uk/CCMS/Finance/Payables/1.0/BillingBIM";
  private static final String BILLING_NS =
      "http://legalservices.gov.uk/CCMS/Finance/Payables/1.0/BillingBIO";
  private static final String COMMON_NS =
      "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";

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
    namespaces.put("common", COMMON_NS);
  }

  private static Date toDate(int year, int month, int day) {
    return Date.from(
        LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  @Test
  public void testCreateInvoice_Poa_MarshalsRequest() throws Exception {
    PaymentOnAccountDetail poa = new PaymentOnAccountDetail();
    poa.setProviderId("12345");
    poa.setCaseReferenceNumber("300000123456");
    poa.setReason("thereason");
    poa.setCourtType("thecourttype");
    poa.setDateIncurred(toDate(2026, 3, 14));
    poa.setActualNetCost(new BigDecimal("100.50"));
    poa.setVatRate("20");
    poa.setDtldAssessmentOrderDate(toDate(2026, 4, 15));
    poa.setNotes("thenotes");
    poa.setCalculatedNetCost(new BigDecimal("90.25"));
    poa.setActualTotalCost(new BigDecimal("120.60"));
    poa.setOpaResponse(new AssessmentResult().assessmentId("assess123"));

    mockServer
        .expect(
            xpath("/msg:InvoiceAddRQ/header:HeaderRQ/header:TransactionRequestID", namespaces)
                .exists())
        .andExpect(
            xpath("/msg:InvoiceAddRQ/header:HeaderRQ/header:UserLoginID", namespaces)
                .evaluatesTo(testLoginId))
        .andExpect(
            xpath("/msg:InvoiceAddRQ/header:HeaderRQ/header:UserRole", namespaces)
                .evaluatesTo(testUserType))
        // The invoice details are a choice, so no Bill is sent alongside the POA.
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill", namespaces).doesNotExist())
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:ProviderID", namespaces)
                .evaluatesTo("12345"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:CaseReferenceNumber",
                    namespaces)
                .evaluatesTo("300000123456"))
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:Reason", namespaces)
                .evaluatesTo("thereason"))
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:CourtType", namespaces)
                .evaluatesTo("thecourttype"))
        // EBS rejects a time or an offset on these xsd:date fields.
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:DateIncurred", namespaces)
                .evaluatesTo("2026-03-14"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:DtldAssessmentOrderDate",
                    namespaces)
                .evaluatesTo("2026-04-15"))
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:ActualNetCost", namespaces)
                .evaluatesTo("100.50"))
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:VATRate", namespaces)
                .evaluatesTo("20"))
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:Notes", namespaces)
                .evaluatesTo("thenotes"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:CalculatedNetCost",
                    namespaces)
                .evaluatesTo("90.25"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:ActualTotalCost",
                    namespaces)
                .evaluatesTo("120.60"))
        // The OPA response is mapped by CaseDetailsMapper, so check it is wired in and sent.
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA/billing:OPAResponse"
                        + "/common:AssesmentID",
                    namespaces)
                .evaluatesTo("assess123"))
        .andRespond(withPayload(invoiceAddRS_valid));

    String invoiceReferenceId =
        invoicesService.createInvoice(testLoginId, testUserType, new InvoiceDetail().poa(poa));

    assertEquals("invoice123", invoiceReferenceId);

    mockServer.verify();
  }

  @Test
  public void testCreateInvoice_Bill_MarshalsRequest() throws Exception {
    BillDetail bill = new BillDetail();
    bill.setCaseReferenceNumber("300000123456");
    bill.setProviderFirmId("12345");
    bill.setTypeOfBill("CLAIM");
    bill.setSupportingInfo("thesupportinginfo");
    bill.setClientApproval(true);
    bill.setDateSentToClient(toDate(2026, 3, 14));
    bill.setClientResponse("theclientresponse");
    bill.setClientObjectionReason("theobjectionreason");
    bill.setCourtCode("thecourtcode");
    bill.setCourtAssessment(false);
    bill.setCourtAssessmentDate(toDate(2026, 4, 15));
    bill.setOpaResponse(new AssessmentResult().assessmentId("assess123"));

    mockServer
        .expect(
            xpath("/msg:InvoiceAddRQ/header:HeaderRQ/header:UserLoginID", namespaces)
                .evaluatesTo(testLoginId))
        .andExpect(
            xpath("/msg:InvoiceAddRQ/header:HeaderRQ/header:UserRole", namespaces)
                .evaluatesTo(testUserType))
        // The invoice details are a choice, so no POA is sent alongside the Bill.
        .andExpect(xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:POA", namespaces).doesNotExist())
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:CaseReferenceNumber",
                    namespaces)
                .evaluatesTo("300000123456"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:ProviderFirmID",
                    namespaces)
                .evaluatesTo("12345"))
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:TypeOfBill", namespaces)
                .evaluatesTo("CLAIM"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:SupportingInfo",
                    namespaces)
                .evaluatesTo("thesupportinginfo"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:ClientApproval",
                    namespaces)
                .evaluatesTo("true"))
        // EBS rejects a time or an offset on these xsd:date fields.
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:DateSentToClient",
                    namespaces)
                .evaluatesTo("2026-03-14"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:CourtAssessmentDate",
                    namespaces)
                .evaluatesTo("2026-04-15"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:ClientResponse",
                    namespaces)
                .evaluatesTo("theclientresponse"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:ClientObjectionReason",
                    namespaces)
                .evaluatesTo("theobjectionreason"))
        .andExpect(
            xpath("/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:CourtCode", namespaces)
                .evaluatesTo("thecourtcode"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:CourtAssessment",
                    namespaces)
                .evaluatesTo("false"))
        .andExpect(
            xpath(
                    "/msg:InvoiceAddRQ/msg:InvoiceDetails/msg:Bill/billing:OPAResponse"
                        + "/common:AssesmentID",
                    namespaces)
                .evaluatesTo("assess123"))
        .andRespond(withPayload(invoiceAddRS_valid));

    String invoiceReferenceId =
        invoicesService.createInvoice(testLoginId, testUserType, new InvoiceDetail().bill(bill));

    assertEquals("invoice123", invoiceReferenceId);

    mockServer.verify();
  }
}
