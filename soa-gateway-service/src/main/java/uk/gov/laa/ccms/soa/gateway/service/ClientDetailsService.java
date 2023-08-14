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
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.util.PaginationUtil;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
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

}
