package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentResult;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.laa.ccms.soa.gateway.model.OpaInstance;
import uk.gov.laa.ccms.soa.gateway.model.RecordHistory;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;


/**
 * Mapper interface for converting common data between different representations.
 */
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

  @Mapping(target = "documentId", source = "documentID")
  @Mapping(target = "fileData", source = "binData")
  Document toDocument(DocumentElementType documentElementType);

  @Mapping(target = "caseReferenceNumber",
      expression = "java( mapFirstResultToString(response.getResults()) )")
  CaseReferenceSummary toCaseReferenceSummary(ReferenceDataInqRS response);

  /**
   * Maps the first result from a list of strings.
   *
   * @param results List of results to be mapped.
   * @return The first result as a string, or null if the list is empty or null.
   */
  default String mapFirstResultToString(List<String> results) {
    return Optional.ofNullable(results)
        .flatMap(r -> r.stream().findFirst())
        .orElse(null);
  }

  default Boolean toBoolean(String ynString) {
    return ynString != null ? ynString.equalsIgnoreCase("Y") : null;
  }

  default String toYnString(Boolean flag) {
    return flag != null ? (flag ? "Y" : "N") : null;
  }

  default byte[] toByteArrayFromBase64EncodedString(String base64EncodedString) {
    return Base64.getDecoder().decode(base64EncodedString);
  }

  /**
   * Convert an array of bytes to a Base64 encoded string.
   *
   * @param bytes - the byte array.
   * @return Base64 encoded String, or null.
   */
  default String toBase64EncodedStringFromByteArray(byte[] bytes) {
    return Optional.ofNullable(bytes)
        .map(b -> Base64.getEncoder().encodeToString(b))
        .orElse(null);
  }

}
