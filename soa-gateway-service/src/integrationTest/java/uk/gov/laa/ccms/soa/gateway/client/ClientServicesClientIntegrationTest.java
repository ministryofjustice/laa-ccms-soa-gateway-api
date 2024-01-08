package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withError;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddUpdtStatusRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ContactDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.PersonalDetails;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.Name;

@SpringBootTest
public class ClientServicesClientIntegrationTest {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private ClientServicesClient client;

    private static MockWebServiceServer mockServer;

    @Value("classpath:/payload/ClientInqRS_valid.xml")
    Resource clientInqRS_valid;

    @Value("classpath:/payload/ClientInqRS_valid_one.xml")
    Resource clientInqRS_valid_one;

    @Value("classpath:/payload/ClientAddRS_valid.xml")
    Resource clientAddRS_valid;

    @Value("classpath:/payload/ClientAddUpdtStatusRS_Valid.xml")
    Resource clientAddUpdtStatusRS_valid;
    private static final String HEADER_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
    private static final String MSG_NS = "http://legalservices.gov.uk/CCMS/ClientManagement/Client/1.0/ClientBIM";
    private static final String CLIENT_NS = "http://legalservices.gov.uk/CCMS/ClientManagement/Client/1.0/ClientBIO";
    private static final String COMMON_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";

    private String testTransactionId = "202309260908406430348479724";
    private  String testLoginId;
    private  String testUserType;
    private  int maxRecords;

    private  String title;
    private  String firstName;
    private  String surname;

    private final String fullname = firstName + " " + surname;
    private String houseNumber = "1";
    private String addressLine1 = "Test Address";
    private String city = "City";
    private String country = "GBR";
    private String postcode = "ABC 123";

    // Personal Information
    private String dateOfBirth = "2000-01-01" ;
    private String gender = "MALE";
    private String maritalStatus = "BE_LIV_TOG";
    private boolean vulnerableClient = false;
    private boolean highProfileClient = false;
    private boolean vexatiousLitigant = false;
    private String countryOfOrigin = "GBR";
    private boolean mentalCapacityInd = false;

    // Contacts
    private String telephoneHome = "1234567890";
    private String password = "password";
    private String passwordReminder = "apassword";
    private String correspondenceMethod = "LETTER";

    private Map<String, String> namespaces;

    @BeforeEach
    public void createServer() {
        mockServer = MockWebServiceServer.createServer(webServiceTemplate);

        testLoginId = "testLogin";
        testUserType = "testType";
        maxRecords = 50;
        firstName = "John";
        surname = "Doe";

        namespaces = new HashMap<>();
        namespaces.put("header", HEADER_NS);
        namespaces.put("msg", MSG_NS);
        namespaces.put("client", CLIENT_NS);
        namespaces.put("common", COMMON_NS);
    }

    @Test
    public void testGetClientDetails_ReturnsData() throws Exception {
        ClientInfo clientInfo = buildClientInfo();

        mockServer.expect(xpath("/msg:ClientInqRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
                .andExpect(xpath("/msg:ClientInqRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(testLoginId))
                .andExpect(xpath("/msg:ClientInqRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(testUserType))
                .andExpect(xpath("/msg:ClientInqRQ/msg:SearchCriteria/msg:ClientInfo/client:FirstName", namespaces).evaluatesTo(firstName))
                .andExpect(xpath("/msg:ClientInqRQ/msg:SearchCriteria/msg:ClientInfo/client:Surname", namespaces).evaluatesTo(surname))
                .andRespond(withPayload(clientInqRS_valid));


        ClientInqRS response = client.getClientDetails(testLoginId, testUserType,
                maxRecords, clientInfo);

        assertNotNull(response.getClientList());
        assertEquals(2, response.getClientList().getClient().size());

        mockServer.verify();
    }



    @Test
    public void testGetClientDetails_HandlesError() {
        ClientInfo clientInfo = buildClientInfo();

        mockServer.expect(xpath("/msg:ClientInqRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
                .andRespond(withError("Failed to call soap service"));

        assertThrows(RuntimeException.class, () -> client.getClientDetails(
                testLoginId, testUserType, maxRecords, clientInfo));

        mockServer.verify();
    }

    @Test
    public void testGetClientDetail_ReturnsData() throws Exception {
        String clientReferenceNumber = "12345";

        mockServer.expect(xpath("/msg:ClientInqRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
                .andExpect(xpath("/msg:ClientInqRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(testLoginId))
                .andExpect(xpath("/msg:ClientInqRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(testUserType))
                .andExpect(xpath("/msg:ClientInqRQ/msg:SearchCriteria/msg:ClientReferenceNumber", namespaces).evaluatesTo(clientReferenceNumber))
                .andRespond(withPayload(clientInqRS_valid_one));

        ClientInqRS response = client.getClientDetail(testLoginId, testUserType, maxRecords, clientReferenceNumber);

        assertNotNull(response);
        assertNotNull(response.getHeaderRS());
        assertNotNull(response.getHeaderRS().getRequestDetails());

        assertNotNull(response.getRecordCount());

        assertNotNull(response.getClient());
        assertEquals("9428827", response.getClient().getClientReferenceNumber());

        assertNotNull(response.getClient().getDetails());
        assertNotNull(response.getClient().getDetails().getName());
        assertEquals("MRS.", response.getClient().getDetails().getName().getTitle());
        assertEquals("Mcgettigan", response.getClient().getDetails().getName().getSurname());
        assertEquals("Vivienne", response.getClient().getDetails().getName().getFirstName());

        mockServer.verify();
    }

    @Test
    public void testGetClientDetail_HandlesError() {
        String clientReferenceNumber = "12345";

        mockServer.expect(xpath("/msg:ClientInqRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
                .andRespond(withError("Failed to call soap service"));

        assertThrows(RuntimeException.class, () -> client.getClientDetail(
                testLoginId, testUserType, maxRecords, clientReferenceNumber));

        mockServer.verify();
    }

    @Test
    public void testGetClientStatus_ReturnsData() throws IOException {

        mockServer.expect(xpath("/msg:ClientAddUpdtStatusRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
            .andExpect(xpath("/msg:ClientAddUpdtStatusRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(testLoginId))
            .andExpect(xpath("/msg:ClientAddUpdtStatusRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(testUserType))
            .andExpect(xpath("/msg:ClientAddUpdtStatusRQ/msg:TransactionID", namespaces).evaluatesTo(testTransactionId))
            .andRespond(withPayload(clientAddUpdtStatusRS_valid));

        ClientAddUpdtStatusRS response = client.getClientStatus(testLoginId, testUserType, testTransactionId);

        assertNotNull(response);
        assertNotNull(response.getHeaderRS());

        assertNotNull(response.getClientReferenceNumber());
        assertEquals("62586560", response.getClientReferenceNumber());

        mockServer.verify();
    }

    @Test
    public void testPostClient_ReturnsData() throws IOException, DatatypeConfigurationException {

        ClientDetails clientDetails = buildClientDetails();

        mockServer.expect(xpath("/msg:ClientAddRQ/header:HeaderRQ/header:TransactionRequestID", namespaces).exists())
            .andExpect(xpath("/msg:ClientAddRQ/header:HeaderRQ/header:UserLoginID", namespaces).evaluatesTo(testLoginId))
            .andExpect(xpath("/msg:ClientAddRQ/header:HeaderRQ/header:UserRole", namespaces).evaluatesTo(testUserType))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Name/common:Surname", namespaces).evaluatesTo(surname))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Name/common:FirstName", namespaces).evaluatesTo(firstName))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Name/common:SurnameAtBirth", namespaces).evaluatesTo(surname))

            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Address/common:House", namespaces).evaluatesTo(houseNumber))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Address/common:AddressLine1", namespaces).evaluatesTo(addressLine1))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Address/common:City", namespaces).evaluatesTo(city))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Address/common:Country", namespaces).evaluatesTo(country))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Address/common:PostalCode", namespaces).evaluatesTo(postcode))

            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:PersonalInformation/client:DateOfBirth", namespaces).evaluatesTo(dateOfBirth))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:PersonalInformation/client:Gender", namespaces).evaluatesTo(gender))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:PersonalInformation/client:MaritalStatus", namespaces).evaluatesTo(maritalStatus))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:PersonalInformation/client:VulnerableClient", namespaces).evaluatesTo(String.valueOf(vulnerableClient)))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:PersonalInformation/client:HighProfileClient", namespaces).evaluatesTo(String.valueOf(highProfileClient)))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:PersonalInformation/client:VexatiousLitigant", namespaces).evaluatesTo(String.valueOf(vexatiousLitigant)))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:PersonalInformation/client:CountryOfOrigin", namespaces).evaluatesTo(countryOfOrigin))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:PersonalInformation/client:MentalCapacityInd", namespaces).evaluatesTo(String.valueOf(mentalCapacityInd)))

            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Contacts/client:TelephoneHome", namespaces).evaluatesTo(telephoneHome))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Contacts/client:Password", namespaces).evaluatesTo(password))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Contacts/client:PasswordReminder", namespaces).evaluatesTo(passwordReminder))
            .andExpect(xpath("/msg:ClientAddRQ/msg:Client/client:Contacts/client:CorrespondenceMethod", namespaces).evaluatesTo(correspondenceMethod))
            .andRespond(withPayload(clientAddRS_valid));

        ClientAddRS response = client.postClientDetails(testLoginId, testUserType, clientDetails);

        assertNotNull(response);
        assertNotNull(response.getHeaderRS());

        assertNotNull(response.getTransactionID());
        assertEquals(testTransactionId, response.getTransactionID());

        mockServer.verify();
    }

    private ClientDetails buildClientDetails() throws DatatypeConfigurationException {
        ClientDetails clientDetails = new ClientDetails();
        Name name = new Name();
        name.setFirstName(firstName);
        name.setSurname(surname);
        name.setSurnameAtBirth(surname);

        Address address = new Address();
        address.setHouse(houseNumber);
        address.setAddressLine1(addressLine1);
        address.setCity(city);
        address.setCountry(country);
        address.setPostalCode(postcode);

        // Build PersonalInformation
        PersonalDetails personalInformation = new PersonalDetails();
        XMLGregorianCalendar gregorianDateOfBirth = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateOfBirth);
        personalInformation.setDateOfBirth(gregorianDateOfBirth);
        personalInformation.setGender(gender);
        personalInformation.setMaritalStatus(maritalStatus);
        personalInformation.setVulnerableClient(vulnerableClient);
        personalInformation.setHighProfileClient(highProfileClient);
        personalInformation.setVexatiousLitigant(vexatiousLitigant);
        personalInformation.setCountryOfOrigin(countryOfOrigin);
        personalInformation.setMentalCapacityInd(mentalCapacityInd);

        // Build Contacts
        ContactDetails contacts = new ContactDetails();
        contacts.setTelephoneHome(telephoneHome);
        contacts.setPassword(password);
        contacts.setPasswordReminder(passwordReminder);
        contacts.setCorrespondenceMethod(correspondenceMethod);

        clientDetails.setName(name);
        clientDetails.setPersonalInformation(personalInformation);
        clientDetails.setContacts(contacts);
        clientDetails.setAddress(address);
        return clientDetails;
    }

    private ClientInfo buildClientInfo(){
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setFirstName(firstName);
        clientInfo.setSurname(surname);
        return clientInfo;
    }
}
