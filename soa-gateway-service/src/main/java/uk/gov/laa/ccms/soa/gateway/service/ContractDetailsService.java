package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ContractDetailsClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ContractDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ContractDetails;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;

@Service
@RequiredArgsConstructor
public class ContractDetailsService {

    private final ContractDetailsClient contractDetailsClient;

    private final ContractDetailsMapper contractDetailsMapper;

    public ContractDetails getContractDetails(
        Integer searchFirmId,
        Integer searchOfficeId,
        String soaGatewayUserLoginId,
        String soaGatewayUserRole,
        Integer maxRecords){
        ContractDetailsInqRS response = contractDetailsClient.getContractDetails(
            searchFirmId.toString(),
            searchOfficeId.toString(),
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            maxRecords);

        return contractDetailsMapper.toContractDetails(response);
    }




}
