package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.CaseServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.CaseDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.util.PaginationUtil;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseUpdateRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseUpdateRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseAdd;

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
public class CaseDetailsService extends AbstractSoaService {

  private final CaseServicesClient caseServicesClient;

  private final CaseDetailsMapper caseDetailsMapper;

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
   * Registers a new case based on the provided case details.
   *
   * <p>This method communicates with the external Case Services system using the provided
   * case details, creates a new case in CCMS, and then extracts the returned transaction id
   * for return.</p>
   *
   * @param soaGatewayUserLoginId The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole    The user role in the SOA Gateway.
   * @param caseDetail            The case details to register.
   * @return A {@link String} representing the SOA transaction id for the case registration.
   */
  public String registerCase(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final CaseDetail caseDetail
  ) {
    log.info("CaseDetailsService - registerCase");
    final CaseAdd caseAdd = caseDetailsMapper.toCaseAdd(caseDetail);

    final CaseAddRS response = caseServicesClient.createCaseApplication(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        caseAdd);

    return response.getTransactionID();
  }

  /**
   * Amends an existing case based on the provided case details.
   *
   * <p>This method communicates with the external Case Services system using the provided
   * case details, amends an existing case in CCMS, and then extracts the returned transaction id
   * for return.</p>
   *
   * @param soaGatewayUserLoginId The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole    The user role in the SOA Gateway.
   * @param caseDetail            The case details to amend.
   * @return A {@link String} representing the SOA transaction id for the case amendment.
   */
  public String amendCase(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final CaseDetail caseDetail,
      final String caseUpdateType
  ) {
    log.info("CaseDetailsService - amendCase");

    CaseUpdateRQ caseUpdateRq = caseDetailsMapper.toCaseUpdateRq(caseDetail, caseUpdateType);

    final CaseUpdateRS response = caseServicesClient.updateCase(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        caseUpdateRq);

    return response.getTransactionID();
  }

}
