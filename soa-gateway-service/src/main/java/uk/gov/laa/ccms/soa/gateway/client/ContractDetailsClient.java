package uk.gov.laa.ccms.soa.gateway.client;

import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;

/**
 * Provides a client interface for interacting with Contract Details.
 */
public interface ContractDetailsClient {

  /**
   * Retrieve contract details based on the provided search criteria.
   *
   * @param searchFirmId     The firm ID for the search.
   * @param searchOfficeId   The office ID for the search.
   * @param loggedInUserId   The logged in user ID.
   * @param loggedInUserType The type of the logged in user.
   * @param maxRecords       Maximum number of records to fetch.
   * @return ContractDetailsInqRS  Response containing contract details.
   */
  ContractDetailsInqRS getContractDetails(final String searchFirmId, final String searchOfficeId,
      final String loggedInUserId, final String loggedInUserType, final Integer maxRecords);


}
