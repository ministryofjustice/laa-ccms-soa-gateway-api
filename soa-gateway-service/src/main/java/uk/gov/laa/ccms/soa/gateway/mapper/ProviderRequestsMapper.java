package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.laa.ccms.soa.gateway.mapper.context.ProviderRequestMappingContext;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestAttribute;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderRequestElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderRequestTextElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

/**
 * Mapper for converting provider request details to various element types.
 */
@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface ProviderRequestsMapper {

  /**
   * Converts a ProviderRequestMappingContext to a ProviderRequestElementType.
   *
   * @param providerRequestMappingContext the context containing provider request details
   * @return the converted ProviderRequestElementType
   */
  @Mapping(target = "caseReferenceNumber", source = "providerRequestDetail.caseReferenceNumber")
  @Mapping(target = "user", source = "providerRequestMappingContext", qualifiedByName = "toUser")
  @Mapping(target = "requestDetails", source = "providerRequestDetail")
  ProviderRequestElementType toProviderRequestElementType(
      ProviderRequestMappingContext providerRequestMappingContext
  );

  /**
   * Converts a ProviderRequestMappingContext to a User.
   *
   * @param providerRequestMappingContext the context containing provider request details
   * @return the converted User
   */
  @Named("toUser")
  @Mapping(target = "userName", source = "providerRequestDetail.username")
  @Mapping(target = "userLoginID", source = "userLoginId")
  @Mapping(target = "userType", source = "userRole")
  User toUser(ProviderRequestMappingContext providerRequestMappingContext);

  /**
   * Converts a ProviderRequestDetail to a list of RequestDetails.
   *
   * @param providerRequestDetail the provider request detail
   * @return the list of RequestDetails
   */
  default List<ProviderRequestElementType.RequestDetails> toRequestDetailsList(
      final ProviderRequestDetail providerRequestDetail) {
    if (providerRequestDetail == null) {
      return null;
    }
    final List<ProviderRequestElementType.RequestDetails> requestDetailsList = new ArrayList<>();
    requestDetailsList.add(toRequestDetails(providerRequestDetail));
    return requestDetailsList;
  }

  /**
   * Converts a ProviderRequestDetail to a RequestDetails.
   *
   * @param providerRequestDetail the provider request detail
   * @return the converted RequestDetails
   */
  @Mapping(target = "request.requestType",
      source = "requestType")
  @Mapping(target = "request.requestText",
      source = "attributes",
      qualifiedByName = "mapAttributes")
  ProviderRequestElementType.RequestDetails toRequestDetails(
      ProviderRequestDetail providerRequestDetail);

  /**
   * Converts a ProviderRequestAttribute to a ProviderRequestTextElementType.
   *
   * @param attribute the provider request attribute
   * @return the converted ProviderRequestTextElementType
   */
  ProviderRequestTextElementType toProviderRequestTextElementType(
      ProviderRequestAttribute attribute);

  /**
   * Custom method to map a list of ProviderRequestAttribute to a list of
   * ProviderRequestTextElementType.
   *
   * @param attributes the list of provider request attributes
   * @return the list of converted ProviderRequestTextElementType
   */
  @Named("mapAttributes")
  default List<ProviderRequestTextElementType> mapAttributes(
      final List<ProviderRequestAttribute> attributes) {
    if (attributes == null) {
      return null;
    }
    final List<ProviderRequestTextElementType> textElementTypes = new ArrayList<>();
    for (final ProviderRequestAttribute attribute : attributes) {
      textElementTypes.add(toProviderRequestTextElementType(attribute));
    }
    return textElementTypes;
  }
}
