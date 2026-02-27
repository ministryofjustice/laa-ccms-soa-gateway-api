package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.ContractDetailsApi;
import uk.gov.laa.ccms.soa.gateway.model.ContractDetails;
import uk.gov.laa.ccms.soa.gateway.service.ContractDetailsService;

/**
 * Controller that manages the retrieval of contract details.
 *
 * <p>Responsible for handling contract-related operations, especially focused on retrieving details
 * for contracts based on various criteria.
 *
 * <p>Acts as an implementation of the {@link ContractDetailsApi}, utilizing the {@link
 * ContractDetailsService} to process business logic and fetch relevant contract data.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ContractDetailsController implements ContractDetailsApi {

  private final ContractDetailsService contractDetailsService;

  /**
   * Get contract details for the provided search criteria.
   *
   * @param providerFirmId (required) - the provider firm id search criteria.
   * @param officeId (required) - the office id search criteria.
   * @param soaGatewayUserLoginId (required) - the user requesting the data.
   * @param soaGatewayUserRole (required) - the user role requesting the data.
   * @param maxRecords (optional, default to 100) - the maximum records to query.
   * @return ResponseEntity wrapping the contract details results.
   */
  @Override
  public ResponseEntity<ContractDetails> getContractDetails(
      final Integer providerFirmId,
      final Integer officeId,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final Integer maxRecords) {

    try {
      ContractDetails contractDetails =
          contractDetailsService.getContractDetails(
              providerFirmId, officeId, soaGatewayUserLoginId, soaGatewayUserRole, maxRecords);
      return ResponseEntity.ok(contractDetails);
    } catch (Exception e) {
      log.error("ContractDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
