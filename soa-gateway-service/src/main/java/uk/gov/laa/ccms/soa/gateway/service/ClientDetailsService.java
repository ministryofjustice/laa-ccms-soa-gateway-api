package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ClientServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ClientDetailsMapper;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientDetailsService {

    private final ClientServicesClient clientServicesClient;

    private final ClientDetailsMapper clientDetailsMapper;

    public ClientDetails getClientDetails(
            String soaGatewayUserLoginId,
            String soaGatewayUserRole,
            Integer maxRecords,
            ClientDetail clientDetail
    ) {
        ClientInfo clientInfo =  clientDetailsMapper.toClientInfo(clientDetail);

        ClientInqRS response = clientServicesClient.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientInfo);

        return clientDetailsMapper.toClientDetails(response);
    }

}
