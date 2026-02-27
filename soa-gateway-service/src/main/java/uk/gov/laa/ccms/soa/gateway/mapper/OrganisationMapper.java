package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.BaseContact;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetail;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetails;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationSummary;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ.SearchCriteria.Organization;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS.OrganizationList;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.OrganizationPartyType;

/** Mapper interface for converting organisation data between different representations. */
@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface OrganisationMapper {

  @Mapping(target = "organizationName", source = "name")
  @Mapping(target = "organizationType", source = "type")
  @Mapping(target = "postCode", source = "postcode")
  Organization toOrganization(OrganisationSummary organisation);

  /**
   * Convert a SOA CommonOrgInqRS to a List of model OrganisationSummary.
   *
   * @param commonOrgInqRs - the object to convert.
   * @return a List of OrganisationSummary
   */
  default List<OrganisationSummary> toOrganisationSummaryList(CommonOrgInqRS commonOrgInqRs) {
    if (commonOrgInqRs != null) {
      List<OrganizationList> orgList = commonOrgInqRs.getOrganizationList();
      if (orgList != null) {
        return toOrganisationSummaryList(orgList);
      }
    }
    return Collections.emptyList();
  }

  List<OrganisationSummary> toOrganisationSummaryList(List<OrganizationList> soaOrganisationList);

  @Mapping(target = "partyId", source = "organizationPartyID")
  @Mapping(target = "name", source = "organizationName")
  @Mapping(target = "type", source = "organizationType")
  @Mapping(target = "postcode", source = "postCode")
  OrganisationSummary toOrganisationSummary(OrganizationList soaOrganisation);

  OrganisationDetails toOrganisationDetails(Page<OrganisationSummary> organisationPage);

  @Mapping(target = "partyId", source = "organizationPartyID")
  @Mapping(target = "name", source = "organizationName")
  @Mapping(target = "type", source = "organizationType")
  OrganisationDetail toOrganisationDetail(OrganizationPartyType organizationPartyType);

  BaseContact toBaseContact(OrganizationPartyType.ContactDetails soaContactDetails);
}
