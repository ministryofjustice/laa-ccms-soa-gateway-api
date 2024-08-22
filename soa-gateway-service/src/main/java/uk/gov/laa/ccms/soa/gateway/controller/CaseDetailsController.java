package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.CasesApi;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
import uk.gov.laa.ccms.soa.gateway.model.CaseTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.TransactionStatus;
import uk.gov.laa.ccms.soa.gateway.service.CaseDetailsService;

/**
 * Controller for handling case details related requests.
 *
 * <p>Provides an endpoint for retrieving detailed case information based on various search
 * criteria. Implements the {@link CasesApi} for consistent behavior with other API
 * implementations.</p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CaseDetailsController implements CasesApi {

  private final CaseDetailsService caseDetailsService;

  /**
   * Get a paged list of cases for the provided search criteria.
   *
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param caseReferenceNumber  (optional) - the case reference number search criteria.
   * @param providerCaseReference  (optional) - the provider case reference search criteria.
   * @param caseStatus  (optional) - the case status search criteria.
   * @param clientSurname  (optional) - the client surname search criteria.
   * @param feeEarnerId  (optional) - the fee earner id search criteria
   * @param officeId  (optional) - the office id search criteria.
   * @param maxRecords  (optional, default to 100) - the maximum records to query.
   * @param pageable - the page settings.
   * @return ResponseEntity containing CaseDetails.
   */
  @Override
  public ResponseEntity<CaseDetails> getCases(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String caseReferenceNumber,
      final String providerCaseReference,
      final String caseStatus,
      final String clientSurname,
      final Integer feeEarnerId,
      final Integer officeId,
      final Integer maxRecords,
      final Pageable pageable) {
    log.info("GET /cases");
    try {
      final CaseDetails caseDetails = caseDetailsService.getCaseDetails(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          caseReferenceNumber,
          providerCaseReference,
          caseStatus,
          clientSurname,
          feeEarnerId,
          officeId,
          maxRecords,
          pageable);

      return ResponseEntity.ok(caseDetails);
    } catch (final Exception e) {
      log.error("CaseDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @Override
  public ResponseEntity<CaseTransactionResponse> createCases(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final CaseDetail caseDetail) {
    log.info("GET /cases");
    try {
      final String transactionId = caseDetailsService.registerCase(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          caseDetail);
      return ResponseEntity.ok(new CaseTransactionResponse()
          .transactionId(transactionId));
    } catch (final Exception e) {
      log.error("CaseDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Get a single case by reference number.
   *
   * @param caseReferenceNumber  (required) - the reference of the case to return.
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @return a ResponseEntity containing the case detail.
   */
  @Override
  public ResponseEntity<CaseDetail> getCase(
      final String caseReferenceNumber,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole) {
    log.info("GET /cases/{}", caseReferenceNumber);
    try {
      final CaseDetail caseDetail = caseDetailsService.getCaseDetail(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          caseReferenceNumber);

      return ResponseEntity.ok(caseDetail);
    } catch (final Exception e) {
      log.error("CaseDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Get the status of a case transaction.
   *
   * @param transactionRequestId  (required) - the id of the transaction.
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @return a ResponseEntity containing the transaction status details.
   */
  @Override
  public ResponseEntity<TransactionStatus> getCaseTransactionStatus(
      final String transactionRequestId,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole) {
    log.info("GET /cases/status/{}", transactionRequestId);
    try {
      final TransactionStatus status = caseDetailsService.getCaseTransactionStatus(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          transactionRequestId);

      return ResponseEntity.ok(status);
    } catch (final Exception e) {
      log.error("CaseDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

}
