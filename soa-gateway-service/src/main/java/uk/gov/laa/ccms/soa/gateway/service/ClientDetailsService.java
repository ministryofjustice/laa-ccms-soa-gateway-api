package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ClientServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ClientDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRS;

/**
 * Service responsible for handling client details operations.
 *
 * <p>This service class communicates with the external Client Services system to fetch and
 * manage client details. It leverages a client services client and a client details mapper
 * to achieve this functionality.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientDetailsService extends AbstractSoaService {

  private final ClientServicesClient clientServicesClient;

  private final ClientDetailsMapper clientDetailsMapper;



  /**
   * Retrieves detailed information for a specific client based on the provided client reference
   * number.
   *
   * <p>This method communicates with the external Client Services system using the provided
   * user credentials and client reference number, then fetches the comprehensive details for
   * that specific client.</p>
   *
   * @param soaGatewayUserLoginId      The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole         The user role in the SOA Gateway.
   * @param maxRecords                 The maximum number of records to retrieve.
   * @param clientReferenceNumber      The unique reference number of the client.
   * @return                           A {@link ClientDetail} object containing the detailed
   *                                   information of the specified client.
   */
  public ClientDetail getClientDetail(
          final String soaGatewayUserLoginId,
          final String soaGatewayUserRole,
          final Integer maxRecords,
          final String clientReferenceNumber
  ) {
    log.info("ClientDetailsService - getClientDetail");
    ClientInqRS response = clientServicesClient.getClientDetail(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords,
            clientReferenceNumber);

    ClientDetail clientDetail = clientDetailsMapper.toClientDetail(response);
    log.debug("clientDetail, received: {}", clientDetail);

    return clientDetail;
  }

  /**
   * Submits new client details to the external Client Services system. The client details are
   * mapped and transformed before they're sent for submission. The method returns the transaction
   * ID of the submission.
   *
   * @param soaGatewayUserLoginId      User login ID for the SOA Gateway.
   * @param soaGatewayUserRole         User role in the SOA Gateway.
   * @param clientDetailDetails        The details of the client to be submitted.
   * @return                           The transaction ID for the submitted client details.
   */
  public String createClient(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final ClientDetailDetails clientDetailDetails
  ) {
    log.info("ClientDetailsService - createClient");
    uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails clientDetails
        =  clientDetailsMapper.toSoaClientDetails(clientDetailDetails);

    ClientAddRS response = clientServicesClient.postClientDetails(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        clientDetails);

    return response.getTransactionID();
  }

  /**
   * Submits an update to client details to the external Client Services system. The client details
   * are mapped and transformed before they're sent for submission. The method returns the
   * transaction ID of the submission.
   *
   * @param clientReferenceNumber      The client unique identifier.
   * @param soaGatewayUserLoginId      User login ID for the SOA Gateway.
   * @param soaGatewayUserRole         User role in the SOA Gateway.
   * @param clientDetailDetails        The details of the client to be submitted.
   * @return                           The transaction ID for the submitted client details.
   */
  public String updateClient(
      final String clientReferenceNumber,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final ClientDetailDetails clientDetailDetails
  ) {
    log.info("ClientDetailsService - updateClient");
    ClientUpdateRQ clientUpdateRq = clientDetailsMapper.toClientUpdateRq(
        clientReferenceNumber, clientDetailDetails);

    ClientUpdateRS response = clientServicesClient.updateClientDetails(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        clientUpdateRq);

    return response.getTransactionID();
  }


}
