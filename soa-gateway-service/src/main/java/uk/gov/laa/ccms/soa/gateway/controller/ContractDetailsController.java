package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.ContractDetailsApi;
import uk.gov.laa.ccms.soa.gateway.model.ContractDetails;
import uk.gov.laa.ccms.soa.gateway.service.ContractDetailsService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ContractDetailsController implements ContractDetailsApi {

    private final ContractDetailsService contractDetailsService;

    @Override
    public ResponseEntity<ContractDetails> getContractDetails(
        Integer providerFirmId,
        Integer officeId,
        String soaGatewayUserLoginId,
        String soaGatewayUserRole,
        Integer maxRecords) {

        try{
            ContractDetails contractDetails = contractDetailsService.getContractDetails(
                providerFirmId,
                officeId,
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords);
            return ResponseEntity.ok(contractDetails);
        } catch(Exception e){
            log.error("ContractDetailsController caught exception" , e);
            return ResponseEntity.internalServerError().build();
        }

    }
}
