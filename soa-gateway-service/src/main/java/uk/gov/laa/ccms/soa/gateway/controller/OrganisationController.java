package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.OrganisationsApi;
import uk.gov.laa.ccms.soa.gateway.model.Organisation;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetails;
import uk.gov.laa.ccms.soa.gateway.service.OrganisationService;

/**
 * Controller for managing organisation related requests.
 *
 * <p> Provides endpoints for retrieving a list of organisation based on
 * various search criteria. Implements the{@link OrganisationsApi} to ensure
 * consistent API behavior.</p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class OrganisationController implements OrganisationsApi {

  private final OrganisationService organisationService;

  @Override
  public ResponseEntity<OrganisationDetails> getOrganisations(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String name,
      final String type,
      final String city,
      final String postcode,
      final Integer maxRecords,
      final Pageable pageable) {
    log.info("GET /organisations");
    try {
      Organisation searchOrganisation = new Organisation()
          .name(name)
          .type(type)
          .city(city)
          .postcode(postcode);

      OrganisationDetails organisationDetails = organisationService.getOrganisations(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          maxRecords,
          searchOrganisation,
          pageable);

      return ResponseEntity.ok(organisationDetails);
    } catch (Exception e) {
      log.error("OrganisationController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
