package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbim.UpdateUserRS;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbio.CCMSUser;
import uk.gov.laa.ccms.soa.gateway.client.UserClient;
import uk.gov.laa.ccms.soa.gateway.mapper.UserMapper;
import uk.gov.laa.ccms.soa.gateway.model.UserOptions;

/**
 * Service class responsible for processing user options.
 *
 * <p>This service interacts with the external Users system to update user options via the
 * {@link UserClient}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {

  private final UserMapper userMapper;
  private final UserClient userClient;

  /**
   * Updates user profile options in Ebs.
   *
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param userOptions User options (required) - the updated user options.
   * @return the transaction ID.
   */
  public String updateUserOptions(String soaGatewayUserLoginId, String soaGatewayUserRole,
      UserOptions userOptions) {
    CCMSUser ccmsUser = userMapper.toCcmsUser(userOptions);

    UpdateUserRS response = userClient.updateUser(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        ccmsUser
    );

    return response.getHeaderRS().getTransactionID();
  }

}
