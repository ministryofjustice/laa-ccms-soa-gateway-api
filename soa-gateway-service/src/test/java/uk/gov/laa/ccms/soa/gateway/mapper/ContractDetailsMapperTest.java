package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.model.ContractDetails;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.ContractDetailsElementType;

@ExtendWith(MockitoExtension.class)
public class ContractDetailsMapperTest {

    @InjectMocks
    ContractDetailsMapperImpl contractDetailsMapper;

    @Test
    public void testMap() {
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

        ContractDetails result = contractDetailsMapper.toContractDetails(response);

        assertNotNull(result);
        assertEquals(1, result.getContracts().size());
        assertEquals(authType, result.getContracts().get(0).getAuthorisationType());
        assertEquals(catOfLaw, result.getContracts().get(0).getCategoryofLaw());
        assertEquals(subCat, result.getContracts().get(0).getSubCategory());
        assertEquals(conDevPowers, result.getContracts().get(0).getContractualDevolvedPowers());
        assertTrue(result.getContracts().get(0).isCreateNewMatters());
        assertTrue(result.getContracts().get(0).isRemainderAuthorisation());
    }

    @Test
    public void testMapHandlesNoContractDetails() {
        ContractDetailsInqRS response = new ContractDetailsInqRS();

        assertNotNull(contractDetailsMapper.toContractDetails(response));
    }

    @Test
    public void testMapHandlesNoContractDetailList() {
        ContractDetailsInqRS response = new ContractDetailsInqRS();
        response.setContractDetails(new ContractDetailsInqRS.ContractDetails());

        assertNotNull(contractDetailsMapper.toContractDetails(response));
    }
}