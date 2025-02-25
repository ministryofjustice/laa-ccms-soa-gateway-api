package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.ClientsApi;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.service.ClientDetailsService;

/**
 * Controller for managing client details related requests.
 *
 * <p> Provides endpoints for retrieving specific client detail or a list of clients based on
 * various search criteria. Implements the{@link ClientsApi} to ensure consistent API behavior.</p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientDetailsController implements ClientsApi {

  private final ClientDetailsService clientDetailsService;

  /**
   * Get client details for the supplied reference number.
   *
   * @param clientReferenceNumber  (required) - the client reference number.
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param maxRecords  (optional, default to 100) - the maximum records to query.
   * @return ResponseEntity containing the client detail.
   */
  @Override
  public ResponseEntity<ClientDetail> getClient(
          final String clientReferenceNumber,
          final String soaGatewayUserLoginId,
          final String soaGatewayUserRole,
          final Integer maxRecords) {
    log.info("GET /clients/{}", clientReferenceNumber);
    try {
      ClientDetail clientDetail = clientDetailsService.getClientDetail(
              soaGatewayUserLoginId,
              soaGatewayUserRole,
              maxRecords,
              clientReferenceNumber);

      return ResponseEntity.ok(clientDetail);
    } catch (Exception e) {
      log.error("ClientDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Update the details of a client.
   *
   * @param clientReferenceNumber  (required) - the client reference number to update.
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param clientDetailDetails update a client (required) - the new details for the client.
   * @return a ResponseEntity wrapping a ClientTransactionResponse.
   */
  @Override
  public ResponseEntity<ClientTransactionResponse> updateClient(
      final String clientReferenceNumber,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final ClientDetailDetails clientDetailDetails) {
    log.info("PUT /clients");
    try {
      String transactionId = clientDetailsService.updateClient(
          clientReferenceNumber,
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          clientDetailDetails);

      return ResponseEntity.ok(new ClientTransactionResponse().transactionId(transactionId));
    } catch (Exception e) {
      log.error("ClientDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Create a new client record.
   *
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param clientDetailDetails Create a client (required) - the new client details.
   * @return a ResponseEntity wrapping a ClientTransactionResponse.
   */
  @Override
  public ResponseEntity<ClientTransactionResponse> createClient(
      String soaGatewayUserLoginId,
      String soaGatewayUserRole,
      ClientDetailDetails clientDetailDetails) {
    log.info("POST /clients");
    try {
      String transactionId = clientDetailsService.createClient(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          clientDetailDetails);

      return ResponseEntity.ok(new ClientTransactionResponse().transactionId(transactionId));
    } catch (Exception e) {
      log.error("ClientDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

}
