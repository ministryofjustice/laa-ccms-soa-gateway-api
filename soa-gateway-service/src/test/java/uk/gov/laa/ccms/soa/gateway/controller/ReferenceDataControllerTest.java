package uk.gov.laa.ccms.soa.gateway.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;
import uk.gov.laa.ccms.soa.gateway.service.ReferenceDataService;

@ExtendWith(MockitoExtension.class)
public class ReferenceDataControllerTest {

    @Mock
    private ReferenceDataService referenceDataService;

    @InjectMocks
    private ReferenceDataController referenceDataController;

    private String soaGatewayUserLoginId;
    private String soaGatewayUserRole;

    @BeforeEach
    void setup() {
        this.soaGatewayUserLoginId = "user";
        this.soaGatewayUserRole = "EXTERNAL";
    }

    @Test
    public void testGetCaseReference_Success() {
        // Create a mock case reference summary response
        CaseReferenceSummary caseReferenceSummary = new CaseReferenceSummary();

        // Stub the referenceDataService to return the mock response
        when(referenceDataService.getCaseReference(soaGatewayUserLoginId, soaGatewayUserRole))
                .thenReturn(caseReferenceSummary);

        // Call the controller method
        ResponseEntity<CaseReferenceSummary> responseEntity = referenceDataController.getCaseReference(soaGatewayUserLoginId, soaGatewayUserRole);

        // Verify that the service method was called with the expected arguments
        verify(referenceDataService).getCaseReference(soaGatewayUserLoginId, soaGatewayUserRole);

        // Assert the response status code and body
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(caseReferenceSummary, responseEntity.getBody());
    }

    @Test
    public void testGetCaseReference_Exception() {
        // Stub the referenceDataService to throw an exception
        when(referenceDataService.getCaseReference(soaGatewayUserLoginId, soaGatewayUserRole))
                .thenThrow(new RuntimeException("Test exception"));

        // Call the controller method
        ResponseEntity<CaseReferenceSummary> responseEntity = referenceDataController.getCaseReference(soaGatewayUserLoginId, soaGatewayUserRole);

        // Verify that the service method was called with the expected arguments
        verify(referenceDataService).getCaseReference(soaGatewayUserLoginId, soaGatewayUserRole);

        // Assert the response status code for internal server error
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
