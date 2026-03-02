package uk.gov.laa.ccms.soa.gateway.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.CommonOrgClient;
import uk.gov.laa.ccms.soa.gateway.mapper.OrganisationMapper;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetail;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetails;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationSummary;
import uk.gov.laa.ccms.soa.gateway.util.PaginationUtil;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ.SearchCriteria.Organization;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS;

/**
 * Service class responsible for fetching and processing organisations.
 *
 * <p>This service interacts with the external CommonOrg system to fetch organisations. It then
 * processes and converts these details to the desired format using the {@link OrganisationMapper}.
 * Pagination of results is handled by the {@link PaginationUtil} utility.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrganisationService extends AbstractSoaService {

  private final CommonOrgClient commonOrgClient;

  private final OrganisationMapper organisationMapper;

  /**
   * Retrieves organisations based on the provided search criteria.
   *
   * <p>This method communicates with the external CommonOrg system using the provided search
   * criteria, fetches the relevant organisations, and then maps and paginates these details to the
   * desired format.
   *
   * @param soaGatewayUserLoginId The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole The user role in the SOA Gateway.
   * @param maxRecords The maximum number of records to retrieve.
   * @param searchOrganisation The organisation search criteria
   * @param pageable The pagination details.
   * @return An {@link OrganisationDetails} object containing the retrieved organisations.
   */
  public OrganisationDetails getOrganisations(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final Integer maxRecords,
      final OrganisationSummary searchOrganisation,
      final Pageable pageable) {
    log.info("OrganisationService - getOrganisations");
    final Organization organization = organisationMapper.toOrganization(searchOrganisation);

    final CommonOrgInqRS response =
        commonOrgClient.getOrganisations(
            soaGatewayUserLoginId, soaGatewayUserRole, maxRecords, organization);

    final List<OrganisationSummary> organisationList =
        organisationMapper.toOrganisationSummaryList(response);

    final int listSize =
        getTotalElementsFromRecordCount(response.getRecordCount(), organisationList.size());

    final Page<OrganisationSummary> page =
        PaginationUtil.paginateList(pageable, organisationList, listSize);

    return organisationMapper.toOrganisationDetails(page);
  }

  /**
   * Retrieves the full details for an organisation based on its party id.
   *
   * <p>This method communicates with the external CommonOrg system using the provided search
   * criteria, fetches the relevant organisation, and then maps these details to the desired format.
   *
   * @param soaGatewayUserLoginId The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole The user role in the SOA Gateway.
   * @param maxRecords The maximum number of records to retrieve.
   * @param organisationId The organisation id.
   * @return An {@link OrganisationDetail} object containing the retrieved organisation detail.
   */
  public OrganisationDetail getOrganisation(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final Integer maxRecords,
      final String organisationId) {
    log.info("OrganisationService - getOrganisation");

    final CommonOrgInqRS response =
        commonOrgClient.getOrganisation(
            soaGatewayUserLoginId, soaGatewayUserRole, maxRecords, organisationId);

    return organisationMapper.toOrganisationDetail(response.getOrganization());
  }
}
