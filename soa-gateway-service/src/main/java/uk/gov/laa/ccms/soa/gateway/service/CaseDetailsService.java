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
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
import uk.gov.laa.ccms.soa.gateway.model.CaseSummary;
import uk.gov.laa.ccms.soa.gateway.util.PaginationUtil;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseInfo;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseDetailsService {

    private final CaseServicesClient caseServicesClient;

    private final CaseDetailsMapper caseDetailsMapper;


    public CaseDetails getCaseDetails(
        final String soaGatewayUserLoginId,
        final String soaGatewayUserRole,
        final String caseReferenceNumber,
        final  String providerCaseRefNumber,
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

    private CaseInfo buildCaseInfo(
        String caseReferenceNumber,
        String providerCaseRefNumber,
        String caseStatus,
        String clientSurname,
        Integer feeEarnerId,
        Integer officeId) {
        CaseInfo caseInfo =  new CaseInfo();
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
