package uk.gov.laa.ccms.soa.gateway.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.CaseServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.CaseDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
import uk.gov.laa.ccms.soa.gateway.model.CaseSummary;
import uk.gov.laa.ccms.soa.gateway.util.PaginationUtil;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseInfo;

/**
 * Service class responsible for fetching and processing case details.
 *
 * <p>This service interacts with the external Case Services system to fetch case details. It then
 * processes and converts these details to the desired format using the {@link CaseDetailsMapper}.
 * Pagination of results is handled by the {@link PaginationUtil} utility.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseDetailsService {

  private final CaseServicesClient caseServicesClient;

  private final CaseDetailsMapper caseDetailsMapper;

  /**
   * Retrieves case details based on the provided search criteria.
   *
   * <p>This method communicates with the external Case Services system using the provided
   * search criteria, fetches the relevant case details, and then maps and paginates these details
   * to the desired format.</p>
   *
   * @param soaGatewayUserLoginId The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole    The user role in the SOA Gateway.
   * @param caseReferenceNumber   The reference number for the case.
   * @param providerCaseRefNumber The provider's reference number for the case.
   * @param caseStatus            The status of the case.
   * @param clientSurname         The surname of the client associated with the case.
   * @param feeEarnerId           The ID of the fee earner associated with the case.
   * @param officeId              The ID of the office handling the case.
   * @param maxRecords            The maximum number of records to retrieve.
   * @param pageable              The pagination details.
   * @return A {@link CaseDetails} object containing the retrieved and processed case details.
   */
  public CaseDetails getCaseDetails(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String caseReferenceNumber,
      final String providerCaseRefNumber,
      final String caseStatus,
      final String clientSurname,
      final Integer feeEarnerId,
      final Integer officeId,
      final Integer maxRecords,
      final Pageable pageable
  ) {
    log.info("CaseDetailsService - getCaseDetails");
    CaseInfo caseInfo = buildCaseInfo(
        caseReferenceNumber,
        providerCaseRefNumber,
        caseStatus,
        clientSurname,
        feeEarnerId,
        officeId);

    CaseInqRS response = caseServicesClient.getCaseDetails(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        maxRecords,
        caseInfo);

    List<CaseSummary> caseSummaryList = caseDetailsMapper.toCaseSummaryList(response);

    Page<CaseSummary> page = PaginationUtil.paginateList(pageable, caseSummaryList);

    return caseDetailsMapper.toCaseDetails(page);
  }

  /**
   * Retrieve the full details of a single Case based on the supplied caseReferenceNumber.
   *
   * <p>This method communicates with the external Case Services system using the provided
   * case reference, fetches the relevant case details, and then maps these details
   * to the desired format.</p>
   *
   * @param soaGatewayUserLoginId The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole    The user role in the SOA Gateway.
   * @param caseReferenceNumber   The reference number for the case.
   * @return A {@link CaseDetail} object containing the retrieved and processed case detail.
   */
  public CaseDetail getCaseDetail(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String caseReferenceNumber
  ) {
    log.info("CaseDetailsService - getCaseDetail");

    CaseInqRS response = caseServicesClient.getCaseDetail(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        caseReferenceNumber);

    return caseDetailsMapper.toCaseDetail(response.getCase());
  }

  /**
   * Fetches the status of a case transaction using its transaction ID. The method communicates
   * with the external Case Services system and retrieves the current status of the specified
   * transaction.
   *
   * @param soaGatewayUserLoginId      User login ID for the SOA Gateway.
   * @param soaGatewayUserRole         User role in the SOA Gateway.
   * @param transactionId              The transaction ID for which the status is to be fetched.
   * @return                           The status of the specified case transaction.
   */
  public uk.gov.laa.ccms.soa.gateway.model.TransactionStatus getCaseTransactionStatus(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String transactionId
  ) {
    log.info("CaseDetailsService - getCaseTransactionStatus");
    CaseAddUpdtStatusRS response = caseServicesClient.getCaseTransactionStatus(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        transactionId);

    return caseDetailsMapper.toTransactionStatus(response);
  }

  private CaseInfo buildCaseInfo(
      String caseReferenceNumber,
      String providerCaseRefNumber,
      String caseStatus,
      String clientSurname,
      Integer feeEarnerId,
      Integer officeId) {
    CaseInfo caseInfo = new CaseInfo();
    caseInfo.setCaseReferenceNumber(caseReferenceNumber);
    caseInfo.setProviderCaseReferenceNumber(providerCaseRefNumber);
    caseInfo.setCaseStatus(caseStatus);
    caseInfo.setClientSurname(clientSurname);
    caseInfo.setFeeEarnerContactID(
        Optional.ofNullable(feeEarnerId).map(String::valueOf).orElse(null));
    caseInfo.setOfficeID(
        Optional.ofNullable(officeId).map(String::valueOf).orElse(null));
    return caseInfo;
  }

}
