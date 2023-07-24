package uk.gov.laa.ccms.soa.gateway.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.ClientsApi;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.service.ClientDetailsService;
import uk.gov.laa.ccms.soa.gateway.util.DateUtil;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientDetailsController implements ClientsApi{

    private final ClientDetailsService clientDetailsService;
    @Override
    public ResponseEntity<ClientDetails> getClients(String soaGatewayUserLoginId,
                                                    String soaGatewayUserRole,
                                                    String firstName,
                                                    String surname,
                                                    Date dateOfBirth,
                                                    String gender,
                                                    String caseReferenceNumber,
                                                    String homeOfficeReference,
                                                    String nationalInsuranceNumber,
                                                    Integer maxRecords,
                                                    Pageable pageable) {
        try{
            ClientDetail clientDetail = new ClientDetail()
                    .firstName(firstName)
                    .surname(surname)
                    .dateOfBirth(dateOfBirth)
                    .gender(gender)
                    .caseReferenceNumber(caseReferenceNumber)
                    .homeOfficeReference(homeOfficeReference)
                    .nationalInsuranceNumber(nationalInsuranceNumber);

            log.info("clientDetail: " + clientDetail.toString());

            ClientDetails clientDetails = clientDetailsService.getClientDetails(
                    soaGatewayUserLoginId,
                    soaGatewayUserRole,
                    maxRecords,
                    clientDetail,
                    pageable);

            return ResponseEntity.ok(clientDetails);
        } catch(Exception e){
            log.error("ClientDetailsController caught exception" , e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
