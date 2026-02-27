package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestDetail;
import uk.gov.laa.ccms.soa.gateway.service.ProviderRequestsService;

@ExtendWith(MockitoExtension.class)
class ProviderRequestsControllerTest {

  private MockMvc mockMvc;

  @Mock private ProviderRequestsService providerRequestsService;

  @InjectMocks private ProviderRequestsController providerRequestsController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";

  @BeforeEach
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(providerRequestsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @DisplayName("POST /provider-requests - Successful Submission")
  void testSubmitProviderRequest_Success() throws Exception {
    final ProviderRequestDetail providerRequestDetail = new ProviderRequestDetail();
    final String expectedNotificationId = "notif123";

    Mockito.when(
            providerRequestsService.submitProviderRequest(
                eq(SOA_GATEWAY_USER_LOGIN_ID),
                eq(SOA_GATEWAY_USER_ROLE),
                eq(providerRequestDetail)))
        .thenReturn(expectedNotificationId);

    mockMvc
        .perform(
            post("/provider-requests")
                .content(objectMapper.writeValueAsString(providerRequestDetail))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isOk());

    Mockito.verify(providerRequestsService)
        .submitProviderRequest(
            eq(SOA_GATEWAY_USER_LOGIN_ID), eq(SOA_GATEWAY_USER_ROLE), eq(providerRequestDetail));
  }

  @Test
  @DisplayName("POST /provider-requests - Exception Handling")
  void testSubmitProviderRequest_Exception() throws Exception {
    final ProviderRequestDetail providerRequestDetail = new ProviderRequestDetail();

    Mockito.when(providerRequestsService.submitProviderRequest(any(), any(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    mockMvc
        .perform(
            post("/provider-requests")
                .content(objectMapper.writeValueAsString(providerRequestDetail))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isInternalServerError());

    Mockito.verify(providerRequestsService).submitProviderRequest(any(), any(), any());
  }
}
