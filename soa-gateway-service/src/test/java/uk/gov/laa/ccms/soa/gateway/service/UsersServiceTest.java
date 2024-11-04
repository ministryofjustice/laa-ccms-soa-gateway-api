package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbim.UpdateUserRS;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbio.CCMSUser;
import uk.gov.laa.ccms.soa.gateway.client.UserClient;
import uk.gov.laa.ccms.soa.gateway.mapper.UserMapper;
import uk.gov.laa.ccms.soa.gateway.model.UserOptions;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

  @Mock
  private UserMapper userMapper;

  @Mock
  private UserClient userClient;

  @InjectMocks
  private UsersService usersService;

  @Test
  public void testUpdateUserOptions() {

    String soaGatewayUserLoginId = "soaGatewayUserLoginId";
    String soaGatewayUserRole = "soaGatewayUserRole";

    UserOptions userOptions = new UserOptions();

    CCMSUser ccmsUser = new CCMSUser();

    when(userMapper.toCcmsUser(userOptions)).thenReturn(ccmsUser);

    HeaderRSType headerRSType = new HeaderRSType();
    headerRSType.setTransactionID("12345");

    UpdateUserRS updateUserRS = new UpdateUserRS();
    updateUserRS.setHeaderRS(headerRSType);

    when(userClient.updateUser(soaGatewayUserLoginId, soaGatewayUserRole, ccmsUser))
        .thenReturn(updateUserRS);

    String transactionId = usersService.updateUserOptions(soaGatewayUserLoginId, soaGatewayUserRole,
        userOptions);

    assertEquals("12345", transactionId);

    verify(userMapper).toCcmsUser(userOptions);
    verify(userClient).updateUser(soaGatewayUserLoginId, soaGatewayUserRole, ccmsUser);

  }

}
