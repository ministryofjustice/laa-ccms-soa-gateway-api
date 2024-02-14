package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ReferenceDataClient;
import uk.gov.laa.ccms.soa.gateway.mapper.CommonMapper;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;

/**
 * Service class dedicated to handling reference data-related operations.
 *
 * <p>This service interfaces with the external Reference Data system through
 * {@link ReferenceDataClient} and converts the results into a structured form using
 * {@link CommonMapper}. Its primary responsibility is to fetch
 * case reference summaries based on SOA Gateway user credentials.</p>
 */
@Service
@RequiredArgsConstructor
public class ReferenceDataService {

  private final ReferenceDataClient referenceDataClient;

  private final CommonMapper commonMapper;

  /**
   * Retrieves a summary of case references based on SOA Gateway user credentials.
   *
   * <p>Fetches the next case reference from the Reference Data system. It then maps the raw
   * response into a structured form, {@link CaseReferenceSummary}.</p>
   *
   * @param soaGatewayUserLoginId   The SOA Gateway user login ID.
   * @param soaGatewayUserRole      The SOA Gateway user role.
   * @return                        A {@link CaseReferenceSummary} representing the summary
   *                                of case references.
   */
  public CaseReferenceSummary getCaseReference(final String soaGatewayUserLoginId,
                                               final String soaGatewayUserRole) {

    ReferenceDataInqRS response = referenceDataClient.getCaseReference(
            soaGatewayUserLoginId,
            soaGatewayUserRole);

    return commonMapper.toCaseReferenceSummary(response);
  }


}
