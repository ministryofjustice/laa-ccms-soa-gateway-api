package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ReferenceDataClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ReferenceDataMapper;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;

@Service
@RequiredArgsConstructor
public class ReferenceDataService {

    private final ReferenceDataClient referenceDataClient;

    private final ReferenceDataMapper referenceDataMapper;

    public CaseReferenceSummary getCaseReference(final String soaGatewayUserLoginId,
                                                 final String soaGatewayUserRole){

        ReferenceDataInqRS response = referenceDataClient.getCaseReference(
                soaGatewayUserLoginId,
                soaGatewayUserRole);

        return referenceDataMapper.toCaseReferenceSummary(response);
    }


}
