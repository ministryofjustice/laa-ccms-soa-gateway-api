package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.client.ClientServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ClientDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

@ExtendWith(MockitoExtension.class)
public class ClientDetailsServiceTest {

    @Mock
    private ClientServicesClient clientServicesClient;

    @Mock
    private ClientDetailsMapper clientDetailsMapper;

    @InjectMocks
    private ClientDetailsService clientDetailsService;

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
    void setup() {
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
    public void testGetClientDetail() {
        // Create test data
        String clientReferenceNumber = "123456789";

        // Create a mocked instance of the response object
        ClientInqRS response = new ClientInqRS();

        // Create a mocked instance of the mapped client detail
        ClientDetail clientDetail = new ClientDetail();

        when(clientServicesClient.getClientDetail(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientReferenceNumber
        )).thenReturn(response);

        when(clientDetailsMapper.toClientDetail(response)).thenReturn(clientDetail);

        // Call the service method
        ClientDetail result = clientDetailsService.getClientDetail(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientReferenceNumber
        );

        // Verify that the client method was called with the expected arguments
        verify(clientServicesClient).getClientDetail(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientReferenceNumber
        );

        // Verify that the mapper method was called
        verify(clientDetailsMapper).toClientDetail(response);

        // Assert the result
        assertEquals(clientDetail, result);
    }

    @Test
    public void testPostClient() {
        // Create test data
        ClientDetailDetails clientDetailDetails = new ClientDetailDetails();

        uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails mockClientDetails
            = new uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails();

        String expectedTransactionId = "1234567890";
        ClientAddRS mockClientAddRS = new ClientAddRS();
        mockClientAddRS.setTransactionID(expectedTransactionId);

        when(clientDetailsMapper.toSoaClientDetails(clientDetailDetails)).thenReturn(mockClientDetails);

        when(clientServicesClient.postClientDetails(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            mockClientDetails)).thenReturn(mockClientAddRS);

        String result = clientDetailsService.createClient(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            clientDetailDetails);

        assertEquals(result,expectedTransactionId);

        verify(clientDetailsMapper).toSoaClientDetails(clientDetailDetails);
        verify(clientServicesClient).postClientDetails(soaGatewayUserLoginId,soaGatewayUserRole, mockClientDetails);
    }

    @Test
    public void testUpdateClient() {
        // Create test data
        ClientDetailDetails clientDetailDetails = new ClientDetailDetails();

        ClientUpdateRQ mockClientUpdateRq = new ClientUpdateRQ();

        String expectedTransactionId = "1234567890";
        ClientUpdateRS mockClientUpdateRS = new ClientUpdateRS();
        mockClientUpdateRS.setTransactionID(expectedTransactionId);

        when(clientDetailsMapper.toClientUpdateRq("123456", clientDetailDetails))
            .thenReturn(mockClientUpdateRq);

        when(clientServicesClient.updateClientDetails(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            mockClientUpdateRq)).thenReturn(mockClientUpdateRS);

        String result = clientDetailsService.updateClient(
            "123456",
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            clientDetailDetails);

        assertEquals(result,expectedTransactionId);

        verify(clientDetailsMapper).toClientUpdateRq("123456",clientDetailDetails);
        verify(clientServicesClient).updateClientDetails(soaGatewayUserLoginId,soaGatewayUserRole, mockClientUpdateRq);
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

    private ClientInfo buildClientInfo(){
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setFirstName(firstName);
        clientInfo.setSurname(surname);
        clientInfo.setNINumber(nationalInsuranceNumber);
        clientInfo.setHomeOfficeReference(homeOfficeReference);
        clientInfo.setCaseReferenceNumber(clientReferenceNumber);
        return clientInfo;
    }
}
