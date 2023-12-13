package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddUpdtStatusRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddUpdtStatusRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ObjectFactory;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

/**
 * Provides a client interface for interacting with Client Management Services in the SOA-based
 * system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on client management services. It provides facilities for retrieving client
 * details based on search criteria provided. Service name and URL details are injected at
 * runtime.</p>
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class ClientServicesClient extends AbstractSoaClient {

  private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link ClientServicesClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName        The name of the client management service.
   * @param serviceUrl         The URL endpoint for the client management service.
   */
  public ClientServicesClient(
          final WebServiceTemplate webServiceTemplate,
          @Value("${laa.ccms.soa-gateway.client.service-name}") final String serviceName,
          @Value("${laa.ccms.soa-gateway.client.service-url}") final String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Create client details based on the provided {@link ClientDetails}.
   *
   * @param loggedInUserId      The logged-in user ID.
   * @param loggedInUserType    The type of the logged-in user.
   * @param clientDetails       The client information to be saved.
   * @return ClientAddRS        Response containing client details.
   */
  public ClientAddRS postClientDetails(
      final String loggedInUserId,
      final String loggedInUserType,
      final ClientDetails clientDetails
  ) {
    final String soapAction = String.format("%s/CreateClient", serviceName);
    ClientAddRQ clientAddRq = CASE_FACTORY.createClientAddRQ();
    clientAddRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    clientAddRq.setClient(clientDetails);

    JAXBElement<ClientAddRS> response =
        (JAXBElement<ClientAddRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                serviceUrl,
                CASE_FACTORY.createClientAddRQ(clientAddRq),
                new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }

  /**
   * Update client details based on the provided {@link ClientDetails}.
   *
   * @param loggedInUserId      The logged-in user ID.
   * @param loggedInUserType    The type of the logged-in user.
   * @param clientUpdateRq       The client update information to be saved.
   * @return ClientAddRS        Response containing client details.
   */
  public ClientUpdateRS updateClientDetails(
      final String loggedInUserId,
      final String loggedInUserType,
      ClientUpdateRQ clientUpdateRq
  ) {
    final String soapAction = String.format("%s/UpdateClient", serviceName);
    clientUpdateRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    JAXBElement<ClientUpdateRS> response =
        (JAXBElement<ClientUpdateRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                serviceUrl,
                CASE_FACTORY.createClientUpdateRQ(clientUpdateRq),
                new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }

  /**
   * Retrieves the status of a client addition or update based on the provided transaction ID.
   *
   * <p>This method communicates with the service endpoint to fetch the status related to a
   * specific transaction pertaining to client addition or update. If the SOA call is not
   * successful, an exception will be thrown.</p>
   *
   * @param loggedInUserId   The ID of the user who is currently logged in.
   * @param loggedInUserType The type of the user who is currently logged in.
   * @param transactionId    The unique ID of the transaction for which the status is to be
   *                         fetched.
   * @return ClientAddUpdtStatusRS The response object containing the status of the client addition
   *         or update.
   */
  public ClientAddUpdtStatusRS getClientStatus(
      final String loggedInUserId,
      final String loggedInUserType,
      final String transactionId
  ) {
    final String soapAction = String.format("%s/GetClientTxnStatus", serviceName);
    ClientAddUpdtStatusRQ clientAddUpdtStatusRq = CASE_FACTORY.createClientAddUpdtStatusRQ();
    clientAddUpdtStatusRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    clientAddUpdtStatusRq.setTransactionID(transactionId);

    JAXBElement<ClientAddUpdtStatusRS> response =
        (JAXBElement<ClientAddUpdtStatusRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                serviceUrl,
                CASE_FACTORY.createClientAddUpdtStatusRQ(clientAddUpdtStatusRq),
                new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }

  /**
   * Retrieve client details based on the provided {@link ClientInfo} search criteria.
   *
   * @param loggedInUserId      The logged-in user ID.
   * @param loggedInUserType    The type of the logged-in user.
   * @param maxRecords          Maximum number of records to fetch.
   * @param clientInfo          The client information used for search.
   * @return ClientInqRS        Response containing client details.
   */
  public ClientInqRS getClientDetails(
          final String loggedInUserId,
          final String loggedInUserType,
          final Integer maxRecords,
          final ClientInfo clientInfo
  ) {

    final String soapAction = String.format("%s/GetClientDetails", serviceName);
    ClientInqRQ clientInqRq = CASE_FACTORY.createClientInqRQ();
    clientInqRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    ClientInqRQ.SearchCriteria searchCriteria = CASE_FACTORY
            .createClientInqRQSearchCriteria();

    searchCriteria.setClientInfo(clientInfo);

    return getClientInqRs(maxRecords, soapAction, clientInqRq, searchCriteria);
  }


  /**
   * Retrieve client details based on the provided client reference number.
   *
   * @param loggedInUserId          The logged-in user ID.
   * @param loggedInUserType        The type of the logged-in user.
   * @param maxRecords              Maximum number of records to fetch.
   * @param clientReferenceNumber   The client reference number used for search.
   * @return ClientInqRS            Response containing client details.
   */
  public ClientInqRS getClientDetail(
          final String loggedInUserId,
          final String loggedInUserType,
          final Integer maxRecords,
          final String clientReferenceNumber
  ) {

    final String soapAction = String.format("%s/GetClientDetails", serviceName);
    ClientInqRQ clientInqRq = CASE_FACTORY.createClientInqRQ();
    clientInqRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    ClientInqRQ.SearchCriteria searchCriteria = CASE_FACTORY
            .createClientInqRQSearchCriteria();

    searchCriteria.setClientReferenceNumber(clientReferenceNumber);

    return getClientInqRs(maxRecords, soapAction, clientInqRq, searchCriteria);
  }


  private ClientInqRS getClientInqRs(
          Integer maxRecords,
          String soapAction,
          ClientInqRQ clientInqRq,
          ClientInqRQ.SearchCriteria searchCriteria) {
    clientInqRq.setSearchCriteria(searchCriteria);
    clientInqRq.setRecordCount(createRecordCount(maxRecords));

    JAXBElement<ClientInqRS> response =
            (JAXBElement<ClientInqRS>) getWebServiceTemplate()
                    .marshalSendAndReceive(
                            serviceUrl,
                            CASE_FACTORY.createClientInqRQ(clientInqRq),
                            new SoapActionCallback(soapAction));


    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }
}
