package uk.gov.laa.ccms.soa.gateway.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.CreateInvoiceClient;
import uk.gov.laa.ccms.soa.gateway.client.GetInvoiceDetailsClient;
import uk.gov.laa.ccms.soa.gateway.mapper.InvoiceMapper;
import uk.gov.laa.ccms.soa.gateway.model.InvoiceDetail;
import uk.gov.laa.ccms.soa.gateway.model.OpaEntity;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.GetInvoiceDetailsRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRS;

/** Service for handling invoice submissions. */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoicesService extends AbstractSoaService {

  private final CreateInvoiceClient createInvoiceClient;

  private final GetInvoiceDetailsClient getInvoiceDetailsClient;

  private final InvoiceMapper invoiceMapper;

  /**
   * Submits an invoice and returns the invoice reference id.
   *
   * @param soaGatewayUserLoginId the login ID of the SOA gateway user
   * @param soaGatewayUserRole the role of the SOA gateway user
   * @param invoiceDetail the bill or payment on account to submit
   * @return the invoice reference id of the submitted invoice
   */
  public String createInvoice(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final InvoiceDetail invoiceDetail) {
    log.info("InvoicesService - createInvoice");

    final InvoiceAddRQ.InvoiceDetails invoiceDetails =
        invoiceMapper.toInvoiceDetails(invoiceDetail);

    final InvoiceAddRS response =
        createInvoiceClient.createInvoice(
            soaGatewayUserLoginId, soaGatewayUserRole, invoiceDetails);

    return response.getInvoiceReferenceID();
  }

  /**
   * Retrieves the OPA assessment data held against a billing id.
   *
   * @param soaGatewayUserLoginId the login ID of the SOA gateway user
   * @param soaGatewayUserRole the role of the SOA gateway user
   * @param billingId the billing id to retrieve the assessment data for
   * @return the assessment entities, or null if EBS holds no assessment data for the billing id
   */
  public List<OpaEntity> getInvoiceData(
      final String soaGatewayUserLoginId, final String soaGatewayUserRole, final String billingId) {
    log.info("InvoicesService - getInvoiceData");

    final GetInvoiceDetailsRS response =
        getInvoiceDetailsClient.getInvoiceData(
            soaGatewayUserLoginId, soaGatewayUserRole, billingId);

    // EBS makes the assessment results optional, and returns them absent for an unknown billing id.
    if (response.getAssessmentResultsElement() == null) {
      return null;
    }

    return invoiceMapper.toOpaEntities(response.getAssessmentResultsElement().getEntity());
  }
}
