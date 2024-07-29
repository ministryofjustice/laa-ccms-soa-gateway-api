package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.ApplicationDetails;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentResult;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentScreen;
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
import uk.gov.laa.ccms.soa.gateway.model.OpaAttribute;
import uk.gov.laa.ccms.soa.gateway.model.OpaGoal;
import uk.gov.laa.ccms.soa.gateway.model.OtherAsset;
import uk.gov.laa.ccms.soa.gateway.model.OtherParty;
import uk.gov.laa.ccms.soa.gateway.model.OtherPartyPerson;
import uk.gov.laa.ccms.soa.gateway.model.OutcomeDetail;
import uk.gov.laa.ccms.soa.gateway.model.PriorAuthority;
import uk.gov.laa.ccms.soa.gateway.model.ProceedingDetail;
import uk.gov.laa.ccms.soa.gateway.model.ProviderDetail;
import uk.gov.laa.ccms.soa.gateway.model.RecoveryAmount;
import uk.gov.laa.ccms.soa.gateway.model.ScopeLimitation;
import uk.gov.laa.ccms.soa.gateway.model.TimeRelatedAward;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardsElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Case;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseAdd;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDocs;
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
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LinkedCaseUpdateType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LinkedCasesUpdate;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherAssetElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyPersonType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OutcomeDetailElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorities;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryAmountElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ScopeLimitationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.TimeRelatedElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentDetailType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentScreenType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAAttributesType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAGoalType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType.Attributes;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;


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
  default List<CaseSummary> toCaseSummaryList(final CaseInqRS clientInqRs) {
    if (clientInqRs != null) {
      List<CaseList> caseList = clientInqRs.getCaseList();
      if (caseList != null) {
        return toCaseSummaryList(caseList);
      }
    }
    return Collections.emptyList();
  }

  CaseDetails toCaseDetails(final Page<CaseSummary> page);

  @Mapping(target = ".", source = "caseDetails")
  @Mapping(target = "linkedCases", source = "caseDetails.linkedCases.linkedCase")
  @Mapping(target = "awards", source = "caseDetails.awards.award")
  @Mapping(target = "priorAuthorities", source = "caseDetails.priorAuthorities.priorAuthority")
  @Mapping(target = "availableFunctions", source = "caseDetails.availableFunctions.function")
  @Mapping(target = "caseDocs", source = "caseDetails.caseDocs.caseDoc")
  CaseDetail toCaseDetail(final Case sourceCase);

  @Mapping(target = "caseStatusDisplay", source = "displayStatus")
  CaseSummary toCaseSummary(CaseList clientList);


  @Mapping(target = "caseDetails", source = ".")
  CaseAdd toCaseAdd(final CaseDetail caseDetail);

  default LinkedCasesUpdate toLinkedCasesUpdate(final List<LinkedCase> linkedCases) {
    LinkedCasesUpdate linkedCasesUpdate = new LinkedCasesUpdate();

    if (linkedCases != null) {
      linkedCasesUpdate.getLinkedCase().addAll(
          linkedCases.stream().map(this::toLinkedCaseUpdateType).toList());
    }

    return linkedCasesUpdate;
  }

  default AwardsElementType toAwardsElementType(final List<Award> awards) {
    AwardsElementType awardsElementType = new AwardsElementType();

    if (awards != null) {
      awardsElementType.getAward().addAll(
          awards.stream().map(this::toAwardElementType).toList());
    }

    return awardsElementType;
  }

  default PriorAuthorities toPriorAuthorities(final List<PriorAuthority> priorAuthorities) {
    PriorAuthorities soaPriorAuthorities = new PriorAuthorities();

    if (priorAuthorities != null) {
      soaPriorAuthorities.getPriorAuthority().addAll(
          priorAuthorities.stream().map(this::toPriorAuthorityElementType).toList());
    }

    return soaPriorAuthorities;
  }

  default CaseDocs toCaseDocs(final List<CaseDoc> caseDocs) {
    CaseDocs soaCaseDocs = new CaseDocs();

    if (caseDocs != null) {
      soaCaseDocs.getCaseDoc().addAll(
          caseDocs.stream().map(this::toCaseDocElementType).toList());
    }

    return soaCaseDocs;
  }

  default OPAResultType toOpaResultType(final List<OpaGoal> opaGoals) {
    OPAResultType opaResultType = new OPAResultType();

    if (opaGoals != null) {
      opaResultType.getGoal().addAll(
          opaGoals.stream().map(this::toOpaGoalType).toList());
    }

    return opaResultType;
  }


  default AssessmentDetailType toAssessmentDetailType(
      final List<AssessmentScreen> assessmentScreens) {
    AssessmentDetailType assessmentDetailType = new AssessmentDetailType();

    if (assessmentScreens != null) {
      assessmentDetailType.getAssessmentScreens().addAll(
          assessmentScreens.stream().map(this::toAssessmentScreenType).toList());
    }

    return assessmentDetailType;
  }

  default Attributes toAttributes(List<OpaAttribute> opaAttributes) {
    Attributes attributes = new Attributes();

    if (opaAttributes != null) {
      attributes.getAttribute().addAll(
          opaAttributes.stream().map(this::toOpaAttributesType).toList());
    }

    return attributes;
  }

  @Mapping(target = "otherParties", source = "otherParties.otherParty")
  @Mapping(target = "externalResources", source = "externalResources.externalResource")
  @Mapping(target = "proceedings", source = "proceedings.proceeding")
  @Mapping(target = "meansAssesments", source = "meansAssesments.assesmentResults")
  @Mapping(target = "meritsAssesments", source = "meritsAssesments.assesmentResults")
  @Mapping(target = "larDetails", source = "LARDetails")
  ApplicationDetails toApplicationDetails(
      final uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails
          soaApplicationDetails);

  @InheritInverseConfiguration
  uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails
      toSoaApplicationDetails(final ApplicationDetails applicationDetails);

  @Mapping(target = "costLimitId", source = "costLimitID")
  @Mapping(target = "billingProviderId", source = "billingProviderID")
  CostLimitation toCostLimitation(final CostLimitationElementType costLimitationElementType);

  @InheritInverseConfiguration
  CostLimitationElementType toCostLimitationElementType(final CostLimitation costLimitation);

  @Mapping(target = "costLimitations", source = "costLimitations.costLimitation")
  CategoryOfLaw toCategoryOfLaw(final CategoryOfLawElementType categoryOfLawElementType);

  @InheritInverseConfiguration
  CategoryOfLawElementType toCategoryOfLawElementType(final CategoryOfLaw categoryOfLaw);

  @Mapping(target = "costCeiling", source = "costCeiling.costLimitation")
  ExternalResource toExternalResource(final ExtResourceElementType extResourceElementType);

  @InheritInverseConfiguration
  ExtResourceElementType toExtResourceElementType(final ExternalResource externalResource);

  @Mapping(target = ".", source = "proceedingDetails")
  @Mapping(target = "availableFunctions", source = "availableFunctions.function")
  @Mapping(target = "proceedingCaseId", source = "proceedingCaseID")
  @Mapping(target = "scopeLimitations",
      source = "proceedingDetails.scopeLimitations.scopeLimitation")
  ProceedingDetail toProceedingDetail(final ProceedingElementType proceedingElementType);

  @InheritInverseConfiguration
  ProceedingElementType toProceedingDetail(final ProceedingDetail proceedingDetail);

  @Mapping(target = "details", source = "details.attribute")
  PriorAuthority toPriorAuthority(final PriorAuthorityElementType priorAuthorityElementType);

  @InheritInverseConfiguration
  PriorAuthorityElementType toPriorAuthorityElementType(final PriorAuthority priorAuthority);

  @Mapping(target = "feeEarnerId", source = "feeEarnerID")
  LinkedCase toLinkedCase(final LinkedCaseType linkedCaseType);

  LinkedCaseUpdateType toLinkedCaseUpdateType(final LinkedCase linkedCase);

  @Mapping(target = "awardId", source = "awardID")
  @Mapping(target = ".", source = "awardDetails")
  @Mapping(target = "financialAward", source = "awardDetails.awardDetails.financialAward")
  @Mapping(target = "costAward", source = "awardDetails.awardDetails.costAward")
  @Mapping(target = "landAward", source = "awardDetails.awardDetails.landAward")
  @Mapping(target = "otherAsset", source = "awardDetails.awardDetails.otherAsset")
  Award toAward(final AwardElementType awardElementType);

  @InheritInverseConfiguration
  AwardElementType toAwardElementType(final Award award);

  @Mapping(target = "ccmsDocumentId", source = "CCMSDocumentID")
  CaseDoc toCaseDoc(final CaseDocsElementType caseDocsElementType);

  @InheritInverseConfiguration
  CaseDocsElementType toCaseDocElementType(final CaseDoc caseDoc);

  @Mapping(target = "liableParties", source = "liableParties.otherParyID")
  @Mapping(target = "preCertificateAwardLsc", source = "preCertificateAwardLSC")
  @Mapping(target = "certificateCostRateLsc", source = "certificateCostRateLSC")
  CostAward toCostAward(final CostAwardElementType costAwardElementType);

  @InheritInverseConfiguration
  CostAwardElementType toCostAwardElementType(final CostAward costAward);

  @Mapping(target = "liableParties", source = "liableParties.otherPartyID")
  FinancialAward toFinancialAward(final FinancialAwardElementType financialAwardElementType);

  @InheritInverseConfiguration
  FinancialAwardElementType toFinancialAwardElementType(final FinancialAward financialAward);

  @Mapping(target = "otherProprietors", source = "otherProprietors.otherPartyID")
  LandAward toLandAward(final LandAwardElementType landAwardElementType);

  @InheritInverseConfiguration
  LandAwardElementType toLandAwardElementType(final LandAward landAward);

  @Mapping(target = "heldBy", source = "heldBy.otherPartyID")
  OtherAsset toOtherAsset(final OtherAssetElementType otherAssetElementType);

  @InheritInverseConfiguration
  OtherAssetElementType toOtherAssetElementType(final OtherAsset otherAsset);

  @Mapping(target = "otherPartyId", source = "otherPartyID")
  @Mapping(target = "person", source = "otherPartyDetail.person")
  @Mapping(target = "organisation", source = "otherPartyDetail.organization")
  OtherParty toOtherParty(final OtherPartyElementType otherPartyElementType);

  @InheritInverseConfiguration
  OtherPartyElementType toOtherPartyElementType(final OtherParty otherParty);

  @Mapping(target = "contactUserId", source = "contactUserID")
  @Mapping(target = "providerFirmId", source = "providerFirmID")
  @Mapping(target = "providerOfficeId", source = "providerOfficeID")
  @Mapping(target = "feeEarnerContactId", source = "feeEarnerContactID")
  @Mapping(target = "supervisorContactId", source = "supervisorContactID")
  ProviderDetail toProviderDetail(final ProviderDetails providerDetails);

  @InheritInverseConfiguration
  ProviderDetails toProviderDetails(final ProviderDetail providerDetails);

  @Mapping(target = "larScopeFlag", source = "LARScopeFlag")
  @Mapping(target = "legalHelpUfn", source = "legalHelpUFN")
  LarDetails toLarDetails(final LARDetails larDetails);

  @InheritInverseConfiguration
  LARDetails toLarDetails(final LarDetails larDetails);

  @Mapping(target = "scopeLimitationId", source = "scopeLimitationID")
  ScopeLimitation toScopeLimitation(final ScopeLimitationElementType scopeLimitationElementType);

  @InheritInverseConfiguration
  ScopeLimitationElementType toScopeLimitationElementType(final ScopeLimitation scopeLimitation);

  @Mapping(target = "widerBenefits", source = "widerBenifits")
  OutcomeDetail toOutcomeDetail(final OutcomeDetailElementType outcomeDetailElementType);

  @InheritInverseConfiguration
  OutcomeDetailElementType toOutcomeDetailElementType(final OutcomeDetail outcomeDetail);

  @Mapping(target = "paidToLsc", source = "paidToLSC")
  RecoveryAmount toRecoveryAmount(final RecoveryAmountElementType recoveryAmountElementType);

  @InheritInverseConfiguration
  RecoveryAmountElementType toRecoveryAmountElementType(final RecoveryAmount recoveryAmount);

  @Mapping(target = "awardTriggeringEvent", source = "awardTrigeringEvent")
  TimeRelatedAward toTimeRelatedAward(final TimeRelatedElementType timeRelatedElementType);

  @InheritInverseConfiguration
  TimeRelatedElementType toTimeRelatedElementType(final TimeRelatedAward timeRelatedAward);

  @Mapping(target = "niNumber", source = "NINumber")
  @Mapping(target = "assessedAssets", source = "assessedAsstes")
  OtherPartyPerson toOtherPartyPerson(final OtherPartyPersonType otherPartyPersonType);

  @InheritInverseConfiguration
  OtherPartyPersonType toOtherPartyPersonType(final OtherPartyPerson otherPartyPerson);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "passwordReminder", ignore = true)
  @Mapping(target = "correspondenceMethod", ignore = true)
  @Mapping(target = "correspondenceLanguage", ignore = true)
  ContactDetail toContactDetail(final ContactDetails contactDetails);

  @InheritInverseConfiguration
  ContactDetails toContactDetails(final ContactDetail contactDetail);

  OPAGoalType toOpaGoalType(final OpaGoal opaGoal);

  AssessmentScreenType toAssessmentScreenType(final AssessmentScreen assessmentScreen);

  @Mapping(target = "userLoginID", source = "userLoginId")
  User toUser(final UserDetail userDetail);

  @Mapping(target = "assesmentID", source = "assessmentId")
  @Mapping(target = "default", source = "defaultInd")
  @Mapping(target = "assesmentDetails", source = "assessmentDetails")
  AssesmentResultType toAssesmentResultType(final AssessmentResult assessmentResult);

  OPAAttributesType toOpaAttributesType(final OpaAttribute opaAttribute);

  @Mapping(target = "submissionStatus", source = "headerRS.status.status")
  @Mapping(target = "referenceNumber", source = "caseReferenceNumber")
  uk.gov.laa.ccms.soa.gateway.model.TransactionStatus toTransactionStatus(
      CaseAddUpdtStatusRS response);

}
