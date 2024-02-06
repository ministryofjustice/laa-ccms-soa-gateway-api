package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.model.Organisation;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetails;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ.SearchCriteria.Organization;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS.OrganizationList;

@ExtendWith(MockitoExtension.class)
public class OrganisationMapperTest {

  public static final String ORGNAME = "orgname";
  public static final String ORGTYPE = "orgtype";
  public static final String ORGPARTYID = "orgpartyid";
  public static final String ORGCITY = "orgcity";
  public static final String ORGPOSTCODE = "orgpostcode";
  private static DatatypeFactory datatypeFactory;
  @InjectMocks
  OrganisationMapperImpl organisationMapper;

  @BeforeAll
  public static void setUp() throws DatatypeConfigurationException {
    datatypeFactory = DatatypeFactory.newInstance();
  }

  @Test
  public void testToOrganisation() {
    OrganizationList soaOrganization = buildOrganizationList();

    Organisation result = organisationMapper.toOrganisation(soaOrganization);

    assertNotNull(result);
    assertEquals(soaOrganization.getOrganizationName(), result.getName());
    assertEquals(soaOrganization.getOrganizationType(), result.getType());
    assertEquals(soaOrganization.getOrganizationPartyID(), result.getPartyId());
    assertEquals(soaOrganization.getCity(), result.getCity());
    assertEquals(soaOrganization.getPostCode(), result.getPostcode());
  }

  @Test
  public void testToOrganization() {
    Organisation organisation = buildOrganisation();

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

    List<Organisation> result = organisationMapper.toOrganisationList(commonOrgInqRs);

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
    Page<Organisation> organisationPage = new PageImpl<>(List.of(buildOrganisation()),
        Pageable.unpaged(), 1);

    OrganisationDetails result =
        organisationMapper.toOrganisationDetails(organisationPage);

    assertNotNull(result);
    assertNotNull(result.getContent());
    assertEquals(1, result.getSize());
    assertEquals(organisationPage.getContent().get(0), result.getContent().get(0));
  }

  private OrganizationList buildOrganizationList() {
    OrganizationList soaOrganization = new OrganizationList();
    soaOrganization.setOrganizationName(ORGNAME);
    soaOrganization.setOrganizationType(ORGTYPE);
    soaOrganization.setOrganizationPartyID(ORGPARTYID);
    soaOrganization.setCity(ORGCITY);
    soaOrganization.setPostCode(ORGPOSTCODE);
    return soaOrganization;
  }

  private Organisation buildOrganisation(){
    Organisation organisation = new Organisation();
    organisation.setPartyId(ORGPARTYID);
    organisation.setName(ORGNAME);
    organisation.setType(ORGTYPE);
    organisation.setCity(ORGCITY);
    organisation.setPostcode(ORGPOSTCODE);
    return organisation;
  }

}
