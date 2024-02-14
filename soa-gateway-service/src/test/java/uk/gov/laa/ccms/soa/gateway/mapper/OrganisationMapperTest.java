package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.BaseContact;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetail;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetails;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationSummary;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ.SearchCriteria.Organization;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS.OrganizationList;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.OrganizationPartyType;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.OrganizationPartyType.ContactDetails;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;

@ExtendWith(MockitoExtension.class)
public class OrganisationMapperTest {

  public static final String ORGNAME = "orgname";
  public static final String ORGTYPE = "orgtype";
  public static final String ORGPARTYID = "orgpartyid";
  public static final String ORGCITY = "orgcity";
  public static final String ORGPOSTCODE = "orgpostcode";

  @Mock
  CommonMapper commonMapper;

  @InjectMocks
  OrganisationMapperImpl organisationMapper;

  @Test
  public void testToOrganisation() {
    OrganizationList soaOrganization = buildOrganizationList();

    OrganisationSummary result = organisationMapper.toOrganisationSummary(soaOrganization);

    assertNotNull(result);
    assertEquals(soaOrganization.getOrganizationName(), result.getName());
    assertEquals(soaOrganization.getOrganizationType(), result.getType());
    assertEquals(soaOrganization.getOrganizationPartyID(), result.getPartyId());
    assertEquals(soaOrganization.getCity(), result.getCity());
    assertEquals(soaOrganization.getPostCode(), result.getPostcode());
  }

  @Test
  public void testToOrganization() {
    OrganisationSummary organisation = buildOrganisation();

    Organization result = organisationMapper.toOrganization(organisation);

    assertNotNull(result);
    assertEquals(organisation.getName(), result.getOrganizationName());
    assertEquals(organisation.getType(), result.getOrganizationType());
    assertEquals(organisation.getCity(), result.getCity());
    assertEquals(organisation.getPostcode(), result.getPostCode());
  }

  @Test
  public void testToOrganisationList() {

    OrganizationList organizationList = buildOrganizationList();
    CommonOrgInqRS commonOrgInqRs = new CommonOrgInqRS();
    commonOrgInqRs.getOrganizationList().add(organizationList);

    List<OrganisationSummary> result = organisationMapper.toOrganisationSummaryList(commonOrgInqRs);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(organizationList.getOrganizationPartyID(), result.get(0).getPartyId());
    assertEquals(organizationList.getOrganizationName(), result.get(0).getName());
    assertEquals(organizationList.getOrganizationType(), result.get(0).getType());
    assertEquals(organizationList.getCity(), result.get(0).getCity());
    assertEquals(organizationList.getPostCode(), result.get(0).getPostcode());
  }

  @Test
  public void testToOrganisationDetails() {
    Page<OrganisationSummary> organisationPage = new PageImpl<>(List.of(buildOrganisation()),
        Pageable.unpaged(), 1);

    OrganisationDetails result =
        organisationMapper.toOrganisationDetails(organisationPage);

    assertNotNull(result);
    assertNotNull(result.getContent());
    assertEquals(1, result.getSize());
    assertEquals(organisationPage.getContent().get(0), result.getContent().get(0));
  }

  @Test
  public void testToOrganisationDetail() {
    OrganizationPartyType organizationPartyType = buildOrganisationPartyType();

    when(commonMapper.toAddressDetail(organizationPartyType.getAddress()))
        .thenReturn(new AddressDetail());

    OrganisationDetail result =
        organisationMapper.toOrganisationDetail(organizationPartyType);

    assertNotNull(result);
    assertEquals(organizationPartyType.getOrganizationName(), result.getName());
    assertEquals(organizationPartyType.getOrganizationType(), result.getType());
    assertEquals(organizationPartyType.getOrganizationPartyID(), result.getPartyId());
    assertEquals(organizationPartyType.getContactName(), result.getContactName());
    assertNotNull(result.getContactDetails());
    assertNotNull(result.getAddress());
    assertEquals(organizationPartyType.getOtherInformation(), result.getOtherInformation());
  }

  @Test
  public void testToBaseContact() {
    ContactDetails contactDetails = buildContactDetails();

    BaseContact result = organisationMapper.toBaseContact(contactDetails);

    assertNotNull(result);
    assertEquals(contactDetails.getTelephoneHome(), result.getTelephoneHome());
    assertEquals(contactDetails.getTelephoneWork(), result.getTelephoneWork());
    assertEquals(contactDetails.getMobileNumber(), result.getMobileNumber());
    assertEquals(contactDetails.getEmailAddress(), result.getEmailAddress());
    assertEquals(contactDetails.getFax(), result.getFax());
  }

  private OrganizationList buildOrganizationList() {
    OrganizationList soaOrganization = new OrganizationList();
    soaOrganization.setOrganizationPartyID(ORGPARTYID);
    soaOrganization.setOrganizationName(ORGNAME);
    soaOrganization.setOrganizationType(ORGTYPE);
    soaOrganization.setCity(ORGCITY);
    soaOrganization.setPostCode(ORGPOSTCODE);
    return soaOrganization;
  }

  private OrganisationSummary buildOrganisation(){
    OrganisationSummary organisation = new OrganisationSummary();
    organisation.setPartyId(ORGPARTYID);
    organisation.setName(ORGNAME);
    organisation.setType(ORGTYPE);
    organisation.setCity(ORGCITY);
    organisation.setPostcode(ORGPOSTCODE);
    return organisation;
  }

  private OrganizationPartyType buildOrganisationPartyType(){
    OrganizationPartyType organizationPartyType = new OrganizationPartyType();
    organizationPartyType.setOrganizationPartyID(ORGPARTYID);
    organizationPartyType.setOrganizationName(ORGNAME);
    organizationPartyType.setOrganizationType(ORGTYPE);
    organizationPartyType.setAddress(buildAddress());
    organizationPartyType.setContactDetails(buildContactDetails());
    return organizationPartyType;
  }

  private Address buildAddress() {
    Address address = new Address();
    address.setAddressID("addid");
    address.setAddressLine1("addline1");
    address.setAddressLine2("addline2");
    address.setAddressLine3("addline3");
    address.setAddressLine4("addline4");
    address.setCity("city");
    address.setCoffName("careof");
    address.setCountry("country");
    address.setCounty("county");
    address.setHouse("house");
    address.setPostalCode("postcode");
    address.setProvince("province");
    address.setState("state");

    return address;
  }

  private ContactDetails buildContactDetails() {
    ContactDetails contactDetails = new ContactDetails();
    contactDetails.setFax("faxnum");
    contactDetails.setEmailAddress("email");
    contactDetails.setMobileNumber("mobile");
    contactDetails.setTelephoneHome("telhome");
    contactDetails.setTelephoneWork("telwork");

    return contactDetails;
  }
}
