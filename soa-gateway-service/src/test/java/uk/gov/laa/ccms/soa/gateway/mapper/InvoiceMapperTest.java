package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentResult;
import uk.gov.laa.ccms.soa.gateway.model.BillDetail;
import uk.gov.laa.ccms.soa.gateway.model.InvoiceDetail;
import uk.gov.laa.ccms.soa.gateway.model.PaymentOnAccountDetail;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.BillElementType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.POAElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;

@ExtendWith(MockitoExtension.class)
class InvoiceMapperTest {

  // The assessment result conversion itself is covered by CaseDetailsMapperTest, so here we only
  // check that it is delegated to.
  @Mock CaseDetailsMapper caseDetailsMapper;

  @InjectMocks InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

  private static Date toDate(final int year, final int month, final int day) {
    return Date.from(
        LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private static void assertDateOnly(
      final XMLGregorianCalendar actual, final int year, final int month, final int day) {
    assertNotNull(actual);
    assertEquals(year, actual.getYear());
    assertEquals(month, actual.getMonth());
    assertEquals(day, actual.getDay());
    // EBS rejects a time or an offset on these xsd:date fields.
    assertEquals(DatatypeConstants.FIELD_UNDEFINED, actual.getHour());
    assertEquals(DatatypeConstants.FIELD_UNDEFINED, actual.getTimezone());
  }

  @Test
  @DisplayName("toInvoiceDetails - maps a payment on account")
  void toInvoiceDetails_mapsPoa() {
    final PaymentOnAccountDetail poa = new PaymentOnAccountDetail();
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
    final AssessmentResult opaResponse = new AssessmentResult().assessmentId("assess123");
    poa.setOpaResponse(opaResponse);

    final AssesmentResultType mappedOpaResponse = new AssesmentResultType();
    when(caseDetailsMapper.toAssesmentResultType(opaResponse)).thenReturn(mappedOpaResponse);

    final InvoiceAddRQ.InvoiceDetails result =
        invoiceMapper.toInvoiceDetails(new InvoiceDetail().poa(poa));

    assertNotNull(result);
    assertTrue(result.getBill().isEmpty());
    assertEquals(1, result.getPOA().size());

    final POAElementType mapped = result.getPOA().getFirst();
    assertEquals("12345", mapped.getProviderID());
    assertEquals("300000123456", mapped.getCaseReferenceNumber());
    assertEquals("thereason", mapped.getReason());
    assertEquals("thecourttype", mapped.getCourtType());
    assertDateOnly(mapped.getDateIncurred(), 2026, 3, 14);
    assertEquals(new BigDecimal("100.50"), mapped.getActualNetCost());
    assertEquals("20", mapped.getVATRate());
    assertDateOnly(mapped.getDtldAssessmentOrderDate(), 2026, 4, 15);
    assertEquals("thenotes", mapped.getNotes());
    assertEquals(new BigDecimal("90.25"), mapped.getCalculatedNetCost());
    assertEquals(new BigDecimal("120.60"), mapped.getActualTotalCost());
    assertEquals(mappedOpaResponse, mapped.getOPAResponse());
  }

  @Test
  @DisplayName("toInvoiceDetails - maps a bill")
  void toInvoiceDetails_mapsBill() {
    final BillDetail bill = new BillDetail();
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
    final AssessmentResult opaResponse = new AssessmentResult().assessmentId("assess123");
    bill.setOpaResponse(opaResponse);

    final AssesmentResultType mappedOpaResponse = new AssesmentResultType();
    when(caseDetailsMapper.toAssesmentResultType(opaResponse)).thenReturn(mappedOpaResponse);

    final InvoiceAddRQ.InvoiceDetails result =
        invoiceMapper.toInvoiceDetails(new InvoiceDetail().bill(bill));

    assertNotNull(result);
    assertTrue(result.getPOA().isEmpty());
    assertEquals(1, result.getBill().size());

    final BillElementType mapped = result.getBill().getFirst();
    assertEquals("300000123456", mapped.getCaseReferenceNumber());
    assertEquals("12345", mapped.getProviderFirmID());
    assertEquals("CLAIM", mapped.getTypeOfBill());
    assertEquals("thesupportinginfo", mapped.getSupportingInfo());
    assertEquals(Boolean.TRUE, mapped.isClientApproval());
    assertDateOnly(mapped.getDateSentToClient(), 2026, 3, 14);
    assertEquals("theclientresponse", mapped.getClientResponse());
    assertEquals("theobjectionreason", mapped.getClientObjectionReason());
    assertEquals("thecourtcode", mapped.getCourtCode());
    assertEquals(Boolean.FALSE, mapped.isCourtAssessment());
    assertDateOnly(mapped.getCourtAssessmentDate(), 2026, 4, 15);
    assertEquals(mappedOpaResponse, mapped.getOPAResponse());
  }

  @Test
  @DisplayName("toInvoiceDetails - a bill takes precedence over a payment on account")
  void toInvoiceDetails_billTakesPrecedence() {
    final InvoiceDetail invoiceDetail =
        new InvoiceDetail()
            .bill(new BillDetail().caseReferenceNumber("300000123456"))
            .poa(new PaymentOnAccountDetail().caseReferenceNumber("300000123456"));

    final InvoiceAddRQ.InvoiceDetails result = invoiceMapper.toInvoiceDetails(invoiceDetail);

    assertNotNull(result);
    assertEquals(1, result.getBill().size());
    assertTrue(result.getPOA().isEmpty());
  }

  @Test
  @DisplayName("toInvoiceDetails - empty when neither a bill nor a payment on account is supplied")
  void toInvoiceDetails_empty() {
    final InvoiceAddRQ.InvoiceDetails result = invoiceMapper.toInvoiceDetails(new InvoiceDetail());

    assertNotNull(result);
    assertTrue(result.getBill().isEmpty());
    assertTrue(result.getPOA().isEmpty());
  }

  @Test
  @DisplayName("toInvoiceDetails - null when the invoice is null")
  void toInvoiceDetails_null() {
    assertNull(invoiceMapper.toInvoiceDetails(null));
  }

  @Test
  @DisplayName("toXmlDateOnly - null when the date is null")
  void toXmlDateOnly_null() {
    assertNull(invoiceMapper.toXmlDateOnly(null));
  }
}
