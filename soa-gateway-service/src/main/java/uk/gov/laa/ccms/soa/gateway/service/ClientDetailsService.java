package uk.gov.laa.ccms.soa.gateway.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ClientServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ClientDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.util.PaginationUtil;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddUpdtStatusRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

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
public class ClientDetailsService {

  private final ClientServicesClient clientServicesClient;

  private final ClientDetailsMapper clientDetailsMapper;



  /**
   * Retrieves client details based on the provided search criteria and client summary.
   *
   * <p>This method communicates with the external Client Services system using the provided
   * criteria and client summary, fetches the relevant client details, and then maps and
   * paginates these details to the desired format.</p>
   *
   * @param soaGatewayUserLoginId  The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole     The user role in the SOA Gateway.
   * @param maxRecords             The maximum number of records to retrieve.
   * @param clientSummary          The summary details of the client.
   * @param pageable               The pagination details.
   * @return                       A {@link ClientDetails} object containing the retrieved and
   *                               processed client details.
   */
  public ClientDetails getClientDetails(
          final String soaGatewayUserLoginId,
          final String soaGatewayUserRole,
          final Integer maxRecords,
          final ClientSummary clientSummary,
          final Pageable pageable
  ) {
    log.info("ClientDetailsService - getClientDetails");
    ClientInfo clientInfo =  clientDetailsMapper.toClientInfo(clientSummary);

    ClientInqRS response = clientServicesClient.getClientDetails(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords,
            clientInfo);

    List<ClientSummary> clientDetailList = clientDetailsMapper.toClientSummaryList(response);

    Page<ClientSummary> page = PaginationUtil.paginateList(pageable, clientDetailList);

    return clientDetailsMapper.toClientDetails(page);
  }

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

  /**
   * Fetches the status of a client transaction using its transaction ID. The method communicates
   * with the external Client Services system and retrieves the current status of the specified
   * transaction.
   *
   * @param soaGatewayUserLoginId      User login ID for the SOA Gateway.
   * @param soaGatewayUserRole         User role in the SOA Gateway.
   * @param transactionId              The transaction ID for which the status is to be fetched.
   * @return                           The status of the specified client transaction.
   */
  public uk.gov.laa.ccms.soa.gateway.model.ClientStatus getClientStatus(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String transactionId
  ) {
    log.info("ClientDetailsService - getClientDetail");
    ClientAddUpdtStatusRS response = clientServicesClient.getClientStatus(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        transactionId);

    return clientDetailsMapper.toClientStatus(response);
  }

}
