package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.ClientsApi;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.service.ClientDetailsService;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientDetailsController implements ClientsApi{

    private final ClientDetailsService clientDetailsService;


    @Override
    public ResponseEntity<ClientDetail> getClient(String clientReferenceNumber,
                                                  String soaGatewayUserLoginId,
                                                  String soaGatewayUserRole,
                                                  Integer maxRecords) {
        log.info("GET /clients/{}", clientReferenceNumber);
        try{

            ClientDetail clientDetail = clientDetailsService.getClientDetail(
                    soaGatewayUserLoginId,
                    soaGatewayUserRole,
                    maxRecords,
                    clientReferenceNumber);

            return ResponseEntity.ok(clientDetail);
        } catch(Exception e){
            log.error("ClientDetailsController caught exception" , e);
            return ResponseEntity.internalServerError().build();
        }
    }

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
        log.info("GET /clients");
        try{
            // Build a ClientSummary to hold the search criteria.
            // Note: caseReferenceNumber can actually hold a case or client reference.
            ClientSummary clientSummary= new ClientSummary()
                    .firstName(firstName)
                    .surname(surname)
                    .dateOfBirth(dateOfBirth)
                    .gender(gender)
                    .clientReferenceNumber(caseReferenceNumber)
                    .homeOfficeReference(homeOfficeReference)
                    .nationalInsuranceNumber(nationalInsuranceNumber);

            log.info("clientSummary: " + clientSummary.toString());

            ClientDetails clientDetails = clientDetailsService.getClientDetails(
                    soaGatewayUserLoginId,
                    soaGatewayUserRole,
                    maxRecords,
                    clientSummary,
                    pageable);

            return ResponseEntity.ok(clientDetails);
        } catch(Exception e){
            log.error("ClientDetailsController caught exception" , e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
