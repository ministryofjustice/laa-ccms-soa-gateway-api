package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbio.CCMSUser;
import uk.gov.laa.ccms.soa.gateway.model.UserOptions;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

  @Mock
  CommonMapper commonMapper;

  @InjectMocks
  UserMapperImpl userMapper;

  @Test
  public void testToCcmsUser() {
    UserOptions userOptions = new UserOptions();
    userOptions.setUserLoginId("user@login.com");
    userOptions.setProviderFirmId("12345");

    CCMSUser user = userMapper.toCcmsUser(userOptions);

    assertEquals("user@login.com", user.getLoginID());
    assertEquals(new BigDecimal(12345), user.getProviderFirmID());
  }

}
