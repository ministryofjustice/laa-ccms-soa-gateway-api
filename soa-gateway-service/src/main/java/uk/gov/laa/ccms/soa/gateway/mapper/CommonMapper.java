package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentResult;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.laa.ccms.soa.gateway.model.OpaInstance;
import uk.gov.laa.ccms.soa.gateway.model.RecordHistory;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

/** Mapper interface for converting common data between different representations. */
@Mapper(componentModel = "spring")
public interface CommonMapper {

  @Mapping(target = "addressId", source = "addressID")
  @Mapping(target = "careOfName", source = "coffName")
  AddressDetail toAddressDetail(Address address);

  @InheritInverseConfiguration
  Address toAddress(AddressDetail address);

  @Mapping(target = "lastUpdatedBy.userLoginId", source = "lastUpdatedBy.userLoginID")
  @Mapping(target = "createdBy.userLoginId", source = "createdBy.userLoginID")
  RecordHistory toRecordHistory(
      uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory soaRecordHistory);

  @Mapping(target = "assessmentId", source = "assesmentID")
  @Mapping(target = "defaultInd", source = "default")
  @Mapping(target = "results", source = "results.goal")
  @Mapping(target = "assessmentDetails", source = "assesmentDetails.assessmentScreens")
  AssessmentResult toAssessmentResult(AssesmentResultType assesmentResultType);

  @Mapping(target = "attributes", source = "attributes.attribute")
  OpaInstance toOpaInstance(OPAInstanceType opaInstanceType);

  @Mapping(target = "userLoginId", source = "userLoginID")
  UserDetail toUserDetail(User user);

  @InheritInverseConfiguration
  User toUser(UserDetail userDetail);

  @Mapping(target = "documentId", source = "documentID")
  @Mapping(target = "fileData", source = "binData")
  Document toDocument(DocumentElementType documentElementType);

  /**
   * Maps the first result from a list of strings.
   *
   * @param results List of results to be mapped.
   * @return The first result as a string, or null if the list is empty or null.
   */
  default String mapFirstResultToString(List<String> results) {
    return Optional.ofNullable(results).flatMap(r -> r.stream().findFirst()).orElse(null);
  }

  /**
   * Convert a y/n String to a boolean value.
   *
   * @param ynString the y/n string.
   * @return true if the value is y/Y, false otherwise.
   */
  default Boolean toBoolean(String ynString) {
    return ynString != null ? ynString.equalsIgnoreCase("Y") : null;
  }

  /**
   * Convert a boolean flag to a Y/N String.
   *
   * @param flag the boolean flag.
   * @return "Y" if the boolean flag is true, otherwise "N".
   */
  default String toYnString(Boolean flag) {
    return flag != null ? (flag ? "Y" : "N") : null;
  }

  /**
   * Convert a Base64 encoded string to an array of bytes.
   *
   * @param base64EncodedString the Base64 encoded string.
   * @return the array of bytes, or null.
   */
  default byte[] toByteArrayFromBase64EncodedString(String base64EncodedString) {
    return Optional.ofNullable(base64EncodedString)
        .map(s -> Base64.getDecoder().decode(s))
        .orElse(null);
  }

  /**
   * Convert an array of bytes to a Base64 encoded string.
   *
   * @param bytes - the byte array.
   * @return Base64 encoded String, or null.
   */
  default String toBase64EncodedStringFromByteArray(byte[] bytes) {
    return Optional.ofNullable(bytes).map(b -> Base64.getEncoder().encodeToString(b)).orElse(null);
  }
}
