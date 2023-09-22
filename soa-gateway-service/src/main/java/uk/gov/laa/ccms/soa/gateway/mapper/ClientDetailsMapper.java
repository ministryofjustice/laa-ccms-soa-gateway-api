package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailRecordHistory;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientPersonalDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.util.DateUtil;
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

  @Mapping(target = ".", source = "client")
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

  @Mapping(target = "NINumber", source = "nationalInsuranceNumber")
  @Mapping(target = "caseReferenceNumber", source = "clientReferenceNumber")
  ClientInfo toClientInfo(ClientSummary clientSummary);

  // Inside the toClientPersonalDetail method
  @Mapping(target = "nationalInsuranceNumber", source = "NINumber")
  ClientPersonalDetail toClientPersonalDetail(PersonalDetails personalInformation);

  @Mapping(target = "contacts.fax", ignore = true)
  ClientDetailDetails toClientDetailDetails(
      uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails
          clientDetails);

  @Mapping(target = "addressId", source = "addressID")
  @Mapping(target = "careOfName", source = "coffName")
  AddressDetail toAddressDetail(
      uk.gov.legalservices.enterprise.common._1_0.common.Address address);

  @Mapping(target = "createdBy.userLoginId", source = "createdBy.userLoginID")
  @Mapping(target = "lastUpdatedBy.userLoginId", source = "lastUpdatedBy.userLoginID")
  ClientDetailRecordHistory toClientDetailRecordHistory(RecordHistory recordHistory);

  // Now, the modified toClientDetail method will use the above method to map the
  // 'personalInformation' fields
  @Mapping(target = ".", source = "client")
  @Mapping(target = "details", source = "client.details")
  ClientDetail toClientDetail(ClientInqRS clientInqRs);

  @Mapping(target = "addressID", source = "addressId")
  @Mapping(target = "coffName", source = "careOfName")
  uk.gov.legalservices.enterprise.common._1_0.common.Address toAddress(AddressDetail address);


  uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientDetails
      toSoaClientDetails(ClientDetailDetails clientDetailDetails);

  @Mapping(target = "NINumber",
      source = "nationalInsuranceNumber")
  @Mapping(target = "dateOfBirth",
      source = "dateOfBirth",
      qualifiedByName = "dateToXmlGregorianCalendarWithoutTimeZone")
  @Mapping(target = "dateOfDeath",
      source = "dateOfDeath",
      qualifiedByName = "dateToXmlGregorianCalendarWithoutTimeZone")
  PersonalDetails toPersonalDetail(ClientPersonalDetail personalInformation);

  @Named("dateToXmlGregorianCalendarWithoutTimeZone")
  default XMLGregorianCalendar dateToXmlGregorianCalendarWithoutTimeZone(Date date)
      throws DatatypeConfigurationException {
    return DateUtil.convertDateToXmlDateOnly(date);
  }
}
