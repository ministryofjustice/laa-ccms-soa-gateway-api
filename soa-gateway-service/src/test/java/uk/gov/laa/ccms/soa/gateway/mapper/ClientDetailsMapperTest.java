package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientPersonalDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.model.ContactDetail;
import uk.gov.laa.ccms.soa.gateway.model.NameDetail;
import uk.gov.laa.ccms.soa.gateway.model.TransactionStatus;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddUpdtStatusRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.Client;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ContactDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.DisabilityDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.PersonalDetails;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.Name;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.Status;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;

@ExtendWith(MockitoExtension.class)
public class ClientDetailsMapperTest {

    @Mock
    CommonMapper commonMapper;

    @InjectMocks
    ClientDetailsMapperImpl clientDetailsMapper;

    private final boolean clientStatuses = true;

    private final String telephoneHome = "1111111111";
    private final String telephoneWork = "2222222222";
    private final String telephoneMobile = "3333333333";
    private final String emailAddress = "test@test.com";
    private final String password = "password";
    private final String passwordReminder = "reminder";
    private final String correspondenceMethod = "LETTER";
    private final String correspondenceLanguage = "GBR";
    private final String countryOfOrigin = "UK";
    private final String gender = "MALE";
    private final String maritalStatus = "SINGLE";
    private final String nationalInsuranceNumber = "NI123456NI";
    private final String homeOfficeNumber = "HO123456HO";
    private final String day = "10";
    private final String month = "6";
    private final String year = "2000";


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

        when(commonMapper.toAddressDetail(address)).thenReturn(new AddressDetail());

        ClientDetailDetails result = clientDetailsMapper.toClientDetailDetails(clientDetails);

        assertNotNull(result);
        assertNotNull(result.getAddress());
        assertNotNull(result.getPersonalInformation());
        assertEquals("AB123456C", result.getPersonalInformation().getNationalInsuranceNumber());
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

        when(commonMapper.toRecordHistory(recordHistory))
            .thenReturn(new uk.gov.laa.ccms.soa.gateway.model.RecordHistory());

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

        when(commonMapper.toAddress(clientDetailDetails.getAddress()))
            .thenReturn(new Address());

        uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails result =
            clientDetailsMapper.toSoaClientDetails(clientDetailDetails);

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


    private ClientPersonalDetail buildClientPersonalDetail() {
        ClientPersonalDetail personalInformation = new ClientPersonalDetail();

        LocalDate localDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        Date dateOfBirth = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dateOfDeath = Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        personalInformation.setDateOfBirth(dateOfBirth);
        personalInformation.setDateOfDeath(dateOfDeath);
        personalInformation.setCountryOfOrigin(countryOfOrigin);
        personalInformation.setGender(gender);
        personalInformation.setNationalInsuranceNumber(nationalInsuranceNumber);
        personalInformation.setHomeOfficeNumber(homeOfficeNumber);
        personalInformation.setMaritalStatus(maritalStatus);
        personalInformation.setVulnerableClient(clientStatuses);
        personalInformation.setHighProfileClient(clientStatuses);
        personalInformation.setVexatiousLitigant(clientStatuses);
        personalInformation.setMentalCapacityInd(clientStatuses);
        return personalInformation;
    }

    private ContactDetail buildContactDetail() {
        ContactDetail contactDetail = new ContactDetail();
        contactDetail.setTelephoneHome(telephoneHome);
        contactDetail.setTelephoneWork(telephoneWork);
        contactDetail.setMobileNumber(telephoneMobile);
        contactDetail.setEmailAddress(emailAddress);
        contactDetail.setPassword(password);
        contactDetail.setPasswordReminder(passwordReminder);
        contactDetail.setCorrespondenceLanguage(correspondenceLanguage);
        contactDetail.setCorrespondenceMethod(correspondenceMethod);
        return contactDetail;
    }

    @Test
    void addClientPersonalDetailToClientUpdateRq() {

        ClientUpdateRQ clientUpdateRq = new ClientUpdateRQ();
        ClientPersonalDetail personalInformation = buildClientPersonalDetail();

        clientDetailsMapper.addClientPersonalDetailToClientUpdateRq(clientUpdateRq, personalInformation);

        assertEquals(personalInformation.getCountryOfOrigin(), clientUpdateRq.getCountryOfOrigin());
        assertEquals(personalInformation.getGender(), clientUpdateRq.getGender());
        assertEquals(personalInformation.getNationalInsuranceNumber(), clientUpdateRq.getNINumber());
        assertEquals(personalInformation.getHomeOfficeNumber(), clientUpdateRq.getHomeOfficeReference());
        assertEquals(personalInformation.getMaritalStatus(), clientUpdateRq.getMaritalStatus());
        assertEquals(personalInformation.isVulnerableClient(), clientUpdateRq.isVulnerableClient());
        assertEquals(personalInformation.isHighProfileClient(), clientUpdateRq.isHighProfileClient());
        assertEquals(personalInformation.isVexatiousLitigant(), clientUpdateRq.isVexatiousLitigant());
        assertEquals(personalInformation.isMentalCapacityInd(), clientUpdateRq.isMentalCapacityInd());
    }

    @Test
    void toClientUpdateRq_referenceOnly(){
        ClientUpdateRQ clientUpdateRQ = clientDetailsMapper.toClientUpdateRq("123456", new ClientDetailDetails());
        assertEquals("123456", clientUpdateRQ.getClientReferenceNumber());
    }

    @Test
    void toClientUpdateRq_null(){
        ClientUpdateRQ clientUpdateRQ = clientDetailsMapper.toClientUpdateRq(null, null);
        assertNull(clientUpdateRQ);
    }

    @Test
    void toClientUpdateRq_nullDetails(){
        ContactDetail contactDetail = buildContactDetail();

        ClientDetailDetails details = new ClientDetailDetails();
        details.setContacts(contactDetail);

        ClientUpdateRQ clientUpdateRQ = clientDetailsMapper.toClientUpdateRq("123456", details);

        assertEquals(telephoneHome, clientUpdateRQ.getTelephoneHome());
        assertEquals(telephoneWork, clientUpdateRQ.getTelephoneWork());
        assertEquals(telephoneMobile, clientUpdateRQ.getMobileNumber());
        assertEquals(emailAddress, clientUpdateRQ.getEmailAddress());
        assertEquals(password, clientUpdateRQ.getPassword());
        assertEquals(passwordReminder, clientUpdateRQ.getPasswordReminder());
        assertEquals(correspondenceMethod, clientUpdateRQ.getCorrespondenceMethod());
        assertEquals(correspondenceLanguage, clientUpdateRQ.getCorrespondenceLanguage());
    }

    @Test
    void toTransactionStatus() {
        ClientAddUpdtStatusRS clientAddUpdtStatusRS = new ClientAddUpdtStatusRS();
        clientAddUpdtStatusRS.setClientReferenceNumber("ref1");
        clientAddUpdtStatusRS.setHeaderRS(new HeaderRSType());
        clientAddUpdtStatusRS.getHeaderRS().setStatus(new Status());
        clientAddUpdtStatusRS.getHeaderRS().getStatus().setStatus(StatusTextType.ERROR);

        TransactionStatus result = clientDetailsMapper.toTransactionStatus(clientAddUpdtStatusRS);

        assertNotNull(result);
        assertEquals(clientAddUpdtStatusRS.getClientReferenceNumber(), result.getReferenceNumber());
        assertEquals(clientAddUpdtStatusRS.getHeaderRS().getStatus().getStatus().name(),
            result.getSubmissionStatus());
    }
}
