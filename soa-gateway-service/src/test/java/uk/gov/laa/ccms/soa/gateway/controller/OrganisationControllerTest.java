package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ws.client.WebServiceIOException;
import uk.gov.laa.ccms.soa.gateway.model.Organisation;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationDetails;
import uk.gov.laa.ccms.soa.gateway.service.OrganisationService;

@ExtendWith(MockitoExtension.class)
public class OrganisationControllerTest {

    @Mock
    private OrganisationService organisationService;

    @InjectMocks
    private OrganisationController organisationController;

    private MockMvc mockMvc;

    private String soaGatewayUserLoginId;
    private String soaGatewayUserRole;
    private String name;
    private String type;
    private String city;

    private String postcode;

    private Integer maxRecords;

    private Pageable pageable;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(organisationController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        this.soaGatewayUserLoginId = "user";
        this.soaGatewayUserRole = "EXTERNAL";
        this.name = "orgname";
        this.type = "orgtype";
        this.city = "thecity";
        this.postcode = "1234567890";
        this.maxRecords = 50;
        this.pageable = Pageable.ofSize(20);
    }

    @Test
    public void testGetClients_Success() throws Exception {
        // Create a mock organisation details response
        OrganisationDetails organisationDetails = new OrganisationDetails();

        Organisation organisation = buildOrganisation();

        // Stub the clientDetailsService to return the mock response
        when(organisationService.getOrganisations(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords,
            organisation,
            pageable))
            .thenReturn(organisationDetails);

        mockMvc.perform(
                get("/organisations?name={name}&type={type}" +
                        "&max-records={maxRecords}" +
                        "&city={city}"+
                        "&postcode={postcode}",
                    name,
                    type,
                    maxRecords,
                    city,
                    postcode)
                    .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                    .header("SoaGateway-User-Role", soaGatewayUserRole))
            .andExpect(status().isOk());

        verify(organisationService).getOrganisations(soaGatewayUserLoginId,
            soaGatewayUserRole, maxRecords, organisation, pageable);
    }

    @Test
    public void testGetClients_Exception() throws Exception {
        Organisation organisation = buildOrganisation();

        // Stub the clientDetailsService to throw an exception
        when(organisationService.getOrganisations(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords,
            organisation,
            pageable))
            .thenThrow(new WebServiceIOException("Test exception"));

        mockMvc.perform(
                get("/organisations?name={name}&type={type}" +
                        "&max-records={maxRecords}" +
                        "&city={city}"+
                        "&postcode={postcode}",
                    name,
                    type,
                    maxRecords,
                    city,
                    postcode)
                    .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                    .header("SoaGateway-User-Role", soaGatewayUserRole))
            .andExpect(status().isInternalServerError());

        verify(organisationService).getOrganisations(soaGatewayUserLoginId,
            soaGatewayUserRole, maxRecords, organisation, pageable);
    }

    private Organisation buildOrganisation(){
        Organisation organisation = new Organisation();
        organisation.setName(name);
        organisation.setType(type);
        organisation.setCity(city);
        organisation.setPostcode(postcode);
        return organisation;
    }

}
