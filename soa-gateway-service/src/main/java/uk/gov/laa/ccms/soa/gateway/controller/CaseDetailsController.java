package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.CasesApi;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
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
      CaseDetails caseDetails = caseDetailsService.getCaseDetails(
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
    } catch (Exception e) {
      log.error("CaseDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @Override
  public ResponseEntity<CaseDetail> getCase(
      final String caseReferenceNumber,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole) {
    log.info("GET /cases/{}", caseReferenceNumber);
    try {
      CaseDetail caseDetail = caseDetailsService.getCaseDetail(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          caseReferenceNumber);

      return ResponseEntity.ok(caseDetail);
    } catch (Exception e) {
      log.error("CaseDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @Override
  public ResponseEntity<TransactionStatus> getCaseTransactionStatus(
      final String transactionRequestId,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole) {
    log.info("GET /cases/status/{}", transactionRequestId);
    try {
      TransactionStatus status = caseDetailsService.getCaseTransactionStatus(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          transactionRequestId);

      return ResponseEntity.ok(status);
    } catch (Exception e) {
      log.error("CaseDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

}
