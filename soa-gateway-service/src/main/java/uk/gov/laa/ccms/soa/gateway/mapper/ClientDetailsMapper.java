package uk.gov.laa.ccms.soa.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientAddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import  uk.gov.laa.ccms.soa.gateway.model.ClientDetailRecordHistory;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.PersonalDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientPersonalDetail;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientDetailsMapper {

    @Mapping(target = ".", source = "clientList.client")
    List<ClientSummary> toClientSummaryList(List<ClientList> clientList);

    ClientDetails toClientDetails(Page<ClientSummary> page);

    default List<ClientSummary> toClientSummaryList(ClientInqRS clientInqRS) {
        if (clientInqRS.getClientList() != null){
            ClientInqRS.ClientList clientListObject = clientInqRS.getClientList();

            if (clientListObject.getClient() != null){
                List<ClientList> clientList = clientListObject.getClient();

                if (clientList != null) {
                    return toClientSummaryList(clientList);
                }
            }
        }
        return Collections.emptyList();
    }

    @Mapping(target = ".", source = "name")
    @Mapping(target = "nationalInsuranceNumber", source = "NINumber")
    @Mapping(target = "caseReferenceNumber", source = "clientReferenceNumber")
    ClientSummary toClientSummary(ClientList clientList);

    @Mapping(target = ".", source = "clientSummary")
    @Mapping(target = "NINumber", source = "clientSummary.nationalInsuranceNumber")
    ClientInfo toClientInfo(ClientSummary clientSummary);

    // Inside the toClientPersonalDetail method
    @Mapping(target = "nationalInsuranceNumber", source = "personalInformation.NINumber")
    ClientPersonalDetail toClientPersonalDetail(PersonalDetails personalInformation);

    @Mapping(target = "address", source = "clientDetails.address")
    ClientDetailDetails toClientDetailDetails(uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails clientDetails);

    @Mapping(target = "addressId", source = "address.addressID")
    ClientAddressDetail toClientDetailDetails(uk.gov.legalservices.enterprise.common._1_0.common.Address address);

    @Mapping(target = "createdBy.userLoginId", source = "recordHistory.createdBy.userLoginID")
    @Mapping(target = "lastUpdatedBy.userLoginId", source = "recordHistory.lastUpdatedBy.userLoginID")
    ClientDetailRecordHistory toClientDetailRecordHistory(RecordHistory recordHistory);

    // Now, the modified toClientDetail method will use the above method to map the 'personalInformation' fields
    @Mapping(target = ".", source = "clientInqRS.client")
    @Mapping(target = "details", source = "clientInqRS.client.details")
    ClientDetail toClientDetail(ClientInqRS clientInqRS);


}
