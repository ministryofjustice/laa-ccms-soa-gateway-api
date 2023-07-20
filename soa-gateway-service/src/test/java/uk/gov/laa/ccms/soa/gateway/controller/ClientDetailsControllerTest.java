package uk.gov.laa.ccms.soa.gateway.controller;

import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ws.client.WebServiceIOException;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.service.ClientDetailsService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ClientDetailsControllerTest {

    @Mock
    private ClientDetailsService clientDetailsService;

    @InjectMocks
    private ClientDetailsController clientDetailsController;

    private MockMvc mockMvc;

    private String soaGatewayUserLoginId;
    private String soaGatewayUserRole;
    private String firstName;
    private String surname;
    private String gender;
    private String clientReferenceNumber;
    private String homeOfficeReference;
    private String nationalInsuranceNumber;
    private Integer maxRecords;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(clientDetailsController).build();
        this.soaGatewayUserLoginId = "user";
        this.soaGatewayUserRole = "EXTERNAL";
        this.firstName = "John";
        this.surname = "Doe";
        this.gender = "Male";
        this.clientReferenceNumber = "1234567890";
        this.homeOfficeReference = "ABC123";
        this.nationalInsuranceNumber = "AB123456C";
        this.maxRecords = 50;
    }


    @Test
    public void testGetClients_Success() throws Exception {
        // Create a mock client details response
        ClientDetails clientDetails = new ClientDetails();

        ClientDetail clientDetail = buildClientDetail();

        // Stub the clientDetailsService to return the mock response
        when(clientDetailsService.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientDetail))
                .thenReturn(clientDetails);

        mockMvc.perform(
                        get("/clients?first-name={firstName}&surname={surname}&maxRecords={maxRecords}" +
                                        "&gender={gender}"+
                                        "&client-reference-number={clientReferenceNumber}" +
                                        "&home-office-reference={homeOfficeReference}" +
                                        "&national-insurance-number={nationalInsuranceNumber}",
                                firstName,
                                surname,
                                maxRecords,
                                gender,
                                clientReferenceNumber,
                                homeOfficeReference,
                                nationalInsuranceNumber)
                                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                                .header("SoaGateway-User-Role", soaGatewayUserRole))
                .andExpect(status().isOk());

        verify(clientDetailsService).getClientDetails(soaGatewayUserLoginId,
                soaGatewayUserRole, maxRecords, clientDetail);
    }

    @Test
    public void testGetClients_Exception() throws Exception {
        ClientDetail clientDetail = buildClientDetail();

        // Stub the clientDetailsService to return the mock response
        when(clientDetailsService.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                new ClientDetail()))
                .thenThrow(new WebServiceIOException("Test exception"));

        mockMvc.perform(
                        get("/clients?first-name={firstName}&surname={surname}&maxRecords={maxRecords}" +
                                        "&gender={gender}"+
                                        "&client-reference-number={clientReferenceNumber}" +
                                        "&home-office-reference={homeOfficeReference}" +
                                        "&national-insurance-number={nationalInsuranceNumber}",
                                firstName,
                                surname,
                                maxRecords,
                                gender,
                                clientReferenceNumber,
                                homeOfficeReference,
                                nationalInsuranceNumber)
                                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                                .header("SoaGateway-User-Role", soaGatewayUserRole))
                .andExpect(status().isInternalServerError());

        verify(clientDetailsService).getClientDetails(soaGatewayUserLoginId,
                soaGatewayUserRole, maxRecords, clientDetail);
    }

    private ClientDetail buildClientDetail(){
        return new ClientDetail()
                .firstName(firstName)
                .surname(surname)
                .gender(gender)
                .clientReferenceNumber(clientReferenceNumber)
                .homeOfficeReference(homeOfficeReference)
                .nationalInsuranceNumber(nationalInsuranceNumber);
    }
}
