package uk.gov.laa.ccms.soa.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.laa.ccms.soa.gateway.model.CaseStatementOfAccount;
import uk.gov.laa.ccms.soa.gateway.model.SoaAmountSummary;
import uk.gov.laa.ccms.soa.gateway.model.SoaInvoice;
import uk.gov.laa.ccms.soa.gateway.model.SoaStatement;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.InvoiceListElementType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.SOADetailElementType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.SOAElementType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.SOASummaryElementType;

/**
 * Mapper interface for converting the SOAP {@link SOAElementType} statement of account into the
 * REST {@link CaseStatementOfAccount} model.
 *
 * <p>Amounts map straight across ({@code BigDecimal}) and SOAP {@code XMLGregorianCalendar} dates
 * convert to {@code java.util.Date} via MapStruct's built-in conversions. Only the fields whose
 * SOAP getter casing or wrapping differs from the model need explicit mappings.
 */
@Mapper(componentModel = "spring")
public interface CaseStatementOfAccountMapper {

  /**
   * Map the SOAP statement of account element to the REST model.
   *
   * @param soaElement the SOAP {@link SOAElementType}.
   * @return the mapped {@link CaseStatementOfAccount}, or null if the input is null.
   */
  @Mapping(target = "statements", source = "details.statement")
  CaseStatementOfAccount toCaseStatementOfAccount(SOAElementType soaElement);

  @Mapping(target = "poa", source = "POA")
  SoaAmountSummary toSoaAmountSummary(SOASummaryElementType summary);

  @Mapping(target = "firmId", source = "firmID")
  @Mapping(target = "poa", source = "POA")
  @Mapping(target = "invoices", source = "invoiceList.invoice")
  SoaStatement toSoaStatement(SOADetailElementType detail);

  SoaInvoice toSoaInvoice(InvoiceListElementType invoice);
}
