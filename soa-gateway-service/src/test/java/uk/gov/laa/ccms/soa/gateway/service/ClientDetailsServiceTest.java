package uk.gov.laa.ccms.soa.gateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.ClientServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ClientDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    }
    @Test
    public void testGetClientDetails() {
        // Create a mocked instance of the response object
        ClientInqRS response = new ClientInqRS();

        // Create a mocked instance of the mapped client details
        ClientDetails clientDetails = new ClientDetails();

        ClientInfo clientInfo = buildClientInfo();
        ClientDetail clientDetail = buildClientDetail();

        when(clientDetailsMapper.toClientInfo(clientDetail)).thenReturn(clientInfo);

        // Stub the client to return the mocked response
        when(clientServicesClient.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientInfo))
                .thenReturn(response);

        // Stub the mapper to return the mocked client details
        when(clientDetailsMapper.toClientDetails(response)).thenReturn(clientDetails);

        // Call the service method
        ClientDetails result = clientDetailsService.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientDetail
        );

        // Verify that the client method was called with the expected arguments
        verify(clientServicesClient).getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientInfo
        );

        // Verify that the mapper method was called with the mocked response
        verify(clientDetailsMapper).toClientDetails(response);

        // Assert the result
        assertEquals(clientDetails, result);
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
