package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ws.client.WebServiceIOException;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
import uk.gov.laa.ccms.soa.gateway.service.CaseDetailsService;

@ExtendWith(MockitoExtension.class)
public class CaseDetailsControllerTest {

  @Mock
  private CaseDetailsService caseDetailsService;

  @InjectMocks
  private CaseDetailsController caseDetailsController;

  private MockMvc mockMvc;

  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";
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
    this.mockMvc = MockMvcBuilders.standaloneSetup(caseDetailsController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }

  @Test
  public void testGetCases_Success() throws Exception {
    // Create a mock response
    CaseDetails caseDetails = new CaseDetails();

    // Stub the clientDetailsService to return the mock response
    when(caseDetailsService.getCaseDetails(
        SOA_GATEWAY_USER_LOGIN_ID,
        SOA_GATEWAY_USER_ROLE,
        CASE_REFERENCE_NUMBER,
        PROVIDER_CASE_REFERENCE,
        CASE_STATUS,
        CLIENT_SURNAME,
        FEE_EARNER_ID,
        OFFICE_ID,
        MAX_RECORDS,
        PAGEABLE))
        .thenReturn(caseDetails);

    mockMvc.perform(
            get("/cases?case-reference-number={caseReferenceNumber}" +
                    "&provider-case-reference={providerCaseRef}" +
                    "&case-status={caseStatus}" +
                    "&client-surname={surname}" +
                    "&fee-earner-id={feeEarnerId}" +
                    "&office-id={officeId}" +
                    "&max-records={maxRecords}",
                CASE_REFERENCE_NUMBER,
                PROVIDER_CASE_REFERENCE,
                CASE_STATUS,
                CLIENT_SURNAME,
                FEE_EARNER_ID,
                OFFICE_ID,
                MAX_RECORDS)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isOk());

    verify(caseDetailsService).getCaseDetails(
        SOA_GATEWAY_USER_LOGIN_ID,
        SOA_GATEWAY_USER_ROLE,
        CASE_REFERENCE_NUMBER,
        PROVIDER_CASE_REFERENCE,
        CASE_STATUS,
        CLIENT_SURNAME,
        FEE_EARNER_ID,
        OFFICE_ID,
        MAX_RECORDS,
        PAGEABLE);
  }

  @Test
  public void testGetCases_Exception() throws Exception {

    // Stub the mock response
    when(caseDetailsService.getCaseDetails(
        SOA_GATEWAY_USER_LOGIN_ID,
        SOA_GATEWAY_USER_ROLE,
        CASE_REFERENCE_NUMBER,
        PROVIDER_CASE_REFERENCE,
        CASE_STATUS,
        CLIENT_SURNAME,
        FEE_EARNER_ID,
        OFFICE_ID,
        MAX_RECORDS,
        PAGEABLE))
        .thenThrow(new WebServiceIOException("Test exception"));

    mockMvc.perform(
            get("/cases?case-reference-number={caseReferenceNumber}" +
                    "&provider-case-reference={providerCaseRef}" +
                    "&case-status={caseStatus}" +
                    "&client-surname={surname}" +
                    "&fee-earner-id={feeEarnerId}" +
                    "&office-id={officeId}" +
                    "&max-records={maxRecords}",
                CASE_REFERENCE_NUMBER,
                PROVIDER_CASE_REFERENCE,
                CASE_STATUS,
                CLIENT_SURNAME,
                FEE_EARNER_ID,
                OFFICE_ID,
                MAX_RECORDS)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isInternalServerError());

    verify(caseDetailsService).getCaseDetails(
        SOA_GATEWAY_USER_LOGIN_ID,
        SOA_GATEWAY_USER_ROLE,
        CASE_REFERENCE_NUMBER,
        PROVIDER_CASE_REFERENCE,
        CASE_STATUS,
        CLIENT_SURNAME,
        FEE_EARNER_ID,
        OFFICE_ID,
        MAX_RECORDS,
        PAGEABLE);
  }
}
