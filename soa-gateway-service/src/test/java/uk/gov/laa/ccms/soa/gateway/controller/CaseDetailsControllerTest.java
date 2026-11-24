package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.laa.ccms.soa.gateway.advice.GlobalExceptionHandler;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.ProviderDetail;
import uk.gov.laa.ccms.soa.gateway.model.SubmittedApplicationDetails;
import uk.gov.laa.ccms.soa.gateway.security.ProviderContext;
import uk.gov.laa.ccms.soa.gateway.security.ProviderScopeFilter;
import uk.gov.laa.ccms.soa.gateway.service.CaseDetailsService;

@ExtendWith(MockitoExtension.class)
public class CaseDetailsControllerTest {

  @Mock private CaseDetailsService caseDetailsService;

  @InjectMocks private CaseDetailsController caseDetailsController;

  private ObjectMapper objectMapper = new ObjectMapper();

  private MockMvc mockMvc;

  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";
  private static final String CAAB_PROVIDER_ID = "12345";
  private static final String CASE_REFERENCE_NUMBER = "1234567890";
  private static final String PROVIDER_CASE_REFERENCE = "ABCDEF";
  private static final String CLIENT_SURNAME = "Doe";
  private static final String CASE_STATUS = "APPL";
  private static final Integer FEE_EARNER_ID = 123;
  private static final Integer OFFICE_ID = 987;

  private static final Integer MAX_RECORDS = 50;

  private static final Pageable PAGEABLE = Pageable.ofSize(20);

  @BeforeEach
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(caseDetailsController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .addFilters(new ProviderScopeFilter())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  private static CaseDetail caseForProvider(final String providerFirmId) {
    return new CaseDetail()
        .applicationDetails(
            new SubmittedApplicationDetails()
                .providerDetails(new ProviderDetail().providerFirmId(providerFirmId)));
  }

  @Test
  public void testCreateCase_Success() throws Exception {
    CaseDetail caseDetail = caseForProvider(CAAB_PROVIDER_ID);
    when(caseDetailsService.registerCase(
            SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, caseDetail))
        .thenReturn("tx-123");

    mockMvc
        .perform(
            post("/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID)
                .content(objectMapper.writeValueAsString(caseDetail)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.transaction_id").value("tx-123"));

    verify(caseDetailsService)
        .registerCase(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, caseDetail);
  }

  @Test
  public void testCreateCase_Exception() throws Exception {
    CaseDetail caseDetail = caseForProvider(CAAB_PROVIDER_ID);
    when(caseDetailsService.registerCase(any(), any(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    mockMvc
        .perform(
            post("/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID)
                .content(objectMapper.writeValueAsString(caseDetail)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void testCreateCase_MissingProviderHeader_ReturnsBadRequest() throws Exception {
    CaseDetail caseDetail = caseForProvider(CAAB_PROVIDER_ID);

    mockMvc
        .perform(
            post("/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .content(objectMapper.writeValueAsString(caseDetail)))
        .andExpect(status().isBadRequest());

    verify(caseDetailsService, never()).registerCase(any(), any(), any());
  }

  @ParameterizedTest
  @ValueSource(strings = {"99999", "", "   "})
  public void testCreateCase_MismatchedOrBlankProviderFirmId_ReturnsForbidden(
      final String bodyProviderFirmId) throws Exception {
    CaseDetail caseDetail = caseForProvider(bodyProviderFirmId);

    mockMvc
        .perform(
            post("/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID)
                .content(objectMapper.writeValueAsString(caseDetail)))
        .andExpect(status().isForbidden());

    verify(caseDetailsService, never()).registerCase(any(), any(), any());
  }

  @Test
  public void testCreateCase_NullApplicationDetails_ReturnsForbidden() throws Exception {
    CaseDetail caseDetail = new CaseDetail();

    mockMvc
        .perform(
            post("/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID)
                .content(objectMapper.writeValueAsString(caseDetail)))
        .andExpect(status().isForbidden());

    verify(caseDetailsService, never()).registerCase(any(), any(), any());
  }

  @Test
  public void testUpdateCase_Success() throws Exception {
    CaseDetail caseDetail = caseForProvider(CAAB_PROVIDER_ID);

    mockMvc
        .perform(
            put("/cases", CASE_REFERENCE_NUMBER)
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID)
                .queryParam("case-update-type", "caseUpdateType")
                .content(objectMapper.writeValueAsString(caseDetail)))
        .andExpect(status().isOk());

    verify(caseDetailsService)
        .amendCase(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, caseDetail, "caseUpdateType");
  }

  @Test
  public void testUpdateCase_MismatchedProviderFirmId_ReturnsForbidden() throws Exception {
    CaseDetail caseDetail = caseForProvider("99999");

    mockMvc
        .perform(
            put("/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE)
                .header(ProviderContext.HEADER_NAME, CAAB_PROVIDER_ID)
                .queryParam("case-update-type", "ProviderDetails")
                .content(objectMapper.writeValueAsString(caseDetail)))
        .andExpect(status().isForbidden());

    verify(caseDetailsService, never()).amendCase(any(), any(), any(), any());
  }

  @Test
  void updateCase_doesNotLogCaseBodyOrPersonalDataInErrors() {
    var logger =
        (ch.qos.logback.classic.Logger)
            org.slf4j.LoggerFactory.getLogger(CaseDetailsController.class);
    var appender =
        new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
    appender.start();
    logger.addAppender(appender);
    ProviderContext.set(CAAB_PROVIDER_ID);
    try {
      CaseDetail caseDetail = caseForProvider(CAAB_PROVIDER_ID);
      caseDetail.setCaseReferenceNumber("Jane Doe 1980-02-03");
      when(caseDetailsService.amendCase(any(), any(), any(), any()))
          .thenThrow(new RuntimeException("rejected AB123456C for jane.doe@example.com"));

      caseDetailsController.updateCase(
          SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, "type", caseDetail);

      String info =
          appender.list.stream()
              .filter(e -> e.getLevel().isGreaterOrEqual(ch.qos.logback.classic.Level.INFO))
              .map(ch.qos.logback.classic.spi.ILoggingEvent::getFormattedMessage)
              .collect(java.util.stream.Collectors.joining("\n"));
      org.assertj.core.api.Assertions.assertThat(info)
          .isNotBlank()
          .doesNotContain("Jane", "1980", "AB123456C", "jane.doe");
      org.assertj.core.api.Assertions.assertThat(appender.list)
          .noneMatch(
              e ->
                  e.getLevel().isGreaterOrEqual(ch.qos.logback.classic.Level.INFO)
                      && e.getThrowableProxy() != null);
    } finally {
      ProviderContext.clear();
      logger.detachAppender(appender);
    }
  }
}
