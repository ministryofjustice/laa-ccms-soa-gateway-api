package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.ProviderRequestsApi;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestDetail;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestResponse;
import uk.gov.laa.ccms.soa.gateway.service.ProviderRequestsService;

/** REST controller for handling provider requests. */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ProviderRequestsController implements ProviderRequestsApi {

  private final ProviderRequestsService providerRequestsService;

  /**
   * Handles submission of a provider request.
   *
   * @param soaGatewayUserLoginId the login ID of the SOA gateway user
   * @param soaGatewayUserRole the role of the SOA gateway user
   * @param providerRequestDetail the details of the provider request
   * @return a response entity containing the provider request response or an internal server error
   */
  @Override
  public ResponseEntity<ProviderRequestResponse> submitProviderRequest(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final ProviderRequestDetail providerRequestDetail) {
    log.info("POST /provider-requests");
    try {
      final String notificationId =
          providerRequestsService.submitProviderRequest(
              soaGatewayUserLoginId, soaGatewayUserRole, providerRequestDetail);
      return ResponseEntity.ok(new ProviderRequestResponse().notificationId(notificationId));
    } catch (final Exception e) {
      log.error("ProviderRequestsController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
