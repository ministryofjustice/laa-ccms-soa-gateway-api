package uk.gov.laa.ccms.soa.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ws.client.WebServiceIOException;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.service.ClientDetailsService;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private Pageable pageable;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(clientDetailsController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        this.soaGatewayUserLoginId = "user";
        this.soaGatewayUserRole = "EXTERNAL";
        this.firstName = "John";
        this.surname = "Doe";
        this.gender = "Male";
        this.clientReferenceNumber = "1234567890";
        this.homeOfficeReference = "ABC123";
        this.nationalInsuranceNumber = "AB123456C";
        this.maxRecords = 50;
        this.pageable = Pageable.ofSize(20);
    }

    @Test
    public void testGetClient_Success() throws Exception {
        // Create a mock client detail response
        ClientDetail clientDetail = new ClientDetail();

        // Stub the clientDetailsService to return the mock response
        when(clientDetailsService.getClientDetail(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientReferenceNumber)) // Assuming you have the clientReferenceNumber parameter
                .thenReturn(clientDetail);

        mockMvc.perform(
                        get("/clients/{clientReferenceNumber}", clientReferenceNumber)
                                .param("max-records", String.valueOf(maxRecords))
                                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                                .header("SoaGateway-User-Role", soaGatewayUserRole))
                .andExpect(status().isOk());

        verify(clientDetailsService).getClientDetail(soaGatewayUserLoginId, soaGatewayUserRole, maxRecords, clientReferenceNumber);
    }

    @Test
    public void testGetClient_Exception() throws Exception {
        // Stub the clientDetailsService to throw an exception
        when(clientDetailsService.getClientDetail(any(), any(), any(), any()))
                .thenThrow(new WebServiceIOException("Test exception"));

        mockMvc.perform(
                        get("/clients/{clientReferenceNumber}", clientReferenceNumber)
                                .param("max-records", String.valueOf(maxRecords))
                                .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                                .header("SoaGateway-User-Role", soaGatewayUserRole))
                .andExpect(status().isInternalServerError());

        verify(clientDetailsService).getClientDetail(any(), any(), any(), any());
    }


    @Test
    public void testGetClients_Success() throws Exception {
        // Create a mock client details response
        ClientDetails clientDetails = new ClientDetails();

        ClientSummary clientSummary= buildClientSummary();

        // Stub the clientDetailsService to return the mock response
        when(clientDetailsService.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientSummary,
                pageable))
                .thenReturn(clientDetails);

        mockMvc.perform(
                        get("/clients?first-name={firstName}&surname={surname}" +
                                        "&max-records={maxRecords}" +
                                        "&gender={gender}"+
                                        "&case-reference-number={caseReferenceNumber}" +
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
                soaGatewayUserRole, maxRecords, clientSummary, pageable);
    }

    @Test
    public void testGetClients_Exception() throws Exception {
        ClientSummary clientSummary= buildClientSummary();

        // Stub the clientDetailsService to return the mock response
        when(clientDetailsService.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                new ClientSummary(),
                pageable))
                .thenThrow(new WebServiceIOException("Test exception"));

        mockMvc.perform(
                        get("/clients?first-name={firstName}&surname={surname}" +
                                        "&max-records={maxRecords}" +
                                        "&gender={gender}"+
                                        "&case-reference-number={caseReferenceNumber}" +
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
                soaGatewayUserRole, maxRecords, clientSummary, pageable);
    }

    @Test
    public void testPostClient_Success() throws Exception {
        uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails clientDetailDetails = new uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails();  // You may need to set some data for this object.
        String expectedTransactionId = "trans123";

        when(clientDetailsService.postClient(soaGatewayUserLoginId, soaGatewayUserRole, clientDetailDetails))
            .thenReturn(expectedTransactionId);

        mockMvc.perform(
                post("/clients")  // Assuming the endpoint for postClient method is "/clients"
                    .content(new ObjectMapper().writeValueAsString(clientDetailDetails))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                    .header("SoaGateway-User-Role", soaGatewayUserRole))
            .andExpect(status().isOk());

        verify(clientDetailsService).postClient(soaGatewayUserLoginId, soaGatewayUserRole, clientDetailDetails);
    }

    @Test
    public void testPostClient_Exception() throws Exception {
        uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails clientDetailDetails = new uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails();

        when(clientDetailsService.postClient(any(), any(), any()))
            .thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(
                post("/clients")
                    .content(new ObjectMapper().writeValueAsString(clientDetailDetails))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                    .header("SoaGateway-User-Role", soaGatewayUserRole))
            .andExpect(status().isInternalServerError());

        verify(clientDetailsService).postClient(any(), any(), any());
    }

    @Test
    public void testGetClientStatus_Success() throws Exception {
        String transactionRequestId = "trans123";
        String expectedStatus = "COMPLETED";

        when(clientDetailsService.getClientStatus(soaGatewayUserLoginId, soaGatewayUserRole, transactionRequestId))
            .thenReturn(expectedStatus);

        mockMvc.perform(
                get("/clients/status/{transactionRequestId}", transactionRequestId)
                    .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                    .header("SoaGateway-User-Role", soaGatewayUserRole))
            .andExpect(status().isOk());

        verify(clientDetailsService).getClientStatus(soaGatewayUserLoginId, soaGatewayUserRole, transactionRequestId);
    }

    @Test
    public void testGetClientStatus_Exception() throws Exception {
        String transactionRequestId = "trans123";

        when(clientDetailsService.getClientStatus(any(), any(), any()))
            .thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(
                get("/clients/status/{transactionRequestId}", transactionRequestId)
                    .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                    .header("SoaGateway-User-Role", soaGatewayUserRole))
            .andExpect(status().isInternalServerError());

        verify(clientDetailsService).getClientStatus(any(), any(), any());
    }

    private ClientSummary buildClientSummary(){
        return new ClientSummary()
                .firstName(firstName)
                .surname(surname)
                .gender(gender)
                .clientReferenceNumber(clientReferenceNumber)
                .homeOfficeReference(homeOfficeReference)
                .nationalInsuranceNumber(nationalInsuranceNumber);
    }
}
