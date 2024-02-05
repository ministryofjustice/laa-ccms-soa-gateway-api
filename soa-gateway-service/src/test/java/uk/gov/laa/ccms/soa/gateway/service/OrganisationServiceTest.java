package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.client.CommonOrgClient;
import uk.gov.laa.ccms.soa.gateway.mapper.OrganisationMapper;
import uk.gov.laa.ccms.soa.gateway.model.Organisation;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetails;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ.SearchCriteria.Organization;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS;

@ExtendWith(MockitoExtension.class)
public class OrganisationServiceTest {

    @Mock
    private CommonOrgClient commonOrgClient;

    @Mock
    private OrganisationMapper organisationMapper;

    @InjectMocks
    private OrganisationService organisationService;

    private String soaGatewayUserLoginId;
    private String soaGatewayUserRole;

    private Integer maxRecords;

    private Pageable pageable;

    @BeforeEach
    void setup() {
        this.soaGatewayUserLoginId = "user";
        this.soaGatewayUserRole = "EXTERNAL";
        this.maxRecords = 50;
        this.pageable = Pageable.ofSize(20);
    }

    @Test
    public void testGetOrganisations() {
        Organisation searchOrganisation = new Organisation();
        Organization organization = buildOrganization();
        OrganisationDetails expectedResponse = new OrganisationDetails();

        when(organisationMapper.toOrganization(searchOrganisation)).thenReturn(organization);

        when(commonOrgClient.getOrganisations(
            soaGatewayUserLoginId, soaGatewayUserRole, maxRecords, organization))
            .thenReturn(new CommonOrgInqRS());

        when(organisationMapper.toOrganisationList(any(CommonOrgInqRS.class)))
            .thenReturn(List.of(searchOrganisation));

        when(organisationMapper.toOrganisationDetails(any(Page.class))).thenReturn(
            expectedResponse);

        OrganisationDetails result = organisationService.getOrganisations(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords,
            searchOrganisation,
            pageable);


        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    private Organization buildOrganization(){
        Organization organization = new Organization();
        organization.setOrganizationName("thename");
        organization.setOrganizationType("thetype");
        organization.setCity("thecity");
        organization.setPostCode("thepostcode");
        return organization;
    }
}
