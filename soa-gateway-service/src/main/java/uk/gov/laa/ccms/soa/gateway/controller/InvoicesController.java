package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.InvoicesApi;
import uk.gov.laa.ccms.soa.gateway.model.InvoiceDetail;
import uk.gov.laa.ccms.soa.gateway.model.InvoiceResponse;
import uk.gov.laa.ccms.soa.gateway.service.InvoicesService;

/** REST controller for handling invoice submissions. */
@RestController
@RequiredArgsConstructor
@Slf4j
public class InvoicesController implements InvoicesApi {

  private final InvoicesService invoicesService;

  /**
   * Handles submission of an invoice.
   *
   * @param soaGatewayUserLoginId the login ID of the SOA gateway user
   * @param soaGatewayUserRole the role of the SOA gateway user
   * @param invoiceDetail the bill or payment on account to submit
   * @return a response entity containing the invoice reference id, a bad request if neither or both
   *     of the bill and payment on account were supplied, or an internal server error
   */
  @Override
  public ResponseEntity<InvoiceResponse> createInvoice(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final InvoiceDetail invoiceDetail) {
    log.info("POST /invoices");

    // EBS models the invoice details as a choice, so exactly one of the two must be supplied.
    if (invoiceDetail == null
        || (invoiceDetail.getBill() == null) == (invoiceDetail.getPoa() == null)) {
      log.error("POST /invoices requires exactly one of bill or poa");
      return ResponseEntity.badRequest().build();
    }

    try {
      final String invoiceReferenceId =
          invoicesService.createInvoice(soaGatewayUserLoginId, soaGatewayUserRole, invoiceDetail);
      return ResponseEntity.ok(new InvoiceResponse().invoiceReferenceId(invoiceReferenceId));
    } catch (final Exception e) {
      log.error("InvoicesController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
