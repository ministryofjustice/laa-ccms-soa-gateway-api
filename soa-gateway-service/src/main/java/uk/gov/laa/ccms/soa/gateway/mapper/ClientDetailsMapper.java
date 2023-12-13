package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.mapstruct.BeforeMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetailDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.laa.ccms.soa.gateway.model.ClientPersonalDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientSummary;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.laa.ccms.soa.gateway.util.DateUtil;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientAddUpdtStatusRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientUpdateRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.PersonalDetails;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

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
  AddressDetail toAddressDetail(Address address);

  uk.gov.laa.ccms.soa.gateway.model.RecordHistory toClientDetailRecordHistory(
      RecordHistory recordHistory);

  // Now, the modified toClientDetail method will use the above method to map the
  // 'personalInformation' fields
  @Mapping(target = ".", source = "client")
  @Mapping(target = "details", source = "client.details")
  @Mapping(target = "recordHistory", source = "client.recordHistory")
  ClientDetail toClientDetail(ClientInqRS clientInqRs);

  @InheritInverseConfiguration
  Address toAddress(AddressDetail address);

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

  @Mapping(target = "clientSubmissionStatus",
      source = "headerRS.status.status")
  uk.gov.laa.ccms.soa.gateway.model.ClientStatus toClientStatus(ClientAddUpdtStatusRS response);

  @Named("dateToXmlGregorianCalendarWithoutTimeZone")
  default XMLGregorianCalendar dateToXmlGregorianCalendarWithoutTimeZone(Date date)
      throws DatatypeConfigurationException {
    return DateUtil.convertDateToXmlDateOnly(date);
  }

  @Mapping(target = "headerRQ", ignore = true)
  @Mapping(target = "recordHistory", ignore = true)
  @Mapping(target = "dateOfBirth", ignore = true)
  @Mapping(target = "dateOfDeath", ignore = true)
  @Mapping(target = "gender", ignore = true)
  @Mapping(target = "maritalStatus", ignore = true)
  @Mapping(target = "NINumber", ignore = true)
  @Mapping(target = "homeOfficeReference", ignore = true)
  @Mapping(target = "vulnerableClient", ignore = true)
  @Mapping(target = "highProfileClient", ignore = true)
  @Mapping(target = "vexatiousLitigant", ignore = true)
  @Mapping(target = "mentalCapacityInd", ignore = true)
  @Mapping(target = "countryOfOrigin", ignore = true)
  @Mapping(target = ".", source = "details.contacts")
  ClientUpdateRQ toClientUpdateRq(
      String clientReferenceNumber,
      ClientDetailDetails details);

  /**
   * Amends the ClientUpdateRq before the mapping takes place, passing through the personal details
   * to map.
   *
   * @param clientUpdateRq The update object to be sent to soa.
   * @param clientDetailDetails The returned soa client details to map from.
   */
  @BeforeMapping
  default void beforeMappingToClientUpdateRq(
      @MappingTarget ClientUpdateRQ clientUpdateRq,
      ClientDetailDetails clientDetailDetails) {
    if (clientDetailDetails.getPersonalInformation() != null) {
      addClientPersonalDetailToClientUpdateRq(clientUpdateRq,
          clientDetailDetails.getPersonalInformation());
    }
  }

  @Mapping(target = "dateOfBirth",
        source = "dateOfBirth",
      qualifiedByName = "dateToXmlGregorianCalendarWithoutTimeZone")
  @Mapping(target = "dateOfDeath",
      source = "dateOfDeath",
      qualifiedByName = "dateToXmlGregorianCalendarWithoutTimeZone")
  @Mapping(target = "NINumber",
      source = "nationalInsuranceNumber")
  @Mapping(target = "homeOfficeReference",
      source = "homeOfficeNumber")
  @Mapping(target = "headerRQ", ignore = true)
  @Mapping(target = "clientReferenceNumber", ignore = true)
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "address", ignore = true)
  @Mapping(target = "noFixedAbode", ignore = true)
  @Mapping(target = "ethnicMonitoring", ignore = true)
  @Mapping(target = "disabilityMonitoring", ignore = true)
  @Mapping(target = "specialConsiderations", ignore = true)
  @Mapping(target = "telephoneHome", ignore = true)
  @Mapping(target = "telephoneWork", ignore = true)
  @Mapping(target = "mobileNumber", ignore = true)
  @Mapping(target = "emailAddress", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "passwordReminder", ignore = true)
  @Mapping(target = "correspondenceMethod", ignore = true)
  @Mapping(target = "correspondenceLanguage", ignore = true)
  @Mapping(target = "recordHistory", ignore = true)
  void addClientPersonalDetailToClientUpdateRq(
      @MappingTarget ClientUpdateRQ clientUpdateRq, ClientPersonalDetail personalInformation);

  @Mapping(target = "userLoginId", source = "userLoginID")
  UserDetail toUserDetail(User user);




}
