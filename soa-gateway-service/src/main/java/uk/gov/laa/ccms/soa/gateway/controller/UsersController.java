package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.UsersApi;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.UserOptions;
import uk.gov.laa.ccms.soa.gateway.service.UsersService;

/**
 * Controller for handling user related requests.
 *
 * <p>Provides an endpoint for updating user profile options. Implements the {@link UsersApi} for
 * consistent behavior with other API implementations.</p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

  private final UsersService usersService;

  /**
   * Update user profile options in Ebs.
   *
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param userOptions User options (required) - the updated user options.
   * @return a ResponseEntity wrapping a ClientTransactionResponse.
   */
  @Override
  public ResponseEntity<ClientTransactionResponse> updateUserOptions(String soaGatewayUserLoginId,
      String soaGatewayUserRole, UserOptions userOptions) {
    log.info("PUT /users/options");
    try {
      String transactionId = usersService.updateUserOptions(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          userOptions);

      return ResponseEntity.ok(new ClientTransactionResponse().transactionId(transactionId));
    } catch (Exception e) {
      log.error("UsersController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
