package uk.gov.laa.ccms.soa.gateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.ReferenceDataClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ReferenceDataMapper;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReferenceDataServiceTest {

    @Mock
    private ReferenceDataClient referenceDataClient;

    @Mock
    private ReferenceDataMapper referenceDataMapper;

    @InjectMocks
    private ReferenceDataService referenceDataService;

    private String soaGatewayUserLoginId;
    private String soaGatewayUserRole;

    @BeforeEach
    void setup() {
        this.soaGatewayUserLoginId = "user";
        this.soaGatewayUserRole = "EXTERNAL";
    }

    @Test
    public void testGetCaseReference() {
        // Create a mocked instance of the response object
        ReferenceDataInqRS response = new ReferenceDataInqRS();

        // Create a mocked instance of the mapped case reference summary
        CaseReferenceSummary caseReferenceSummary = new CaseReferenceSummary();

        when(referenceDataClient.getCaseReference(soaGatewayUserLoginId, soaGatewayUserRole))
                .thenReturn(response);

        when(referenceDataMapper.toCaseReferenceSummary(response))
                .thenReturn(caseReferenceSummary);

        // Call the service method
        CaseReferenceSummary result = referenceDataService.getCaseReference(soaGatewayUserLoginId, soaGatewayUserRole);

        // Verify that the client method was called with the expected arguments
        verify(referenceDataClient).getCaseReference(soaGatewayUserLoginId, soaGatewayUserRole);

        // Verify that the mapper method was called
        verify(referenceDataMapper).toCaseReferenceSummary(response);

        // Assert the result
        assertEquals(caseReferenceSummary, result);
    }
}
