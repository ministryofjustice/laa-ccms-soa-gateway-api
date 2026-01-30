package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.CasesApi;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.CaseTransactionResponse;
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
  public ResponseEntity<CaseTransactionResponse> createCase(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final CaseDetail caseDetail) {
    log.info("POST /cases");
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
   * Amends a single case by reference number.
   *
   * @param soaGatewayUserLoginId (required) - the user requesting the data.
   * @param soaGatewayUserRole    (required) - the user requesting the data.
   * @param caseDetail            Update a case (required)
   * @return a CaseTransactionResponse containing the requests operation result.
   */
  @Override
  public ResponseEntity<CaseTransactionResponse> updateCase(String soaGatewayUserLoginId,
      String soaGatewayUserRole, String caseUpdateType, CaseDetail caseDetail) {
    log.info("PUT /cases");
    try {
      final String transactionId = caseDetailsService.amendCase(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          caseDetail,
          caseUpdateType);
      return ResponseEntity.ok(new CaseTransactionResponse()
          .transactionId(transactionId));
    } catch (final Exception e) {
      log.error("CaseDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
