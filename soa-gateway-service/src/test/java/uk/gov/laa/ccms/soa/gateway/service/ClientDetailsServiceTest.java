package uk.gov.laa.ccms.soa.gateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.client.ClientServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ClientDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddUpdtStatusRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

import java.util.Collections;
import java.util.List;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.Status;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;

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
    public void testGetClientDetails() {
        // Create a mocked instance of the response object
        ClientInqRS response = new ClientInqRS();

        // Create a mocked instance of the mapped client details
        ClientDetails clientDetails = new ClientDetails();

        ClientInfo clientInfo = buildClientInfo();
        ClientSummary clientSummary = buildClientSummary();

        List<ClientSummary> clientSummaryList = Collections.singletonList(clientSummary);
        Page<ClientSummary> clientSummaryPage = new PageImpl<>(clientSummaryList, pageable, clientSummaryList.size());

        when(clientDetailsMapper.toClientInfo(clientSummary)).thenReturn(clientInfo);

        // Stub the client to return the mocked response
        when(clientServicesClient.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientInfo))
                .thenReturn(response);

        // Stub the mapper to return the mocked client details
        when(clientDetailsMapper.toClientSummaryList(response)).thenReturn(clientSummaryList);
        when(clientDetailsMapper.toClientDetails(clientSummaryPage)).thenReturn(clientDetails);

        // Call the service method
        ClientDetails result = clientDetailsService.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientSummary,
                pageable
        );

        // Verify that the client method was called with the expected arguments
        verify(clientServicesClient).getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientInfo
        );

        // Verify that the mapper methods were called
        verify(clientDetailsMapper).toClientInfo(clientSummary);
        verify(clientDetailsMapper).toClientSummaryList(response);
        verify(clientDetailsMapper).toClientDetails(clientSummaryPage);

        // Assert the result
        assertEquals(clientDetails, result);
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

        String result = clientDetailsService.postClient(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            clientDetailDetails);

        assertEquals(result,expectedTransactionId);

        verify(clientDetailsMapper).toSoaClientDetails(clientDetailDetails);
        verify(clientServicesClient).postClientDetails(soaGatewayUserLoginId,soaGatewayUserRole, mockClientDetails);
    }

    @Test
    public void testGetClientStatus() {
        String transactionId = "sampleTransactionId";

        Status mockStatus =  new Status();
        mockStatus.setStatus(StatusTextType.SUCCESS);

        HeaderRSType mockHeader = new HeaderRSType();
        mockHeader.setStatus(mockStatus);

        ClientAddUpdtStatusRS mockClientAddUpdtStatusRS = new ClientAddUpdtStatusRS();
        mockClientAddUpdtStatusRS.setHeaderRS(mockHeader);

        when(clientServicesClient.getClientStatus(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            transactionId)).thenReturn(mockClientAddUpdtStatusRS);

        String status = clientDetailsService.getClientStatus(soaGatewayUserLoginId, soaGatewayUserRole, transactionId);

        assertEquals(status, "Success");

        verify(clientServicesClient).getClientStatus(soaGatewayUserLoginId,soaGatewayUserRole, transactionId);
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
