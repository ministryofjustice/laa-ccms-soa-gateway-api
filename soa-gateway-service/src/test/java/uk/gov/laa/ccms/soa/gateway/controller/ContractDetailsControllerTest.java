package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ws.client.WebServiceIOException;
import uk.gov.laa.ccms.soa.gateway.model.ContractDetails;
import uk.gov.laa.ccms.soa.gateway.service.ContractDetailsService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebAppConfiguration
class ContractDetailsControllerTest {
    @Mock
    private ContractDetailsService contractDetailsService;

    @InjectMocks
    private ContractDetailsController contractDetailsController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(contractDetailsController).build();
    }

    @Test
    public void testGetContractDetails_Success() throws Exception {
        // Mock input parameters
        Integer providerFirmId = 123;
        Integer officeId = 345;
        String soaGatewayUserLoginId = "user";
        String soaGatewayUserRole = "EXTERNAL";
        Integer maxRecords = 50;

        // Create a mock gateway response
        String authType = "authType";
        String catOfLaw = "catOfLaw";
        String subCat = "subCat";
        String conDevPowers = "conDevPowers";
        ContractDetails contractDetails = new ContractDetails();
        contractDetails.addContractsItem(
            new uk.gov.laa.ccms.soa.gateway.model.ContractDetail()
                .authorisationType(authType)
                .categoryofLaw(catOfLaw)
                .contractualDevolvedPowers(conDevPowers)
                .subCategory(subCat)
                .remainderAuthorisation(true)
                .createNewMatters(true));

        // Mock the notificationService to return the mock notification summary
        when(contractDetailsService.getContractDetails(
            providerFirmId,
            officeId,
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords))
                .thenReturn(contractDetails);

        // Call the getUserNotificationSummary method
        mockMvc.perform(
            get("/contract-details?providerFirmId={providerFirmId}&officeId={officeId}&maxRecords={maxRecords}",
                providerFirmId,
                officeId,
                maxRecords)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole))
                .andExpect(status().isOk());

        // Verify that the notificationService method was called with the correct parameters
        verify(contractDetailsService).getContractDetails(providerFirmId,
            officeId, soaGatewayUserLoginId, soaGatewayUserRole, maxRecords);
    }

    @Test
    public void testGetContractDetails_Exception() throws Exception {
        // Mock input parameters
        Integer providerFirmId = 123;
        Integer officeId = 345;
        String soaGatewayUserLoginId = "user";
        String soaGatewayUserRole = "EXTERNAL";
        Integer maxRecords = 50;

        // Mock the notificationService to throw an exception
        when(contractDetailsService.getContractDetails(providerFirmId,
            officeId, soaGatewayUserLoginId, soaGatewayUserRole, maxRecords))
                .thenThrow(new WebServiceIOException("Test exception"));

        // Call the getUserNotificationSummary method
        mockMvc.perform(
            get("/contract-details?providerFirmId={providerFirmId}&officeId={officeId}&maxRecords={maxRecords}",
                providerFirmId,
                officeId,
                maxRecords)
                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                .header("SoaGateway-User-Role", soaGatewayUserRole))
                .andExpect(status().isInternalServerError());

        // Verify that the notificationService method was called with the correct parameters
        verify(contractDetailsService).getContractDetails(providerFirmId, officeId,
            soaGatewayUserLoginId, soaGatewayUserRole, maxRecords);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, EXTERNAL", // SoaGateway-User-Login-Id is null
            "user, null" // SoaGateway-User-Role is null
    }, nullValues={"null"})
    public void testGetContractDetails_HeaderBadRequest(String userLoginId, String userRole) throws Exception {
        // Call the getUserNotificationSummary method with null headers
        MockHttpServletRequestBuilder requestBuilder = get("/contract-details?providerFirmId={providerFirmId}&officeId={officeId}&maxRecords={maxRecords}",
            "providerFirmId",
            "officeId",
            "50");

        if (userLoginId != null) {
            requestBuilder.header("SoaGateway-User-Login-Id", userLoginId);
        }
        if (userRole != null) {
            requestBuilder.header("SoaGateway-User-Role", userRole);
        }

        // Call the getUserNotificationSummary method with optional headers
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
}