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
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

import java.util.Collections;
import java.util.List;

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
    private String caseReferenceNumber;
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
        this.caseReferenceNumber = "1234567890";
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
        ClientDetail clientDetail = buildClientDetail();

        List<ClientDetail> clientDetailList = Collections.singletonList(clientDetail);
        Page<ClientDetail> clientDetailPage = new PageImpl<>(clientDetailList, pageable, clientDetailList.size());

        when(clientDetailsMapper.toClientInfo(clientDetail)).thenReturn(clientInfo);

        // Stub the client to return the mocked response
        when(clientServicesClient.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientInfo))
                .thenReturn(response);

        // Stub the mapper to return the mocked client details
        when(clientDetailsMapper.toClientDetailList(response)).thenReturn(clientDetailList);
        when(clientDetailsMapper.toClientDetails(clientDetailPage)).thenReturn(clientDetails);

        // Call the service method
        ClientDetails result = clientDetailsService.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientDetail,
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
        verify(clientDetailsMapper).toClientInfo(clientDetail);
        verify(clientDetailsMapper).toClientDetailList(response);
        verify(clientDetailsMapper).toClientDetails(clientDetailPage);

        // Assert the result
        assertEquals(clientDetails, result);
    }

    private ClientDetail buildClientDetail(){
        return new ClientDetail()
                .firstName(firstName)
                .surname(surname)
                .gender(gender)
                .caseReferenceNumber(caseReferenceNumber)
                .homeOfficeReference(homeOfficeReference)
                .nationalInsuranceNumber(nationalInsuranceNumber);
    }

    private ClientInfo buildClientInfo(){
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setFirstName(firstName);
        clientInfo.setSurname(surname);
        clientInfo.setNINumber(nationalInsuranceNumber);
        clientInfo.setHomeOfficeReference(homeOfficeReference);
        clientInfo.setCaseReferenceNumber(caseReferenceNumber);
        return clientInfo;
    }
}
