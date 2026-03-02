package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ObjectFactory;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

@ExtendWith(MockitoExtension.class)
class ClientServicesClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";
  @Mock Logger mockLogger;

  @Mock WebServiceTemplate webServiceTemplate;

  @Captor ArgumentCaptor<JAXBElement<ClientInqRQ>> requestCaptor;

  private ClientServicesClient client;

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
    this.client = new ClientServicesClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
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
  public void testGetClientDetailsBuildsCorrectRequest() throws Exception {
    ObjectFactory objectFactory = new ObjectFactory();

    // Mock the response of the WebServiceTemplate
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createClientInqRS(new ClientInqRS()));

    ClientInfo clientInfo = buildClientInfo();

    ClientInqRS response =
        client.getClientDetails(soaGatewayUserLoginId, soaGatewayUserRole, maxRecords, clientInfo);

    // Verify interactions
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<ClientInqRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(soaGatewayUserLoginId, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(soaGatewayUserRole, payload.getValue().getHeaderRQ().getUserRole());
    assertEquals(firstName, payload.getValue().getSearchCriteria().getClientInfo().getFirstName());
    assertEquals(surname, payload.getValue().getSearchCriteria().getClientInfo().getSurname());
    assertEquals(
        nationalInsuranceNumber,
        payload.getValue().getSearchCriteria().getClientInfo().getNINumber());
    assertEquals(
        homeOfficeReference,
        payload.getValue().getSearchCriteria().getClientInfo().getHomeOfficeReference());
    assertEquals(
        clientReferenceNumber,
        payload.getValue().getSearchCriteria().getClientInfo().getCaseReferenceNumber());
    assertNotNull(response);
  }

  @Test
  public void testGetClientDetailWithValidReferenceNumber() throws Exception {
    ObjectFactory objectFactory = new ObjectFactory();

    // Mock the response of the WebServiceTemplate
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createClientInqRS(new ClientInqRS()));

    String clientReferenceNumber = "1234567890";

    ClientInqRS response =
        client.getClientDetail(
            soaGatewayUserLoginId, soaGatewayUserRole, maxRecords, clientReferenceNumber);

    // Verify interactions
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<ClientInqRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(soaGatewayUserLoginId, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(soaGatewayUserRole, payload.getValue().getHeaderRQ().getUserRole());
    assertNull(payload.getValue().getSearchCriteria().getClientInfo());
    assertEquals(
        clientReferenceNumber, payload.getValue().getSearchCriteria().getClientReferenceNumber());
    assertNotNull(response);
  }

  @Captor ArgumentCaptor<JAXBElement<ClientAddRQ>> clientAddRequestCaptor;

  @Test
  public void testPostClientDetailsBuildsCorrectRequest() throws Exception {
    // Mocking expected values
    ClientDetails mockClientDetails = new ClientDetails();

    ObjectFactory objectFactory = new ObjectFactory();
    ClientAddRS mockResponse = objectFactory.createClientAddRS();

    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createClientAddRS(mockResponse));

    ClientAddRS response =
        client.postClientDetails(soaGatewayUserLoginId, soaGatewayUserRole, mockClientDetails);

    // Verify interactions
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), clientAddRequestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<?> payload = clientAddRequestCaptor.getValue();
    assertEquals(mockClientDetails, ((ClientAddRQ) payload.getValue()).getClient());
    assertNotNull(response);
  }

  @Test
  public void testUpdateClientDetailsBuildsCorrectRequest() throws Exception {
    // Mocking expected values
    ClientUpdateRQ mockClientUpdateRq = new ClientUpdateRQ();

    ObjectFactory objectFactory = new ObjectFactory();
    ClientUpdateRS mockResponse = objectFactory.createClientUpdateRS();

    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createClientUpdateRS(mockResponse));

    ClientUpdateRS response =
        client.updateClientDetails(soaGatewayUserLoginId, soaGatewayUserRole, mockClientUpdateRq);

    // Verify interactions
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), clientAddRequestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<?> payload = clientAddRequestCaptor.getValue();
    assertEquals(mockClientUpdateRq, ((ClientUpdateRQ) payload.getValue()));
    assertNotNull(response);
  }

  private ClientInfo buildClientInfo() {
    ClientInfo clientInfo = new ClientInfo();
    clientInfo.setFirstName(firstName);
    clientInfo.setSurname(surname);
    clientInfo.setNINumber(nationalInsuranceNumber);
    clientInfo.setHomeOfficeReference(homeOfficeReference);
    clientInfo.setCaseReferenceNumber(clientReferenceNumber);
    return clientInfo;
  }
}
