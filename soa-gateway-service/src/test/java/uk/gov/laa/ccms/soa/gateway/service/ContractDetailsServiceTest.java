package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.ContractDetailsClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ContractDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ContractDetail;
import uk.gov.laa.ccms.soa.gateway.model.ContractDetails;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.ContractDetailsElementType;

@ExtendWith(MockitoExtension.class)
public class ContractDetailsServiceTest {

    @Mock
    private ContractDetailsClient contractDetailsClient;

    @Mock
    private ContractDetailsMapper contractDetailsMapper;

    @InjectMocks
    private ContractDetailsService contractDetailsService;



    @Test
    public void testGetContractDetails() {
        // Create a mocked instance of soa response object
        String authType = "authType";
        String catOfLaw = "catOfLaw";
        String subCat = "subCat";
        String conDevPowers = "conDevPowers";

        ContractDetailsInqRS response = new ContractDetailsInqRS();
        response.setContractDetails(new ContractDetailsInqRS.ContractDetails());
        ContractDetailsElementType contract = new ContractDetailsElementType();
        contract.setAuthorisationType(authType);
        contract.setCategoryofLaw(catOfLaw);
        contract.setSubCategory(subCat);
        contract.setRemainderAuthorisation(true);
        contract.setCreateNewMatters(true);
        contract.setContractualDevolvedPowers(conDevPowers);
        response.getContractDetails().getContractDetail().add(contract);

        // Create a mocked instance of gateway response
        ContractDetails contractDetails = new ContractDetails();
        contractDetails.addContractsItem(
            new ContractDetail()
                .authorisationType(authType)
                .categoryofLaw(catOfLaw)
                .contractualDevolvedPowers(conDevPowers)
                .subCategory(subCat)
                .remainderAuthorisation(true)
                .createNewMatters(true));

        Integer searchFirmId = 123;
        Integer searchOfficeId = 456;
        String soaGatewayUserLoginId = "soaGatewayUserLoginId";
        String soaGatewayUserRole = "soaGatewayUserRole";
        Integer maxRecords = 50;

        // Stub the Client to return the mocked response
        when(contractDetailsClient.getContractDetails(searchFirmId.toString(), searchOfficeId.toString(),
            soaGatewayUserLoginId, soaGatewayUserRole, maxRecords))
                .thenReturn(response);

        // Stub the Mapper to return the mocked gateway response
        when(contractDetailsMapper.toContractDetails(eq(response))).thenReturn(contractDetails);

        // Call the service method
        ContractDetails result = contractDetailsService.getContractDetails(
            searchFirmId,
            searchOfficeId,
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords
        );

        // Verify that the NotificationClient method was called with the expected arguments
        verify(contractDetailsClient).getContractDetails(
            searchFirmId.toString(),
            searchOfficeId.toString(),
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords
        );

        // Verify that the map function was called with the mocked response
        verify(contractDetailsMapper).toContractDetails(response);

        // Assert the result
        assertEquals(contractDetails, result);
    }
}

