package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.CaseReferenceApi;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;
import uk.gov.laa.ccms.soa.gateway.service.ReferenceDataService;

/**
 * Controller responsible for managing reference data related to case references.
 *
 * <p>Facilitates the retrieval of case reference data based on user information. This
 * controller ensures that users receive case reference data tailored to their roles and
 * access levels as per the {@link CaseReferenceApi} definition.</p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ReferenceDataController implements CaseReferenceApi {

  private final ReferenceDataService referenceDataService;

  @Override
  public ResponseEntity<CaseReferenceSummary> getCaseReference(final String soaGatewayUserLoginId,
                                                               final String soaGatewayUserRole) {
    try {
      CaseReferenceSummary caseReferenceSummary = referenceDataService.getCaseReference(
              soaGatewayUserLoginId,
              soaGatewayUserRole);
      return ResponseEntity.ok(caseReferenceSummary);

    } catch (Exception e) {
      log.error("Reference Data Controller caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
