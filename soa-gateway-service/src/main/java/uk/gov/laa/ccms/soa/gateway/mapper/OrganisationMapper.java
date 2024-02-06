package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.Organisation;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetails;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ.SearchCriteria.Organization;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS.OrganizationList;


/**
 * Mapper interface for converting organisation data between different representations.
 */
@Mapper(componentModel = "spring")
public interface OrganisationMapper {

  @Mapping(target = "organizationName", source = "name")
  @Mapping(target = "organizationType", source = "type")
  @Mapping(target = "postCode", source = "postcode")
  Organization toOrganization(Organisation organisation);

  /**
   * Convert a SOA CommonOrgInqRS to a List of model Organisation.
   *
   * @param commonOrgInqRs - the object to convert.
   * @return a List of Organisation
   */
  default List<Organisation> toOrganisationList(CommonOrgInqRS commonOrgInqRs) {
    if (commonOrgInqRs != null) {
      List<OrganizationList> orgList = commonOrgInqRs.getOrganizationList();
      if (orgList != null) {
        return toOrganisationList(orgList);
      }
    }
    return Collections.emptyList();
  }

  List<Organisation> toOrganisationList(List<OrganizationList> soaOrganisationList);

  @Mapping(target = "partyId", source = "organizationPartyID")
  @Mapping(target = "name", source = "organizationName")
  @Mapping(target = "type", source = "organizationType")
  @Mapping(target = "postcode", source = "postCode")
  Organisation toOrganisation(OrganizationList soaOrganisation);

  OrganisationDetails toOrganisationDetails(Page<Organisation> organisationPage);

}
