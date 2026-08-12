package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Date;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.laa.ccms.soa.gateway.model.BillDetail;
import uk.gov.laa.ccms.soa.gateway.model.InvoiceDetail;
import uk.gov.laa.ccms.soa.gateway.model.OpaAttribute;
import uk.gov.laa.ccms.soa.gateway.model.OpaEntity;
import uk.gov.laa.ccms.soa.gateway.model.OpaInstance;
import uk.gov.laa.ccms.soa.gateway.model.PaymentOnAccountDetail;
import uk.gov.laa.ccms.soa.gateway.util.DateUtil;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.BillElementType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.OPAAttributesType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.OPAEntityType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.OPAInstanceType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.POAElementType;

/**
 * Mapper interface for converting the REST {@link InvoiceDetail} model into the SOAP {@link
 * InvoiceAddRQ.InvoiceDetails} payload submitted to the EBS {@code CreateInvoice} service.
 *
 * <p>The OPA assessment response is mapped by {@link CaseDetailsMapper}, which already converts the
 * REST assessment result into its SOAP equivalent for case submission.
 *
 * <p>The date fields are all {@code common:Dt} (an {@code xsd:date}), so they are converted to
 * date-only values with no time or offset, matching the old PUI.
 */
@Mapper(componentModel = "spring", uses = CaseDetailsMapper.class)
public interface InvoiceMapper {

  /**
   * Map the REST invoice to the SOAP invoice details.
   *
   * <p>EBS models the invoice details as a choice, so a bill and a payment on account are never
   * submitted together. A bill takes precedence if both are somehow supplied.
   *
   * @param invoiceDetail the invoice to submit.
   * @return the mapped {@link InvoiceAddRQ.InvoiceDetails}, or null if the input is null.
   */
  default InvoiceAddRQ.InvoiceDetails toInvoiceDetails(final InvoiceDetail invoiceDetail) {
    if (invoiceDetail == null) {
      return null;
    }

    final InvoiceAddRQ.InvoiceDetails invoiceDetails = new InvoiceAddRQ.InvoiceDetails();

    if (invoiceDetail.getBill() != null) {
      invoiceDetails.getBill().add(toBillElementType(invoiceDetail.getBill()));
    } else if (invoiceDetail.getPoa() != null) {
      invoiceDetails.getPOA().add(toPoaElementType(invoiceDetail.getPoa()));
    }

    return invoiceDetails;
  }

  /**
   * Map a REST bill to its SOAP equivalent.
   *
   * @param billDetail the bill to map.
   * @return the mapped {@link BillElementType}.
   */
  @Mapping(target = "providerFirmID", source = "providerFirmId")
  @Mapping(target = "OPAResponse", source = "opaResponse")
  @Mapping(
      target = "dateSentToClient",
      source = "dateSentToClient",
      qualifiedByName = "toXmlDateOnly")
  @Mapping(
      target = "courtAssessmentDate",
      source = "courtAssessmentDate",
      qualifiedByName = "toXmlDateOnly")
  BillElementType toBillElementType(BillDetail billDetail);

  /**
   * Map a REST payment on account to its SOAP equivalent.
   *
   * @param paymentOnAccountDetail the payment on account to map.
   * @return the mapped {@link POAElementType}.
   */
  @Mapping(target = "providerID", source = "providerId")
  @Mapping(target = "VATRate", source = "vatRate")
  @Mapping(target = "OPAResponse", source = "opaResponse")
  @Mapping(target = "dateIncurred", source = "dateIncurred", qualifiedByName = "toXmlDateOnly")
  @Mapping(
      target = "dtldAssessmentOrderDate",
      source = "dtldAssessmentOrderDate",
      qualifiedByName = "toXmlDateOnly")
  POAElementType toPoaElementType(PaymentOnAccountDetail paymentOnAccountDetail);

  /**
   * Map the OPA entities EBS returns for an invoice to their REST equivalents.
   *
   * <p>The {@code GetInvoiceDetails} response carries only the OPA entities, with no goal and no
   * assessment screens, so this is deliberately not an {@link
   * uk.gov.laa.ccms.soa.gateway.model.AssessmentResult}.
   *
   * @param entities the SOAP entities to map.
   * @return the mapped entities, empty if none were supplied.
   */
  List<OpaEntity> toOpaEntities(List<OPAEntityType> entities);

  /**
   * Map a single OPA entity to its REST equivalent.
   *
   * @param entity the SOAP entity to map.
   * @return the mapped {@link OpaEntity}.
   */
  @Mapping(target = "sequenceNumber", ignore = true)
  @Mapping(target = "caption", ignore = true)
  OpaEntity toOpaEntity(OPAEntityType entity);

  /**
   * Map an OPA instance to its REST equivalent, flattening the wrapper EBS puts around the
   * attributes.
   *
   * @param instance the SOAP instance to map.
   * @return the mapped {@link OpaInstance}.
   */
  @Mapping(target = "caption", ignore = true)
  @Mapping(target = "attributes", source = "attributes.attribute")
  OpaInstance toOpaInstance(OPAInstanceType instance);

  /**
   * Map an OPA attribute to its REST equivalent. EBS uses a narrower attribute here than the one
   * submitted with an invoice, so only the name, value and type are available.
   *
   * @param attribute the SOAP attribute to map.
   * @return the mapped {@link OpaAttribute}.
   */
  @Mapping(target = "attribute", source = "name")
  @Mapping(target = "responseValue", source = "value")
  @Mapping(target = "responseType", source = "type")
  @Mapping(target = "caption", ignore = true)
  @Mapping(target = "responseText", ignore = true)
  @Mapping(target = "userDefinedInd", ignore = true)
  OpaAttribute toOpaAttribute(OPAAttributesType attribute);

  /**
   * Convert a {@link Date} to a date-only {@link XMLGregorianCalendar}, so that no time or offset
   * is sent for the {@code xsd:date} fields EBS expects.
   *
   * @param date the date to convert.
   * @return the date-only {@link XMLGregorianCalendar}, or null if {@code date} is null.
   */
  @Named("toXmlDateOnly")
  default XMLGregorianCalendar toXmlDateOnly(final Date date) {
    try {
      return DateUtil.convertDateToXmlDateOnly(date);
    } catch (final DatatypeConfigurationException e) {
      throw new SoaGatewayMappingException("Unable to convert date.", e);
    }
  }
}
