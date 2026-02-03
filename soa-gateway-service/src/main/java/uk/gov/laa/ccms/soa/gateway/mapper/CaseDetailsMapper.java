package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentResult;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentScreen;
import uk.gov.laa.ccms.soa.gateway.model.Award;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.CaseDoc;
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
import uk.gov.laa.ccms.soa.gateway.model.OtherPartyOrganisation;
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
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseUpdateRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardsElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Case;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseAdd;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDocs;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDocsElementType;
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
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyOrgType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyPersonType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OutcomeDetailElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OutcomeElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorities;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingDetElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryAmountElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ScopeLimitationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.TimeRelatedElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.UndertakingElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.UpdateApplicationDetails;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentDetailType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentScreenType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAAttributesType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAGoalType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType.Attributes;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAResultType;

/**
 * Mapper interface for converting case data between different representations.
 */
@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface CaseDetailsMapper {

  String APP_TYPE_SUBSTANTIVE = "SUB";
  String APP_TYPE_EMERGENCY_DEVOLVED_POWERS = "DP";
  String APP_TYPE_SUBSTANTIVE_DEVOLVED_POWERS = "SUBDP";

  @Mapping(target = ".", source = "caseDetails")
  @Mapping(target = "linkedCases", source = "caseDetails.linkedCases.linkedCase")
  @Mapping(target = "awards", source = "caseDetails.awards.award")
  @Mapping(target = "priorAuthorities", source = "caseDetails.priorAuthorities.priorAuthority")
  @Mapping(target = "availableFunctions", source = "caseDetails.availableFunctions.function")
  @Mapping(target = "caseDocs", source = "caseDetails.caseDocs.caseDoc")
  @Mapping(target = "undertakingMaximumAmount", ignore = true)
  CaseDetail toCaseDetail(final Case sourceCase);

  @Mapping(target = "caseDetails", source = ".")
  CaseAdd toCaseAdd(final CaseDetail caseDetail);

  @Mapping(target = "updateMsgType", expression = "java( caseUpdateType )")
  @Mapping(target = "priorAuthorities.priorAuthority", source = "caseDetail.priorAuthorities")
  @Mapping(target = "linkedCases.linkedCase", source = "caseDetail.linkedCases")
  @Mapping(target = "applicationDetails", source = ".")
  @Mapping(target = "outcomes.outcome", source = "caseDetail.applicationDetails.proceedings")
  @Mapping(target = "awards.award", source = "caseDetail.awards")
  @Mapping(target = "notifications", ignore = true)
  @Mapping(target = "headerRQ", ignore = true)
  @Mapping(target = "actualCaseStatus", ignore = true)
  @Mapping(target = "messages", ignore = true)
  @Mapping(target = "undertakings", ignore = true)
  CaseUpdateRQ toCaseUpdateRq(final CaseDetail caseDetail, @Context String caseUpdateType);

  @Mapping(target = "proceedingCaseID", source = "proceedingCaseId")
  @Mapping(target = "outcomeDetails", source = "outcome")
  OutcomeElementType toOutcomeElementType(final ProceedingDetail proceedingDetail);

  @Mapping(target = "otherParties.otherParty", source = "applicationDetails.otherParties")
  @Mapping(target = "supervisorContactID",
      source = "applicationDetails.providerDetails.supervisorContactId")
  @Mapping(target = "feeEarnerContactID",
      source = "applicationDetails.providerDetails.feeEarnerContactId")
  @Mapping(target = "externalResources.externalResource",
      source = "applicationDetails.externalResources")
  @Mapping(target = "proceedings.proceeding",
      source = "applicationDetails.proceedings")
  @Mapping(target = "meansAssesments.assesmentResults",
      source = "applicationDetails.meansAssessments")
  @Mapping(target = "meritsAssesments.assesmentResults",
      source = "applicationDetails.meritsAssessments")
  @Mapping(target = ".", source = "applicationDetails")
  @Mapping(target = "undertakings", source = "caseDetail")
  @Mapping(target = "applicationAmendmentType", source = ".",
      qualifiedByName = "mapToApplicationAmendmentType")
  @Mapping(target = "LARDetails", source = "applicationDetails.larDetails")
  UpdateApplicationDetails toUpdateApplicationDetails(
      final CaseDetail caseDetail, @Context String caseUpdateType);

  /**
   * Determine the application amendment type based on the status of the case.
   *
   * @param caseDetail the case detail
   * @param caseUpdateType the case update type
   * @return the determined application amendment type
   */
  @Named("mapToApplicationAmendmentType")
  default String mapToApplicationAmendmentType(final CaseDetail caseDetail,
      @Context final String caseUpdateType) {

    String amendmentType = caseDetail.getApplicationDetails().getApplicationAmendmentType();

    boolean reassessment =
        caseDetail != null && !caseDetail.getAvailableFunctions().contains("MNLA");

    SubmittedApplicationDetails amendment = caseDetail.getApplicationDetails();

    if (amendment.isMeansAssessmentAmended()) {
      if ("MeansReassessment".equals(caseUpdateType) && reassessment) {
        //CR217 - Merits Reassessment so ensure the amendment type is substantive
        amendmentType = APP_TYPE_SUBSTANTIVE;
      }
    }
    return amendmentType;
  }

  /**
   * After mapping {@code SubmittedApplicationDetails}, set the devolved powers date depending on
   * the application amendment type.
   *
   * @param applicationDetails the application details
   * @param updateApplicationDetails the updated application details to map to
   */
  @AfterMapping
  default void setDevolvedPowersDate(SubmittedApplicationDetails applicationDetails,
      @MappingTarget UpdateApplicationDetails updateApplicationDetails) {

    List<String> devolvedPowersTypeList =
        List.of(APP_TYPE_EMERGENCY_DEVOLVED_POWERS, APP_TYPE_SUBSTANTIVE_DEVOLVED_POWERS);

    XMLGregorianCalendar result = null;

    if (updateApplicationDetails.getApplicationAmendmentType() != null
        && devolvedPowersTypeList.contains(applicationDetails.getApplicationAmendmentType())) {
      GregorianCalendar gregCal = new GregorianCalendar();
      Date devolvedPowersDate = applicationDetails.getDevolvedPowersDate();

      if (devolvedPowersDate != null) {
        gregCal.setTime(devolvedPowersDate);

        try {
          result = DatatypeFactory.newInstance()
              .newXMLGregorianCalendar(gregCal);
        } catch (DatatypeConfigurationException e) {
          throw new SoaGatewayMappingException("Unable to process devolved powers date.", e);
        }
      }
    }

    updateApplicationDetails.setDevolvedPowersDate(result);
  }

  @Mapping(target = "maxAmount", source = "undertakingMaximumAmount")
  @Mapping(target = "enteredAmount", source = "undertakingAmount")
  @Mapping(target = "details", ignore = true)
  UndertakingElementType toUndertakingElementType(final CaseDetail caseDetail);

  /**
   * Transform a list of LinkedCase to a LinkedCasesUpdate.
   *
   * @param linkedCases - the linked cases to transform.
   * @return LinkedCasesUpdate.
   */
  default LinkedCasesUpdate toLinkedCasesUpdate(final List<LinkedCase> linkedCases) {
    LinkedCasesUpdate linkedCasesUpdate = new LinkedCasesUpdate();

    if (linkedCases != null) {
      linkedCasesUpdate.getLinkedCase().addAll(
          linkedCases.stream().map(this::toLinkedCaseUpdateType).toList());
    }

    return linkedCasesUpdate;
  }

  /**
   * Transform a list of Award to an AwardsElementType.
   *
   * @param awards - the awards to transform.
   * @return AwardsElementType.
   */
  default AwardsElementType toAwardsElementType(final List<Award> awards) {
    AwardsElementType awardsElementType = new AwardsElementType();

    if (awards != null) {
      awardsElementType.getAward().addAll(
          awards.stream().map(this::toAwardElementType).toList());
    }

    return awardsElementType;
  }

  /**
   * Transform a list of PriorAuthority to a PriorAuthorities.
   *
   * @param priorAuthorities - the prior authorities to transform.
   * @return PriorAuthorities.
   */
  default PriorAuthorities toPriorAuthorities(final List<PriorAuthority> priorAuthorities) {
    PriorAuthorities soaPriorAuthorities = new PriorAuthorities();

    if (priorAuthorities != null) {
      soaPriorAuthorities.getPriorAuthority().addAll(
          priorAuthorities.stream().map(this::toPriorAuthorityElementType).toList());
    }

    return soaPriorAuthorities;
  }

  /**
   * Transform a list of CaseDoc to a CaseDocs.
   *
   * @param caseDocs - the case documents to transform.
   * @return CaseDocs.
   */
  default CaseDocs toCaseDocs(final List<CaseDoc> caseDocs) {
    CaseDocs soaCaseDocs = new CaseDocs();

    if (caseDocs != null) {
      soaCaseDocs.getCaseDoc().addAll(
          caseDocs.stream().map(this::toCaseDocElementType).toList());
    }

    return soaCaseDocs;
  }

  /**
   * Transform a list of OpaGoal to a OPAResultType.
   *
   * @param opaGoals - the opa goals to transform.
   * @return OPAResultType.
   */
  default OPAResultType toOpaResultType(final List<OpaGoal> opaGoals) {
    OPAResultType opaResultType = new OPAResultType();

    if (opaGoals != null) {
      opaResultType.getGoal().addAll(
          opaGoals.stream().map(this::toOpaGoalType).toList());
    }

    return opaResultType;
  }

  /**
   * Transform a list of AssessmentScreen to a AssessmentDetailType.
   *
   * @param assessmentScreens - the assessment screens to transform.
   * @return AssessmentDetailType.
   */
  default AssessmentDetailType toAssessmentDetailType(
      final List<AssessmentScreen> assessmentScreens) {
    AssessmentDetailType assessmentDetailType = new AssessmentDetailType();

    if (assessmentScreens != null) {
      assessmentDetailType.getAssessmentScreens().addAll(
          assessmentScreens.stream().map(this::toAssessmentScreenType).toList());
    }

    return assessmentDetailType;
  }

  /**
   * Transform a list of OpaAttribute to an Attributes.
   *
   * @param opaAttributes - the opa attributes to transform.
   * @return Attributes.
   */
  default Attributes toAttributes(List<OpaAttribute> opaAttributes) {
    Attributes attributes = new Attributes();

    if (opaAttributes != null) {
      attributes.getAttribute().addAll(
          opaAttributes.stream().map(this::toOpaAttributesType).toList());
    }

    return attributes;
  }

  /**
   * Transform a list of ScopeLimitation to a ProceedingDetElementType.ScopeLimitations.
   *
   * @param scopeLimitations - the scope limitations to transform.
   * @return ProceedingDetElementType.ScopeLimitations.
   */
  default ProceedingDetElementType.ScopeLimitations toScopeLimitations(
      List<ScopeLimitation> scopeLimitations) {
    ProceedingDetElementType.ScopeLimitations soaScopeLimitations =
        new ProceedingDetElementType.ScopeLimitations();

    if (scopeLimitations != null) {
      soaScopeLimitations.getScopeLimitation().addAll(
          scopeLimitations.stream().map(this::toScopeLimitationElementType).toList());
    }

    return soaScopeLimitations;
  }

  @Mapping(target = "otherParties", source = "otherParties.otherParty")
  @Mapping(target = "externalResources", source = "externalResources.externalResource")
  @Mapping(target = "proceedings", source = "proceedings.proceeding")
  @Mapping(target = "meansAssessments", source = "meansAssesments.assesmentResults")
  @Mapping(target = "meritsAssessments", source = "meritsAssesments.assesmentResults")
  @Mapping(target = "larDetails", source = "LARDetails")
  @Mapping(target = "meansAssessmentAmended", ignore = true)
  @Mapping(target = "meritsAssessmentAmended", ignore = true)
  SubmittedApplicationDetails toSubmittedApplicationDetails(
      ApplicationDetails soaApplicationDetails);

  @InheritInverseConfiguration
  ApplicationDetails toSoaApplicationDetails(final SubmittedApplicationDetails applicationDetails);

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
  @Mapping(target = "proceedingDetails", source = ".")
  ProceedingElementType toProceedingElementType(final ProceedingDetail proceedingDetail);

  @Mapping(target = "details", source = "details.attribute")
  PriorAuthority toPriorAuthority(final PriorAuthorityElementType priorAuthorityElementType);

  @InheritInverseConfiguration
  PriorAuthorityElementType toPriorAuthorityElementType(final PriorAuthority priorAuthority);

  @Mapping(target = "feeEarnerId", source = "feeEarnerID")
  LinkedCase toLinkedCase(final LinkedCaseType linkedCaseType);

  LinkedCaseUpdateType toLinkedCaseUpdateType(final LinkedCase linkedCase);

  @Mapping(target = "awardId", source = "awardID")
  @Mapping(target = "awardCategory", source = "awardDetails.awardCategory")
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
  @Mapping(target = "otherPartyDetail.organization", source = "organisation")
  OtherPartyElementType toOtherPartyElementType(final OtherParty otherParty);

  OtherPartyOrganisation toOtherPartyOrganisation(final OtherPartyOrgType otherPartyOrgType);

  @InheritInverseConfiguration
  OtherPartyOrgType toOtherPartyOrgType(final OtherPartyOrganisation otherPartyOrganisation);

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
  @Mapping(target = "courtOrderedMeansAssessment", source = "courtOrderedMeansAssesment")
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
