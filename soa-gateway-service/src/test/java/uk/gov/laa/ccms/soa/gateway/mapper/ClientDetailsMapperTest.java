package uk.gov.laa.ccms.soa.gateway.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientAddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientContactDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientUserDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientNameDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailRecordHistory;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.*;
import uk.gov.legalservices.enterprise.common._1_0.common.Name;
import uk.gov.laa.ccms.soa.gateway.model.ClientPersonalDetail;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.PersonalDetails;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ClientDetailsMapperTest {

    @InjectMocks
    ClientDetailsMapperImpl clientDetailsMapper;

    private DatatypeFactory datatypeFactory;

    @BeforeEach
    public void setup() throws DatatypeConfigurationException {
        datatypeFactory = DatatypeFactory.newInstance();
    }

    @Test
    public void testToClientDetails() {
        ClientInqRS response = new ClientInqRS();
        ClientInqRS.ClientList clientListObject = new ClientInqRS.ClientList();
        List<ClientList> clientList = Collections.singletonList(new ClientList());
        clientListObject.getClient().addAll(clientList);
        response.setClientList(clientListObject);

        List<ClientSummary> clientSummaryList = Collections.singletonList(new ClientSummary());
        Page<ClientSummary> clientDetailPage = new PageImpl<>(clientSummaryList, Pageable.unpaged(), clientSummaryList.size());

        ClientDetails result = clientDetailsMapper.toClientDetails(clientDetailPage);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testToClientDetailList() {
        ClientInqRS response = new ClientInqRS();
        ClientInqRS.ClientList clientListObject = new ClientInqRS.ClientList();
        List<ClientList> clientList = Collections.singletonList(buildClientList());
        clientListObject.getClient().addAll(clientList);
        response.setClientList(clientListObject);

        List<ClientSummary> result = clientDetailsMapper.toClientSummaryList(response);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testToClientSummary() {
        ClientList clientList = buildClientList();

        ClientSummary result = clientDetailsMapper.toClientSummary(clientList);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSurname());
        assertEquals("Smith", result.getSurnameAtBirth());
        assertEquals("John Doe", result.getFullName());
        assertEquals("HO12345", result.getHomeOfficeReference());
        assertEquals("AB123456C", result.getNationalInsuranceNumber());
        assertEquals("CR123", result.getCaseReferenceNumber());
    }

    @Test
    public void testToClientInfo() {
        // Test setup
        ClientSummary clientSummary= new ClientSummary();
        clientSummary.setNationalInsuranceNumber("AB123456C");
        clientSummary.setGender("Male");
        clientSummary.setFirstName("John");
        clientSummary.setSurname("Doe");
        clientSummary.setDateOfBirth(new Date());
        clientSummary.setHomeOfficeReference("HO12345");
        clientSummary.setCaseReferenceNumber("CR123");

        // Test execution
        ClientInfo result = clientDetailsMapper.toClientInfo(clientSummary);

        // Test verification
        assertNotNull(result);
        assertEquals("AB123456C", result.getNINumber());
        assertEquals("Male", result.getGender());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSurname());
        assertNotNull(result.getDateOfBirth());
        assertEquals("HO12345", result.getHomeOfficeReference());
        assertEquals("CR123", result.getCaseReferenceNumber());
    }

    @Test
    public void testToClientPersonalDetail() {
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setNINumber("AB123456C");
        personalDetails.setGender("Male");
        personalDetails.setMaritalStatus("Single");
        personalDetails.setHomeOfficeNumber("HO12345");

        ClientPersonalDetail result = clientDetailsMapper.toClientPersonalDetail(personalDetails);

        assertNotNull(result);
        assertEquals("AB123456C", result.getNationalInsuranceNumber());
        assertEquals("Male", result.getGender());
        assertEquals("Single", result.getMaritalStatus());
        assertEquals("HO12345", result.getHomeOfficeNumber());
    }

    @Test
    public void testToClientDetailDetails() {
        uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails clientDetails =
                new uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails();

        Address address = new Address();
        address.setAddressID("12345");
        clientDetails.setAddress(address);

        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setNINumber("AB123456C");
        clientDetails.setPersonalInformation(personalDetails);

        ClientDetailDetails result = clientDetailsMapper.toClientDetailDetails(clientDetails);

        assertNotNull(result);
        assertNotNull(result.getAddress());
        assertEquals("12345", result.getAddress().getAddressId());
        assertNotNull(result.getPersonalInformation());
        assertEquals("AB123456C", result.getPersonalInformation().getNationalInsuranceNumber());
    }

    @Test
    public void testToClientDetailDetailsAddress() {
        Address address = new Address();
        address.setAddressID("12345");
        address.setHouse("123 Main St");
        address.setCity("London");
        address.setCountry("UK");
        address.setPostalCode("N1 1AA");

        ClientAddressDetail result = clientDetailsMapper.toClientAddressDetails(address);

        assertNotNull(result);
        assertEquals("12345", result.getAddressId());
        assertEquals("123 Main St", result.getHouse());
        assertEquals("London", result.getCity());
        assertEquals("UK", result.getCountry());
        assertEquals("N1 1AA", result.getPostalCode());
    }

    @Test
    public void testToClientDetailRecordHistory() {
        RecordHistory recordHistory = new RecordHistory();
        User createdUser = new User();
        createdUser.setUserLoginID("createdUser");
        recordHistory.setCreatedBy(createdUser);

        User updatedUser = new User();
        updatedUser.setUserLoginID("updatedUser");
        recordHistory.setLastUpdatedBy(updatedUser);

        ClientDetailRecordHistory result = clientDetailsMapper.toClientDetailRecordHistory(recordHistory);

        assertNotNull(result);
        assertEquals("createdUser", result.getCreatedBy().getUserLoginId());
        assertEquals("updatedUser", result.getLastUpdatedBy().getUserLoginId());
    }

    @Test
    public void testToClientDetail() {
        ClientInqRS clientInqRS = new ClientInqRS();
        Client client = new Client();
        client.setClientReferenceNumber("CRN123");

        uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails clientDetails =
                new uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails();
        client.setDetails(clientDetails);

        RecordHistory recordHistory = new RecordHistory();
        client.setRecordHistory(recordHistory);
        clientInqRS.setClient(client);

        ClientDetail result = clientDetailsMapper.toClientDetail(clientInqRS);

        assertNotNull(result);
        assertEquals("CRN123", result.getClientReferenceNumber());
        assertNotNull(result.getDetails());
        assertNotNull(result.getRecordHistory());
    }

    @Test
    public void testNameToClientNameDetail() {
        Name name = new Name();
        name.setTitle("Mr.");
        name.setSurname("Smith");
        name.setFirstName("John");

        ClientNameDetail result = clientDetailsMapper.nameToClientNameDetail(name);

        assertNotNull(result);
        assertEquals("Mr.", result.getTitle());
        assertEquals("Smith", result.getSurname());
        assertEquals("John", result.getFirstName());
    }

    @Test
    public void testContactDetailsToClientContactDetail() {
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setTelephoneHome("123456");
        contactDetails.setEmailAddress("john@doe.com");

        ClientContactDetail result = clientDetailsMapper.contactDetailsToClientContactDetail(contactDetails);

        assertNotNull(result);
        assertEquals("123456", result.getTelephoneHome());
        assertEquals("john@doe.com", result.getEmailAddress());
    }

    @Test
    public void testUserToClientUserDetail() {
        User user = new User();
        user.setUserLoginID("userID");
        user.setUserName("username");

        ClientUserDetail result = clientDetailsMapper.userToClientUserDetail(user);

        assertNotNull(result);
        assertEquals("userID", result.getUserLoginId());
        assertEquals("username", result.getUserName());
    }

    @Test
    public void testDisabilityDetailsToClientDetailDetailsDisabilityMonitoring_Null() {
        uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetailsDisabilityMonitoring result = clientDetailsMapper.disabilityDetailsToClientDetailDetailsDisabilityMonitoring(null);
        assertNull(result);
    }

    @Test
    public void testDisabilityDetailsToClientDetailDetailsDisabilityMonitoring_DisabilityTypesNotEmpty() {
        DisabilityDetails disabilityDetails = new DisabilityDetails();
        List<String> disabilityTypes = new ArrayList<>();
        disabilityTypes.add("type1");
        disabilityTypes.add("type2");

        for (String type: disabilityTypes){
            disabilityDetails.getDisabilityType().add(type);
        }

        uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetailsDisabilityMonitoring result = clientDetailsMapper.disabilityDetailsToClientDetailDetailsDisabilityMonitoring(disabilityDetails);

        assertNotNull(result);
        assertNotNull(result.getDisabilityType());
        assertEquals(2, result.getDisabilityType().size());
        assertEquals("type1", result.getDisabilityType().get(0));
        assertEquals("type2", result.getDisabilityType().get(1));
    }

    @Test
    public void testDisabilityDetailsToClientDetailDetailsDisabilityMonitoring_DisabilityTypesEmpty() {
        DisabilityDetails disabilityDetails = new DisabilityDetails();

        uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetailsDisabilityMonitoring result = clientDetailsMapper.disabilityDetailsToClientDetailDetailsDisabilityMonitoring(disabilityDetails);

        assertNotNull(result);
        assertNotNull(result.getDisabilityType());
        assertTrue(result.getDisabilityType().isEmpty());
    }


    private ClientList buildClientList() {
        ClientList clientList = new ClientList();
        Name name = new Name();
        name.setFirstName("John");
        name.setSurname("Doe");
        name.setSurnameAtBirth("Smith");
        name.setFullName("John Doe");
        clientList.setName(name);
        clientList.setHomeOfficeReference("HO12345");
        clientList.setNINumber("AB123456C");
        clientList.setClientReferenceNumber("CR123");
        return clientList;
    }
}
