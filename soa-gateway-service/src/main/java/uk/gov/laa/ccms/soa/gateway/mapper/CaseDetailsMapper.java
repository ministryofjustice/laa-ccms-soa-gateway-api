package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.Award;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
import uk.gov.laa.ccms.soa.gateway.model.CaseDoc;
import uk.gov.laa.ccms.soa.gateway.model.CaseSummary;
import uk.gov.laa.ccms.soa.gateway.model.CategoryOfLaw;
import uk.gov.laa.ccms.soa.gateway.model.ContactDetail;
import uk.gov.laa.ccms.soa.gateway.model.CostAward;
import uk.gov.laa.ccms.soa.gateway.model.CostLimitation;
import uk.gov.laa.ccms.soa.gateway.model.ExternalResource;
import uk.gov.laa.ccms.soa.gateway.model.FinancialAward;
import uk.gov.laa.ccms.soa.gateway.model.LandAward;
import uk.gov.laa.ccms.soa.gateway.model.LarDetails;
import uk.gov.laa.ccms.soa.gateway.model.LinkedCase;
import uk.gov.laa.ccms.soa.gateway.model.OtherAsset;
import uk.gov.laa.ccms.soa.gateway.model.OtherParty;
import uk.gov.laa.ccms.soa.gateway.model.OtherPartyPerson;
import uk.gov.laa.ccms.soa.gateway.model.OutcomeDetail;
import uk.gov.laa.ccms.soa.gateway.model.PriorAuthority;
import uk.gov.laa.ccms.soa.gateway.model.ProceedingDetail;
import uk.gov.laa.ccms.soa.gateway.model.ProviderDetail;
import uk.gov.laa.ccms.soa.gateway.model.RecoveryAmount;
import uk.gov.laa.ccms.soa.gateway.model.ScopeLimitation;
import uk.gov.laa.ccms.soa.gateway.model.SubmittedApplicationDetails;
import uk.gov.laa.ccms.soa.gateway.model.TimeRelatedAward;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Case;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDocsElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseList;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CategoryOfLawElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ContactDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostAwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostLimitationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ExtResourceElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.FinancialAwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LARDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LandAwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LinkedCaseType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherAssetElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyPersonType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OutcomeDetailElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryAmountElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ScopeLimitationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.TimeRelatedElementType;

/**
 * Mapper interface for converting case data between different representations.
 */
@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface CaseDetailsMapper {

  List<CaseSummary> toCaseSummaryList(List<CaseList> caseList);

  /**
   * Converts the {@link CaseInqRS} object to a list of {@link CaseSummary} objects.
   *
   * <p>This default method takes a {@link CaseInqRS} instance and extracts the list of
   * cases from it. It then delegates the conversion of this list to the
   * {@link #toCaseSummaryList(List)} method. If the {@link CaseInqRS} instance is null, or if it
   * does not contain any case data, an empty list is returned.</p>
   *
   * @param clientInqRs The case inquiry response to be converted.
   * @return A list of {@link CaseSummary} objects or an empty list if no case data is available.
   */
  default List<CaseSummary> toCaseSummaryList(CaseInqRS clientInqRs) {
    if (clientInqRs != null) {
      List<CaseList> caseList = clientInqRs.getCaseList();
      if (caseList != null) {
        return toCaseSummaryList(caseList);
      }
    }
    return Collections.emptyList();
  }

  CaseDetails toCaseDetails(Page<CaseSummary> page);

  @Mapping(target = ".", source = "caseDetails")
  @Mapping(target = "linkedCases", source = "caseDetails.linkedCases.linkedCase")
  @Mapping(target = "awards", source = "caseDetails.awards.award")
  @Mapping(target = "priorAuthorities", source = "caseDetails.priorAuthorities.priorAuthority")
  @Mapping(target = "availableFunctions", source = "caseDetails.availableFunctions.function")
  @Mapping(target = "caseDocs", source = "caseDetails.caseDocs.caseDoc")
  CaseDetail toCaseDetail(Case sourceCase);

  @Mapping(target = "caseStatusDisplay", source = "displayStatus")
  CaseSummary toCaseSummary(CaseList clientList);

  @Mapping(target = "otherParties", source = "otherParties.otherParty")
  @Mapping(target = "externalResources", source = "externalResources.externalResource")
  @Mapping(target = "proceedings", source = "proceedings.proceeding")
  @Mapping(target = "meansAssesments", source = "meansAssesments.assesmentResults")
  @Mapping(target = "meritsAssesments", source = "meritsAssesments.assesmentResults")
  @Mapping(target = "larDetails", source = "LARDetails")
  SubmittedApplicationDetails toApplicationDetails(
      uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails
          soaApplicationDetails);

  @Mapping(target = "costLimitId", source = "costLimitID")
  @Mapping(target = "billingProviderId", source = "billingProviderID")
  CostLimitation toCostLimitation(CostLimitationElementType costLimitationElementType);

  @Mapping(target = "costLimitations", source = "costLimitations.costLimitation")
  CategoryOfLaw toCategoryOfLaw(CategoryOfLawElementType categoryOfLawElementType);

  @Mapping(target = "costCeiling", source = "costCeiling.costLimitation")
  ExternalResource toExternalResource(ExtResourceElementType extResourceElementType);

  @Mapping(target = ".", source = "proceedingDetails")
  @Mapping(target = "availableFunctions", source = "availableFunctions.function")
  @Mapping(target = "proceedingCaseId", source = "proceedingCaseID")
  @Mapping(target = "scopeLimitations",
      source = "proceedingDetails.scopeLimitations.scopeLimitation")
  ProceedingDetail toProceedingDetail(ProceedingElementType proceedingElementType);

  @Mapping(target = "details", source = "details.attribute")
  PriorAuthority toPriorAuthority(PriorAuthorityElementType priorAuthorityElementType);

  @Mapping(target = "feeEarnerId", source = "feeEarnerID")
  LinkedCase toLinkedCase(LinkedCaseType linkedCaseType);

  @Mapping(target = "awardId", source = "awardID")
  @Mapping(target = ".", source = "awardDetails")
  @Mapping(target = "financialAward", source = "awardDetails.awardDetails.financialAward")
  @Mapping(target = "costAward", source = "awardDetails.awardDetails.costAward")
  @Mapping(target = "landAward", source = "awardDetails.awardDetails.landAward")
  @Mapping(target = "otherAsset", source = "awardDetails.awardDetails.otherAsset")
  Award toAward(AwardElementType awardElementType);

  @Mapping(target = "ccmsDocumentId", source = "CCMSDocumentID")
  CaseDoc toCaseDoc(CaseDocsElementType caseDocsElementType);

  @Mapping(target = "liableParties", source = "liableParties.otherParyID")
  @Mapping(target = "preCertificateAwardLsc", source = "preCertificateAwardLSC")
  @Mapping(target = "certificateCostRateLsc", source = "certificateCostRateLSC")
  CostAward toCostAward(CostAwardElementType costAwardElementType);

  @Mapping(target = "liableParties", source = "liableParties.otherPartyID")
  FinancialAward toFinancialAward(FinancialAwardElementType financialAwardElementType);

  @Mapping(target = "otherProprietors", source = "otherProprietors.otherPartyID")
  LandAward toLandAward(LandAwardElementType landAwardElementType);

  @Mapping(target = "heldBy", source = "heldBy.otherPartyID")
  OtherAsset toOtherAsset(OtherAssetElementType otherAssetElementType);

  @Mapping(target = "otherPartyId", source = "otherPartyID")
  @Mapping(target = "person", source = "otherPartyDetail.person")
  @Mapping(target = "organisation", source = "otherPartyDetail.organization")
  OtherParty toOtherParty(OtherPartyElementType otherPartyElementType);

  @Mapping(target = "contactUserId", source = "contactUserID")
  @Mapping(target = "providerFirmId", source = "providerFirmID")
  @Mapping(target = "providerOfficeId", source = "providerOfficeID")
  @Mapping(target = "feeEarnerContactId", source = "feeEarnerContactID")
  @Mapping(target = "supervisorContactId", source = "supervisorContactID")
  ProviderDetail toProviderDetail(ProviderDetails providerDetails);

  @Mapping(target = "larScopeFlag", source = "LARScopeFlag")
  @Mapping(target = "legalHelpUfn", source = "legalHelpUFN")
  LarDetails toLarDetails(LARDetails larDetails);

  @Mapping(target = "scopeLimitationId", source = "scopeLimitationID")
  ScopeLimitation toScopeLimitation(ScopeLimitationElementType scopeLimitationElementType);

  @Mapping(target = "widerBenefits", source = "widerBenifits")
  OutcomeDetail toOutcomeDetail(OutcomeDetailElementType outcomeDetailElementType);

  @Mapping(target = "paidToLsc", source = "paidToLSC")
  RecoveryAmount toRecoveryAmount(RecoveryAmountElementType recoveryAmountElementType);

  @Mapping(target = "awardTriggeringEvent", source = "awardTrigeringEvent")
  TimeRelatedAward toTimeRelatedAward(TimeRelatedElementType timeRelatedElementType);

  @Mapping(target = "niNumber", source = "NINumber")
  @Mapping(target = "assessedAssets", source = "assessedAsstes")
  OtherPartyPerson toOtherPartyPerson(OtherPartyPersonType otherPartyPersonType);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "passwordReminder", ignore = true)
  @Mapping(target = "correspondenceMethod", ignore = true)
  @Mapping(target = "correspondenceLanguage", ignore = true)
  ContactDetail toContactDetail(ContactDetails contactDetails);

  @Mapping(target = "submissionStatus", source = "headerRS.status.status")
  @Mapping(target = "referenceNumber", source = "caseReferenceNumber")
  uk.gov.laa.ccms.soa.gateway.model.TransactionStatus toTransactionStatus(
      CaseAddUpdtStatusRS response);

}
