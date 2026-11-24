package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.laa.ccms.soa.gateway.advice.GlobalExceptionHandler;
import uk.gov.laa.ccms.soa.gateway.model.UserOptions;
import uk.gov.laa.ccms.soa.gateway.security.ProviderContext;
import uk.gov.laa.ccms.soa.gateway.security.ProviderScopeFilter;
import uk.gov.laa.ccms.soa.gateway.service.UsersService;

@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {

  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";
  private static final String CAAB_PROVIDER_ID = "12345";

  @Mock private UsersService usersService;

  @InjectMocks private UsersController usersController;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(usersController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .addFilters(new ProviderScopeFilter())
            .build();
  }

  @Test
  public void testUpdateUserOptions_Success() throws Exception {

    final UserOptions userOptions =
        new UserOptions().userLoginId("user@login.com").providerFirmId(CAAB_PROVIDER_ID);

    when(usersService.updateUserOptions(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, userOptions))
        .thenReturn("67890");

    mockMvc
        .perform(
            put("/users/options")
                .content(new ObjectMapper().writeValueAsString(userOptions))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID))
        .andExpect(status().isOk());

    verify(usersService)
        .updateUserOptions(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, userOptions);
  }

  @Test
  public void testUpdateUserOptions_ProviderSwitchDifferentTargetFirm_ReturnsOk() throws Exception {
    final UserOptions userOptions =
        new UserOptions().userLoginId("user@login.com").providerFirmId("200");

    when(usersService.updateUserOptions(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, userOptions))
        .thenReturn("tx-switch-200");

    mockMvc
        .perform(
            put("/users/options")
                .content(new ObjectMapper().writeValueAsString(userOptions))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, "100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.transaction_id").value("tx-switch-200"));

    verify(usersService)
        .updateUserOptions(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, userOptions);
  }

  @Test
  public void testUpdateUserOptions_MissingProviderHeader_ReturnsBadRequest() throws Exception {
    final UserOptions userOptions =
        new UserOptions().userLoginId("user@login.com").providerFirmId("200");

    mockMvc
        .perform(
            put("/users/options")
                .content(new ObjectMapper().writeValueAsString(userOptions))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isBadRequest());

    verify(usersService, never()).updateUserOptions(any(), any(), any());
  }

  @Test
  public void testUpdateUserOptions_Exception() throws Exception {
    final UserOptions userOptions =
        new UserOptions().userLoginId("user@login.com").providerFirmId(CAAB_PROVIDER_ID);

    when(usersService.updateUserOptions(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, userOptions))
        .thenThrow(new RuntimeException("SOA error"));

    mockMvc
        .perform(
            put("/users/options")
                .content(new ObjectMapper().writeValueAsString(userOptions))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void testUpdateUserOptions_nullLoginId_returnsBadRequest() throws Exception {

    final UserOptions userOptions = new UserOptions().providerFirmId("12345");

    mockMvc
        .perform(
            put("/users/options")
                .content(new ObjectMapper().writeValueAsString(userOptions))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateUserOptions_nullProviderFirmId_returnsBadRequest() throws Exception {

    final UserOptions userOptions = new UserOptions().userLoginId("user@login.com");

    mockMvc
        .perform(
            put("/users/options")
                .content(new ObjectMapper().writeValueAsString(userOptions))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID))
        .andExpect(status().isBadRequest());
  }
}
