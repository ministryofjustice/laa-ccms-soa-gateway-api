package uk.gov.laa.ccms.soa.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;

@Mapper(componentModel = "spring")
public interface ClientDetailsMapper {

    @Mapping(target = "clients", source = "clientList.client")
    ClientDetails toClientDetails(ClientInqRS clientInqRS);

    @Mapping(target = ".", source = "name")
    @Mapping(target = "nationalInsuranceNumber", source = "NINumber")
    ClientDetail toClientDetail(ClientList clientList);

    @Mapping(target = ".", source = "clientDetail")
    @Mapping(target = "NINumber", source = "clientDetail.nationalInsuranceNumber")
    @Mapping(target = "caseReferenceNumber", source = "clientDetail.clientReferenceNumber")
    ClientInfo toClientInfo(ClientDetail clientDetail);

}
