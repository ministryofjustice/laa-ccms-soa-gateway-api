package uk.gov.laa.ccms.soa.gateway.service;

import jakarta.xml.bind.JAXBContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ClientServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ClientDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.util.PaginationUtil;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;

import java.util.List;

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
            ClientSummary clientSummary,
            Pageable pageable
    ) {
        log.info("ClientDetailsService - getClientDetails");
        ClientInfo clientInfo =  clientDetailsMapper.toClientInfo(clientSummary);

        ClientInqRS response = clientServicesClient.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientInfo);



        List<ClientSummary> clientDetailList = clientDetailsMapper.toClientSummaryList(response);

        Page<ClientSummary> page = PaginationUtil.paginateList(pageable, clientDetailList);

        return clientDetailsMapper.toClientDetails(page);
    }

    public ClientDetail getClientDetail(
            String soaGatewayUserLoginId,
            String soaGatewayUserRole,
            Integer maxRecords,
            String clientReferenceNumber
    ) {
        log.info("ClientDetailsService - getClientDetail");
        ClientInqRS response = clientServicesClient.getClientDetail(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientReferenceNumber);

        ClientDetail clientDetail = clientDetailsMapper.toClientDetail(response);
        log.debug("clientDetail, received: {}", clientDetail);

        return clientDetail;
    }

}
