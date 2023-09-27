package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailRecordHistory;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientPersonalDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.model.NameDetail;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.Client;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ContactDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.DisabilityDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.PersonalDetails;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.Name;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

@ExtendWith(MockitoExtension.class)
public class ClientDetailsMapperTest {

    @InjectMocks
    ClientDetailsMapperImpl clientDetailsMapper;

    @Test
    public void testToClientDetails() {
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
        assertEquals("CR123", result.getClientReferenceNumber());
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
        clientSummary.setClientReferenceNumber("CR123");

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

        uk.gov.legalservices.enterprise.common._1_0.common.Address address =
            new uk.gov.legalservices.enterprise.common._1_0.common.Address();
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

        AddressDetail result = clientDetailsMapper.toAddressDetail(address);

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

        NameDetail result = clientDetailsMapper.nameToNameDetail(name);

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

        uk.gov.laa.ccms.soa.gateway.model.ContactDetail result = clientDetailsMapper.contactDetailsToContactDetail(contactDetails);

        assertNotNull(result);
        assertEquals("123456", result.getTelephoneHome());
        assertEquals("john@doe.com", result.getEmailAddress());
    }

    @Test
    public void testUserToClientUserDetail() {
        User user = new User();
        user.setUserLoginID("userID");
        user.setUserName("username");

        UserDetail result = clientDetailsMapper.userToUserDetail(user);

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

    @Test
    public void whenPersonalInformationIsNotNull_ReturnPersonalDetails() throws
        DatatypeConfigurationException {
        ClientPersonalDetail personalInformation = new ClientPersonalDetail();
        personalInformation.setNationalInsuranceNumber("AB123456C");
        personalInformation.setDateOfBirth(new Date());
        personalInformation.setDateOfDeath(new Date());
        personalInformation.setGender("Male");
        personalInformation.setMaritalStatus("Single");
        personalInformation.setHomeOfficeNumber("HO123456");
        personalInformation.setVulnerableClient(true);
        personalInformation.setHighProfileClient(true);
        personalInformation.setVexatiousLitigant(false);
        personalInformation.setCountryOfOrigin("UK");
        personalInformation.setMentalCapacityInd(true);

        PersonalDetails personalDetails = clientDetailsMapper.toPersonalDetail(personalInformation);
        assertNotNull(personalDetails);
        assertEquals("AB123456C", personalDetails.getNINumber());
        assertNotNull(personalDetails.getDateOfBirth());
        assertNotNull(personalDetails.getDateOfDeath());
        assertEquals("Male", personalDetails.getGender());
        assertEquals("Single", personalDetails.getMaritalStatus());
        assertEquals("HO123456", personalDetails.getHomeOfficeNumber());
        assertTrue(personalDetails.isVulnerableClient());
        assertTrue(personalDetails.isHighProfileClient());
        assertFalse(personalDetails.isVexatiousLitigant());
        assertEquals("UK", personalDetails.getCountryOfOrigin());
        assertTrue(personalDetails.isMentalCapacityInd());
    }

    @Test
    public void whenAddressDetailIsNotNull_ReturnAddress() {
        AddressDetail addressDetail = new AddressDetail();
        addressDetail.setAddressId("Id");
        addressDetail.setCareOfName("John Doe");
        addressDetail.setHouse("100");
        addressDetail.setAddressLine1("Street 1");
        addressDetail.setAddressLine2("Street 2");
        addressDetail.setAddressLine3("Street 3");
        addressDetail.setAddressLine4("Street 4");
        addressDetail.setCity("City");
        addressDetail.setCountry("Country");
        addressDetail.setCounty("County");
        addressDetail.setState("State");
        addressDetail.setProvince("Province");
        addressDetail.setPostalCode("12345");

        Address address = clientDetailsMapper.toAddress(addressDetail);
        assertNotNull(address);
        assertEquals("Id", address.getAddressID());
        assertEquals("John Doe", address.getCoffName());
        assertEquals("100", address.getHouse());
        assertEquals("Street 1", address.getAddressLine1());
        assertEquals("Street 2", address.getAddressLine2());
        assertEquals("Street 3", address.getAddressLine3());
        assertEquals("Street 4", address.getAddressLine4());
        assertEquals("City", address.getCity());
        assertEquals("Country", address.getCountry());
        assertEquals("County", address.getCounty());
        assertEquals("State", address.getState());
        assertEquals("Province", address.getProvince());
        assertEquals("12345", address.getPostalCode());
    }

    @Test
    public void whenNameDetailIsNull_ReturnsNull() {
        assertNull(clientDetailsMapper.nameDetailToName(null));
    }

    @Test
    public void whenNameDetailIsNotNull_ReturnsName() {
        NameDetail nameDetail = new NameDetail();
        nameDetail.setTitle("Mr.");
        nameDetail.setSurname("Doe");
        nameDetail.setFirstName("John");
        nameDetail.setMiddleName("Middle");
        nameDetail.setSurnameAtBirth("Smith");
        nameDetail.setFullName("Mr. John Middle Doe");

        Name name = clientDetailsMapper.nameDetailToName(nameDetail);

        assertNotNull(name);
        assertEquals("Mr.", name.getTitle());
        assertEquals("Doe", name.getSurname());
        assertEquals("John", name.getFirstName());
        assertEquals("Middle", name.getMiddleName());
        assertEquals("Smith", name.getSurnameAtBirth());
        assertEquals("Mr. John Middle Doe", name.getFullName());
    }

    @Test
    public void whenAddressDetailIsNull_ReturnNull() {
        assertNull(clientDetailsMapper.toAddress(null));
    }

    @Test
    public void whenClientDetailDetailsDisabilityMonitoringIsNull_ReturnsNull() {
        assertNull(clientDetailsMapper.clientDetailDetailsDisabilityMonitoringToDisabilityDetails(null));
    }

    @Test
    public void whenClientDetailDetailsDisabilityMonitoringIsNotNull_ReturnsDisabilityDetails() {
        uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetailsDisabilityMonitoring monitoring = new uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetailsDisabilityMonitoring();
        List<String> disabilityTypes = Arrays.asList("Visual", "Hearing", "Mobility");
        monitoring.setDisabilityType(disabilityTypes);

        DisabilityDetails disabilityDetails = clientDetailsMapper.clientDetailDetailsDisabilityMonitoringToDisabilityDetails(monitoring);

        assertNotNull(disabilityDetails);
        assertEquals(disabilityTypes, disabilityDetails.getDisabilityType());
    }

    @Test
    public void whenClientDetailDetailsDisabilityMonitoringHasNoDisabilityType_ReturnsDisabilityDetailsWithEmptyList() {
        uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetailsDisabilityMonitoring monitoring = new uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetailsDisabilityMonitoring();

        DisabilityDetails disabilityDetails = clientDetailsMapper.clientDetailDetailsDisabilityMonitoringToDisabilityDetails(monitoring);

        assertNotNull(disabilityDetails);
        assertTrue(disabilityDetails.getDisabilityType().isEmpty());
    }

    @Test
    public void whenContactDetailIsNull_ReturnsNull() {
        assertNull(clientDetailsMapper.contactDetailToContactDetails(null));
    }

    @Test
    public void whenContactDetailIsNotNull_ReturnsContactDetails() {
        uk.gov.laa.ccms.soa.gateway.model.ContactDetail contactDetail = new uk.gov.laa.ccms.soa.gateway.model.ContactDetail();
        contactDetail.setTelephoneHome("0123456789");
        contactDetail.setTelephoneWork("9876543210");
        contactDetail.setMobileNumber("1234567890");
        contactDetail.setEmailAddress("test@example.com");
        contactDetail.setPassword("password123");
        contactDetail.setPasswordReminder("My reminder");
        contactDetail.setCorrespondenceMethod("Email");
        contactDetail.setCorrespondenceLanguage("English");

        ContactDetails contactDetails = clientDetailsMapper.contactDetailToContactDetails(contactDetail);

        assertNotNull(contactDetails);
        assertEquals("0123456789", contactDetails.getTelephoneHome());
        assertEquals("9876543210", contactDetails.getTelephoneWork());
        assertEquals("1234567890", contactDetails.getMobileNumber());
        assertEquals("test@example.com", contactDetails.getEmailAddress());
        assertEquals("password123", contactDetails.getPassword());
        assertEquals("My reminder", contactDetails.getPasswordReminder());
        assertEquals("Email", contactDetails.getCorrespondenceMethod());
        assertEquals("English", contactDetails.getCorrespondenceLanguage());
    }

    @Test
    public void whenClientDetailDetailsIsNull_ReturnsNull() {
        assertNull(clientDetailsMapper.toSoaClientDetails(null));
    }

    @Test
    public void whenClientDetailDetailsIsNotNull_ReturnsClientDetails() {
        ClientDetailDetails clientDetailDetails = new ClientDetailDetails();
        // Assuming setters for each field are present; populate them with mock data:
        clientDetailDetails.setName(new NameDetail());
        clientDetailDetails.setPersonalInformation(new ClientPersonalDetail());
        clientDetailDetails.setContacts(new uk.gov.laa.ccms.soa.gateway.model.ContactDetail());
        clientDetailDetails.setDisabilityMonitoring(new uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetailsDisabilityMonitoring());
        clientDetailDetails.setNoFixedAbode(true);
        clientDetailDetails.setAddress(new AddressDetail());
        clientDetailDetails.setEthnicMonitoring("SomeValue");
        clientDetailDetails.setSpecialConsiderations("Some considerations");

        uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails result = clientDetailsMapper.toSoaClientDetails(clientDetailDetails);

        assertNotNull(result);
        // Validate each field (based on mock data):
        assertNotNull(result.getName());
        assertNotNull(result.getPersonalInformation());
        assertNotNull(result.getContacts());
        assertNotNull(result.getDisabilityMonitoring());
        assertTrue(result.isNoFixedAbode());
        assertNotNull(result.getAddress());
        assertEquals("SomeValue", result.getEthnicMonitoring());
        assertEquals("Some considerations", result.getSpecialConsiderations());
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
