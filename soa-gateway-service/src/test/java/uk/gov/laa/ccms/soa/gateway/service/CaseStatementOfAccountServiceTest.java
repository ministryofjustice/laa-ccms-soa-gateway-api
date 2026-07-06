package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.CaseStatementOfAccountClient;
import uk.gov.laa.ccms.soa.gateway.mapper.CaseStatementOfAccountMapper;
import uk.gov.laa.ccms.soa.gateway.model.CaseStatementOfAccount;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.CaseSOAInqRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.SOAElementType;

@ExtendWith(MockitoExtension.class)
class CaseStatementOfAccountServiceTest {

  private static final String USER_LOGIN_ID = "user";
  private static final String USER_ROLE = "EXTERNAL";
  private static final String CASE_REFERENCE_NUMBER = "300000123456";

  @Mock private CaseStatementOfAccountClient caseStatementOfAccountClient;

  @Mock private CaseStatementOfAccountMapper caseStatementOfAccountMapper;

  @InjectMocks private CaseStatementOfAccountService caseStatementOfAccountService;

  @Test
  public void getCaseStatementOfAccountMapsClientResponse() {
    SOAElementType soaElement = new SOAElementType();
    CaseSOAInqRS response = new CaseSOAInqRS();
    response.setStatementOfAccount(soaElement);
    CaseStatementOfAccount expected = new CaseStatementOfAccount();

    when(caseStatementOfAccountClient.getCaseStatementOfAccount(
            USER_LOGIN_ID, USER_ROLE, CASE_REFERENCE_NUMBER))
        .thenReturn(response);
    when(caseStatementOfAccountMapper.toCaseStatementOfAccount(soaElement)).thenReturn(expected);

    CaseStatementOfAccount result =
        caseStatementOfAccountService.getCaseStatementOfAccount(
            USER_LOGIN_ID, USER_ROLE, CASE_REFERENCE_NUMBER);

    assertSame(expected, result);
    verify(caseStatementOfAccountClient)
        .getCaseStatementOfAccount(USER_LOGIN_ID, USER_ROLE, CASE_REFERENCE_NUMBER);
    verify(caseStatementOfAccountMapper).toCaseStatementOfAccount(soaElement);
  }
}
