package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailRecordHistory;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientPersonalDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.PersonalDetails;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory;

/**
 * Mapper interface responsible for transforming client data between different representations.
 */
@Mapper(componentModel = "spring")
public interface ClientDetailsMapper {

  @Mapping(target = ".", source = "clientList.client")
  List<ClientSummary> toClientSummaryList(List<ClientList> clientList);

  /**
   * Converts the {@link ClientInqRS} object to a list of {@link ClientSummary} objects.
   *
   * <p>If the provided ClientInqRS object contains valid client data, it extracts and converts
   * the list of client details. Otherwise, it returns an empty list.</p>
   *
   * @param clientInqRs The client inquiry response to be converted.
   * @return List of {@link ClientSummary} objects or an empty list if no client data is available.
   */
  default List<ClientSummary> toClientSummaryList(ClientInqRS clientInqRs) {
    if (clientInqRs.getClientList() != null) {
      ClientInqRS.ClientList clientListObject = clientInqRs.getClientList();

      if (clientListObject.getClient() != null) {
        List<ClientList> clientList = clientListObject.getClient();

        if (clientList != null) {
          return toClientSummaryList(clientList);
        }
      }
    }
    return Collections.emptyList();
  }

  ClientDetails toClientDetails(Page<ClientSummary> page);

  @Mapping(target = ".", source = "name")
  @Mapping(target = "nationalInsuranceNumber", source = "NINumber")
  ClientSummary toClientSummary(ClientList clientList);

  @Mapping(target = ".", source = "clientSummary")
  @Mapping(target = "NINumber", source = "clientSummary.nationalInsuranceNumber")
  @Mapping(target = "caseReferenceNumber", source = "clientReferenceNumber")
  ClientInfo toClientInfo(ClientSummary clientSummary);

  // Inside the toClientPersonalDetail method
  @Mapping(target = "nationalInsuranceNumber", source = "personalInformation.NINumber")
  ClientPersonalDetail toClientPersonalDetail(PersonalDetails personalInformation);

  @Mapping(target = "address", source = "clientDetails.address")
  ClientDetailDetails toClientDetailDetails(
          uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails
                  clientDetails);

  @Mapping(target = "addressId", source = "address.addressID")
  AddressDetail toAddressDetail(
          uk.gov.legalservices.enterprise.common._1_0.common.Address address);

  @Mapping(target = "createdBy.userLoginId", source = "recordHistory.createdBy.userLoginID")
  @Mapping(target = "lastUpdatedBy.userLoginId", source = "recordHistory.lastUpdatedBy.userLoginID")
  ClientDetailRecordHistory toClientDetailRecordHistory(RecordHistory recordHistory);

  // Now, the modified toClientDetail method will use the above method to map the
  // 'personalInformation' fields
  @Mapping(target = ".", source = "clientInqRs.client")
  @Mapping(target = "details", source = "clientInqRs.client.details")
  ClientDetail toClientDetail(ClientInqRS clientInqRs);


}
