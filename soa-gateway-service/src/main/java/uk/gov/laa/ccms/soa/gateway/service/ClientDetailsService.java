package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ClientServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ClientDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.util.PaginationUtil;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;

import java.util.ArrayList;
import java.util.Collections;
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
            ClientDetail clientDetail,
            Pageable pageable
    ) {
        ClientInfo clientInfo =  clientDetailsMapper.toClientInfo(clientDetail);

        ClientInqRS response = clientServicesClient.getClientDetails(
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                maxRecords,
                clientInfo);

        List<ClientDetail> clientDetailList = clientDetailsMapper.toClientDetailList(response);

        Page<ClientDetail> page = PaginationUtil.paginateList(pageable, clientDetailList);

        return clientDetailsMapper.toClientDetails(page);

    }

}
