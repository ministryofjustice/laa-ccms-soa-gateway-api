package uk.gov.laa.ccms.soa.gateway.controller;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.ClientsApi;
import uk.gov.laa.ccms.soa.gateway.model.ClientCreated;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.model.TransactionStatusResponse;
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

  @Override
  public ResponseEntity<TransactionStatusResponse> getClientStatus(
      String transactionRequestId,
      String soaGatewayUserLoginId,
      String soaGatewayUserRole) {
    log.info("GET /clients/status/{}", transactionRequestId);
    try {
      String status = clientDetailsService.getClientStatus(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          transactionRequestId);

      return ResponseEntity.ok(new TransactionStatusResponse().status(status));
    } catch (Exception e) {
      log.error("ClientDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @Override
  public ResponseEntity<ClientDetails> getClients(
          final String soaGatewayUserLoginId,
          final String soaGatewayUserRole,
          final String firstName,
          final String surname,
          final Date dateOfBirth,
          final String gender,
          final String caseReferenceNumber,
          final String homeOfficeReference,
          final String nationalInsuranceNumber,
          final Integer maxRecords,
          final Pageable pageable) {
    log.info("GET /clients");
    try {
      // Build a ClientSummary to hold the search criteria.
      // Note: caseReferenceNumber can actually hold a case or client reference.
      ClientSummary clientSummary = new ClientSummary()
              .firstName(firstName)
              .surname(surname)
              .dateOfBirth(dateOfBirth)
              .gender(gender)
              .clientReferenceNumber(caseReferenceNumber)
              .homeOfficeReference(homeOfficeReference)
              .nationalInsuranceNumber(nationalInsuranceNumber);

      log.info("clientSummary: " + clientSummary.toString());

      ClientDetails clientDetails = clientDetailsService.getClientDetails(
              soaGatewayUserLoginId,
              soaGatewayUserRole,
              maxRecords,
              clientSummary,
              pageable);

      return ResponseEntity.ok(clientDetails);
    } catch (Exception e) {
      log.error("ClientDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @Override
  public ResponseEntity<ClientCreated> postClient(
      String soaGatewayUserLoginId,
      String soaGatewayUserRole,
      ClientDetailDetails clientDetailDetails) {
    log.info("POST /clients");
    try {
      String transactionId = clientDetailsService.postClient(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          clientDetailDetails);

      return ResponseEntity.ok(new ClientCreated().transactionId(transactionId));
    } catch (Exception e) {
      log.error("ClientDetailsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }


}
