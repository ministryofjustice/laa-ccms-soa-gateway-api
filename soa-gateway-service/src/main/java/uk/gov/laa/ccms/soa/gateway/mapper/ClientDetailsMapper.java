package uk.gov.laa.ccms.soa.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientDetailsMapper {

    @Mapping(target = ".", source = "clientList.client")
    List<ClientDetail> toClientDetailList(List<ClientList> clientList);

    ClientDetails toClientDetails(Page<ClientDetail> page);

    default List<ClientDetail> toClientDetailList(ClientInqRS clientInqRS) {
        if (clientInqRS.getClientList() != null){
            ClientInqRS.ClientList clientListObject = clientInqRS.getClientList();

            if (clientListObject.getClient() != null){
                List<ClientList> clientList = clientListObject.getClient();

                if (clientList != null) {
                    return toClientDetailList(clientList);
                }
            }
        }
        return Collections.emptyList();
    }

    @Mapping(target = ".", source = "name")
    @Mapping(target = "nationalInsuranceNumber", source = "NINumber")
    @Mapping(target = "caseReferenceNumber", source = "clientReferenceNumber")
    ClientDetail toClientDetail(ClientList clientList);

    @Mapping(target = ".", source = "clientDetail")
    @Mapping(target = "NINumber", source = "clientDetail.nationalInsuranceNumber")
    ClientInfo toClientInfo(ClientDetail clientDetail);



}
