package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ContractDetailsClient;
import uk.gov.laa.ccms.soa.gateway.client.ContractDetailsClientImpl;
import uk.gov.laa.ccms.soa.gateway.mapper.ContractDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ContractDetails;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;

/**
 * Service class responsible for fetching contract details from the external system.
 *
 * <p>This service class interfaces with the external Contract Details system through {@link
 * ContractDetailsClientImpl} and maps the results into a more manageable form using {@link
 * ContractDetailsMapper}. It provides methods to fetch specific contract details based on various
 * search criteria and user authentication details.
 */
@Service
@RequiredArgsConstructor
public class ContractDetailsService {

  private final ContractDetailsClient contractDetailsClient;

  private final ContractDetailsMapper contractDetailsMapper;

  /**
   * Retrieves contract details based on search criteria related to firm and office identifiers.
   *
   * <p>This method communicates with the Contract Details system to fetch specific contract details
   * using provided search criteria. It uses the search firm ID and search office ID along with user
   * credentials and other optional parameters to narrow down the search results.
   *
   * @param searchFirmId The ID of the firm to search for.
   * @param searchOfficeId The ID of the office to search for.
   * @param soaGatewayUserLoginId The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole The user role in the SOA Gateway.
   * @param maxRecords The maximum number of records to retrieve.
   * @return A {@link ContractDetails} object containing the details of the contracts that match the
   *     search criteria.
   */
  public ContractDetails getContractDetails(
      final Integer searchFirmId,
      final Integer searchOfficeId,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final Integer maxRecords) {
    ContractDetailsInqRS response =
        contractDetailsClient.getContractDetails(
            searchFirmId.toString(),
            searchOfficeId.toString(),
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords);

    return contractDetailsMapper.toContractDetails(response);
  }
}
