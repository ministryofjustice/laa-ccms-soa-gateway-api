package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.CaseStatementOfAccountClient;
import uk.gov.laa.ccms.soa.gateway.mapper.CaseStatementOfAccountMapper;
import uk.gov.laa.ccms.soa.gateway.model.CaseStatementOfAccount;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.CaseSOAInqRS;

/**
 * Service class responsible for fetching and processing a case statement of account.
 *
 * <p>This service interacts with the external EBS {@code GetCaseStmtOfAccount} SOAP service to fetch
 * the statement of account, then maps the response to the {@link CaseStatementOfAccount} model using
 * the {@link CaseStatementOfAccountMapper}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseStatementOfAccountService extends AbstractSoaService {

  private final CaseStatementOfAccountClient caseStatementOfAccountClient;

  private final CaseStatementOfAccountMapper caseStatementOfAccountMapper;

  /**
   * Retrieve the statement of account for the supplied caseReferenceNumber.
   *
   * @param soaGatewayUserLoginId The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole The user role in the SOA Gateway.
   * @param caseReferenceNumber The reference number for the case.
   * @return A {@link CaseStatementOfAccount} object containing the retrieved statement of account.
   */
  public CaseStatementOfAccount getCaseStatementOfAccount(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String caseReferenceNumber) {
    log.info("CaseStatementOfAccountService - getCaseStatementOfAccount");

    CaseSOAInqRS response =
        caseStatementOfAccountClient.getCaseStatementOfAccount(
            soaGatewayUserLoginId, soaGatewayUserRole, caseReferenceNumber);

    return caseStatementOfAccountMapper.toCaseStatementOfAccount(response.getStatementOfAccount());
  }
}
