package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.CasesApi;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
import uk.gov.laa.ccms.soa.gateway.service.CaseDetailsService;

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

}
