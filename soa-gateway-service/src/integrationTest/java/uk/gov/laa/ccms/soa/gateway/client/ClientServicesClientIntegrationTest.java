package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withError;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

@SpringBootTest
@ContextConfiguration
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

    private static final String HEADER_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Header";
    private static final String MSG_NS = "http://legalservices.gov.uk/CCMS/ClientManagement/Client/1.0/ClientBIM";
    private static final String CLIENT_NS = "http://legalservices.gov.uk/CCMS/ClientManagement/Client/1.0/ClientBIO";
    private static final String COMMON_NS = "http://legalservices.gov.uk/Enterprise/Common/1.0/Common";

    private  String testLoginId;
    private   String testUserType;
    private  int maxRecords;
    private  String firstName;
    private  String surname;

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

    private ClientInfo buildClientInfo(){
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setFirstName(firstName);
        clientInfo.setSurname(surname);
        return clientInfo;
    }
}
