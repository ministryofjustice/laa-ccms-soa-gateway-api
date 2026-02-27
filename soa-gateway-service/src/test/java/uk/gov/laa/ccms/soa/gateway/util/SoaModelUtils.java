package uk.gov.laa.ccms.soa.gateway.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ActionListElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardDetailElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardDetailElementType.AwardDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardsElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Case;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDocs;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDocsElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseList;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseStatusElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CategoryOfLawElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Client;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ContactDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostAwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostAwardElementType.LiableParties;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostLimitationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostLimitations;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.DischargeElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ExtResourceElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ExternalResources;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.FinancialAwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LARDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LandAwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LandAwardElementType.OtherProprietors;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LandAwardElementType.Valuation;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LinkedCaseType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LinkedCases;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.MeansAssesments;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.MeritsAssesments;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherAssetElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherAssetElementType.HeldBy;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherParties;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyElementType.OtherPartyDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyOrgType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyPersonType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OutcomeDetailElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorities;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityAttribElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityDetElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingDetElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingDetElementType.ScopeLimitations;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Proceedings;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryAmountElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryElementType.OfferedAmount;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryElementType.RecoveredAmount;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ScopeLimitationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ServiceAddrElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.TimeRelatedElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentDetailType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentScreenType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.Name;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAAttributesType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAEntityType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAGoalType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType.Attributes;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

public class SoaModelUtils {

  public static RecordHistory buildRecordHistory() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory recordHistory =
        new uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory();
    recordHistory.setCreatedBy(buildUser());
    recordHistory.setDateCreated(datatypeFactory.newXMLGregorianCalendar());
    recordHistory.setDateLastUpdated(datatypeFactory.newXMLGregorianCalendar());
    recordHistory.setLastUpdatedBy(buildUser());

    return recordHistory;
  }

  public static User buildUser() {
    User user = new User();
    user.setUserLoginID("loginId");
    user.setUserName("username");
    user.setUserType("usertype");

    return user;
  }

  public static Address buildAddress() {
    Address address = new Address();
    address.setAddressID("addid");
    address.setAddressLine1("addline1");
    address.setAddressLine2("addline2");
    address.setAddressLine3("addline3");
    address.setAddressLine4("addline4");
    address.setCity("city");
    address.setCoffName("careof");
    address.setCountry("country");
    address.setCounty("county");
    address.setHouse("house");
    address.setPostalCode("postcode");
    address.setProvince("province");
    address.setState("state");

    return address;
  }

  public static AssesmentResultType buildAssesmentResultType() {
    OPAGoalType opaGoalType = new OPAGoalType();
    opaGoalType.setAttribute("att");
    opaGoalType.setAttributeValue("val");

    OPAResultType opaResultType = new OPAResultType();
    opaResultType.getGoal().add(opaGoalType);

    OPAInstanceType opaInstanceType = buildOpaInstanceType();

    OPAEntityType opaEntityType = new OPAEntityType();
    opaEntityType.setEntityName("name");
    opaEntityType.setCaption("caption");
    opaEntityType.setSequenceNumber(BigInteger.TEN);
    opaEntityType.getInstances().add(opaInstanceType);

    AssessmentScreenType assessmentScreenType = new AssessmentScreenType();
    assessmentScreenType.setCaption("cap");
    assessmentScreenType.setScreenName("name");
    assessmentScreenType.getEntity().add(opaEntityType);

    AssessmentDetailType assessmentDetailType = new AssessmentDetailType();
    assessmentDetailType.getAssessmentScreens().add(assessmentScreenType);

    AssesmentResultType assessmentResultType = new AssesmentResultType();
    assessmentResultType.setResults(opaResultType);
    assessmentResultType.setAssesmentID("assessid");

    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    assessmentResultType.setDate(datatypeFactory.newXMLGregorianCalendar());
    assessmentResultType.setDefault(Boolean.TRUE);
    assessmentResultType.setAssesmentDetails(assessmentDetailType);

    return assessmentResultType;
  }

  public static OPAInstanceType buildOpaInstanceType() {
    OPAAttributesType opaAttributesType = new OPAAttributesType();
    opaAttributesType.setAttribute("attr");
    opaAttributesType.setCaption("caption");
    opaAttributesType.setResponseType("respType");
    opaAttributesType.setResponseText("responseText");
    opaAttributesType.setResponseValue("respVal");
    opaAttributesType.setUserDefinedInd(Boolean.TRUE);

    Attributes attributes = new Attributes();
    attributes.getAttribute().add(opaAttributesType);

    OPAInstanceType opaInstanceType = new OPAInstanceType();
    opaInstanceType.setInstanceLabel("label");
    opaInstanceType.setAttributes(attributes);

    return opaInstanceType;
  }

  public static DocumentElementType buildDocumentElementType() {
    DocumentElementType documentElementType = new DocumentElementType();
    documentElementType.setDocumentID("1234");
    documentElementType.setDocumentType("sausage");
    documentElementType.setBinData(new byte[8]);
    documentElementType.setFileExtension("doc");
    return documentElementType;
  }

  public static CaseList buildCaseList() {
    CaseList caseList = new CaseList();
    caseList.setCaseReferenceNumber("caseref");
    caseList.setProviderCaseReferenceNumber("provcaseref");
    caseList.setCategoryOfLaw("catoflaw");
    caseList.setFeeEarnerName("feeearner");
    caseList.setDisplayStatus("status");
    caseList.setClient(new Client());
    caseList.getClient().setClientReferenceNumber("clientrefnum");
    caseList.getClient().setFirstName("firstname");
    caseList.getClient().setSurname("surname");
    return caseList;
  }

  public static Case buildCase() {
    Case sourceCase = new Case();
    sourceCase.setCaseReferenceNumber("123");
    sourceCase.setCaseDetails(buildCaseDetails());
    return sourceCase;
  }

  public static uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDetails
      buildCaseDetails() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDetails caseDetails =
        new uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDetails();
    caseDetails.setCertificateType("certtype");
    caseDetails.setLegalHelpCosts(BigDecimal.TEN);
    caseDetails.setCertificateDate(datatypeFactory.newXMLGregorianCalendar());
    caseDetails.setPreCertificateCosts(BigDecimal.TEN);
    caseDetails.setUndertakingAmount(BigDecimal.TEN);

    caseDetails.setApplicationDetails(buildApplicationDetails());
    caseDetails.setCaseStatus(buildCaseStatus());
    caseDetails.setCaseDocs(new CaseDocs());
    caseDetails.getCaseDocs().getCaseDoc().add(buildCaseDocsElementType());
    caseDetails.setLinkedCases(new LinkedCases());
    caseDetails.getLinkedCases().getLinkedCase().add(buildLinkedCaseType());
    caseDetails.setAwards(new AwardsElementType());
    caseDetails.getAwards().getAward().add(buildAwardElementType());
    caseDetails.setAvailableFunctions(buildAvailableFunctions());
    caseDetails.setDischargeStatus(buildDischargeStatus());
    caseDetails.setPriorAuthorities(new PriorAuthorities());
    caseDetails.getPriorAuthorities().getPriorAuthority().add(buildPriorAuthorityElementType());
    caseDetails.setRecordHistory(buildRecordHistory());

    return caseDetails;
  }

  public static PriorAuthorityElementType buildPriorAuthorityElementType() {
    PriorAuthorityAttribElementType priorAuthorityAttribElementType =
        new PriorAuthorityAttribElementType();
    priorAuthorityAttribElementType.setName("aname");
    priorAuthorityAttribElementType.setValue("avalue");

    PriorAuthorityDetElementType priorAuthorityDetElementType = new PriorAuthorityDetElementType();
    priorAuthorityDetElementType.getAttribute().add(priorAuthorityAttribElementType);

    PriorAuthorityElementType priorAuthorityElementType = new PriorAuthorityElementType();
    priorAuthorityElementType.setDetails(priorAuthorityDetElementType);
    priorAuthorityElementType.setPriorAuthorityType("priorauthtype");
    priorAuthorityElementType.setAssessedAmount(BigDecimal.TEN);
    priorAuthorityElementType.setDescription("adescription");
    priorAuthorityElementType.setDecisionStatus("decstatus");
    priorAuthorityElementType.setReasonForRequest("reqreason");
    priorAuthorityElementType.setRequestAmount(BigDecimal.TEN);

    return priorAuthorityElementType;
  }

  public static DischargeElementType buildDischargeStatus() {
    DischargeElementType dischargeElementType = new DischargeElementType();
    dischargeElementType.setOtherDetails("otherdets");
    dischargeElementType.setReason("reason");
    dischargeElementType.setClientContinuePvtInd(Boolean.TRUE);

    return dischargeElementType;
  }

  public static ActionListElementType buildAvailableFunctions() {
    ActionListElementType actionListElementType = new ActionListElementType();
    actionListElementType.getFunction().add("afunction");

    return actionListElementType;
  }

  public static AwardElementType buildAwardElementType() {
    AwardDetails awardDetails = new AwardDetails();
    awardDetails.setCostAward(buildCostAwardElementType());
    awardDetails.setFinancialAward(buildFinancialAwardElementType());
    awardDetails.setLandAward(buildLandAwardElementType());
    awardDetails.setOtherAsset(buildOtherAssetElementType());

    AwardDetailElementType awardDetailElementType = new AwardDetailElementType();
    awardDetailElementType.setAwardDetails(awardDetails);
    awardDetailElementType.setAwardCategory("awardcat");

    AwardElementType awardElementType = new AwardElementType();
    awardElementType.setAwardDetails(awardDetailElementType);

    return awardElementType;
  }

  public static CostAwardElementType buildCostAwardElementType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    CostAwardElementType costAwardElementType = new CostAwardElementType();
    costAwardElementType.setAwardedBy("awardperson");
    costAwardElementType.setOtherDetails("otherdets");
    costAwardElementType.setInterestAwardedRate(BigDecimal.TEN);
    costAwardElementType.setCertificateCostRateLSC(BigDecimal.TEN);
    costAwardElementType.setCertificateCostRateMarket(BigDecimal.TEN);
    costAwardElementType.setCourtAssessmentStatus("courtassessstat");
    costAwardElementType.setInterestAwardedStartDate(datatypeFactory.newXMLGregorianCalendar());
    costAwardElementType.setOrderDate(datatypeFactory.newXMLGregorianCalendar());
    costAwardElementType.setOrderDateServed(datatypeFactory.newXMLGregorianCalendar());
    costAwardElementType.setPreCertificateAwardLSC(BigDecimal.TEN);
    costAwardElementType.setPreCertificateAwardOth(BigDecimal.TEN);
    costAwardElementType.setLiableParties(buildLiableParties());
    costAwardElementType.setRecovery(buildRecoveryElementType());
    costAwardElementType.setServiceAddress(buildServiceAddrElementType());
    return costAwardElementType;
  }

  public static FinancialAwardElementType buildFinancialAwardElementType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    FinancialAwardElementType financialAwardElementType = new FinancialAwardElementType();
    financialAwardElementType.setAmount(BigDecimal.TEN);
    financialAwardElementType.setAwardedBy("awardperson");
    financialAwardElementType.setAwardJustifications("itsjustified");
    financialAwardElementType.setInterimAward(BigDecimal.TEN);
    financialAwardElementType.setOrderDateServed(datatypeFactory.newXMLGregorianCalendar());
    financialAwardElementType.setOrderDate(datatypeFactory.newXMLGregorianCalendar());
    financialAwardElementType.setOtherDetails("otherdets");
    financialAwardElementType.setLiableParties(new FinancialAwardElementType.LiableParties());
    financialAwardElementType.getLiableParties().getOtherPartyID().add("liableparty");
    financialAwardElementType.setRecovery(buildRecoveryElementType());
    financialAwardElementType.setServiceAddress(buildServiceAddrElementType());
    financialAwardElementType.setStatutoryChangeReason("statchange");
    return financialAwardElementType;
  }

  public static RecoveryElementType buildRecoveryElementType() {
    RecoveredAmount recoveredAmount = new RecoveredAmount();
    recoveredAmount.setClient(buildRecoveryAmountElementType());
    recoveredAmount.setCourt(buildRecoveryAmountElementType());
    recoveredAmount.setSolicitor(buildRecoveryAmountElementType());

    OfferedAmount offeredAmount = new OfferedAmount();
    offeredAmount.setAmount(BigDecimal.TEN);
    offeredAmount.setConditionsOfOffer("acondition");

    RecoveryElementType recoveryElementType = new RecoveryElementType();
    recoveryElementType.setAwardValue(BigDecimal.TEN);
    recoveryElementType.setOfferedAmount(offeredAmount);
    recoveryElementType.setRecoveredAmount(recoveredAmount);
    recoveryElementType.setLeaveOfCourtReqdInd(Boolean.TRUE);
    recoveryElementType.setUnRecoveredAmount(BigDecimal.TEN);
    return recoveryElementType;
  }

  public static LiableParties buildLiableParties() {
    LiableParties liableParties = new LiableParties();
    liableParties.getOtherParyID().add("liableparty");
    return liableParties;
  }

  public static LandAwardElementType buildLandAwardElementType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    OtherProprietors otherProprietors = new OtherProprietors();
    otherProprietors.getOtherPartyID().add("otherprop");

    Valuation valuation = new Valuation();
    valuation.setAmount(BigDecimal.TEN);
    valuation.setDate(datatypeFactory.newXMLGregorianCalendar());
    valuation.setCriteria("crit");

    LandAwardElementType landAwardElementType = new LandAwardElementType();
    landAwardElementType.setAwardedBy("awardperson");
    landAwardElementType.setAwardedPercentage(BigDecimal.TEN);
    landAwardElementType.setDescription("adescr");
    landAwardElementType.setRecovery("arecovery");
    landAwardElementType.setEquity("equal");
    landAwardElementType.setDisputedPercentage(BigDecimal.TEN);
    landAwardElementType.setLandChargeRegistration("landreg");
    landAwardElementType.setMortgageAmountDue(BigDecimal.TEN);
    landAwardElementType.setNoRecoveryDetails("norecover");
    landAwardElementType.setOrderDate(datatypeFactory.newXMLGregorianCalendar());
    landAwardElementType.setOtherProprietors(otherProprietors);
    landAwardElementType.setPropertyAddress(buildServiceAddrElementType());
    landAwardElementType.setRegistrationRef("regref");
    landAwardElementType.setStatChargeExemptReason("reason");
    landAwardElementType.setTimeRelatedAward(buildTimeRelatedElementType());
    landAwardElementType.setTitleNo("titleno");
    landAwardElementType.setValuation(valuation);
    return landAwardElementType;
  }

  public static ServiceAddrElementType buildServiceAddrElementType() {
    ServiceAddrElementType serviceAddrElementType = new ServiceAddrElementType();
    serviceAddrElementType.setAddressLine1("addr1");
    serviceAddrElementType.setAddressLine2("addr2");
    serviceAddrElementType.setAddressLine3("addr3");
    return serviceAddrElementType;
  }

  public static OtherAssetElementType buildOtherAssetElementType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    HeldBy heldBy = new HeldBy();
    heldBy.getOtherPartyID().add("holder");

    OtherAssetElementType.Valuation otherValuation = new OtherAssetElementType.Valuation();
    otherValuation.setAmount(BigDecimal.TEN);
    otherValuation.setDate(datatypeFactory.newXMLGregorianCalendar());
    otherValuation.setCriteria("crit");

    OtherAssetElementType otherAssetElementType = new OtherAssetElementType();
    otherAssetElementType.setAwardedBy("awardperson");
    otherAssetElementType.setAwardedPercentage(BigDecimal.TEN);
    otherAssetElementType.setDescription("descr");
    otherAssetElementType.setRecovery("recover");
    otherAssetElementType.setOrderDate(datatypeFactory.newXMLGregorianCalendar());
    otherAssetElementType.setAwardedAmount(BigDecimal.TEN);
    otherAssetElementType.setDisputedAmount(BigDecimal.TEN);
    otherAssetElementType.setHeldBy(heldBy);
    otherAssetElementType.setNoRecoveryDetails("norecover");
    otherAssetElementType.setDisputedPercentage(BigDecimal.TEN);
    otherAssetElementType.setRecoveredAmount(BigDecimal.TEN);
    otherAssetElementType.setRecoveredPercentage(BigDecimal.TEN);
    otherAssetElementType.setStatChargeExemptReason("exreason");
    otherAssetElementType.setValuation(otherValuation);
    otherAssetElementType.setTimeRelatedAward(buildTimeRelatedElementType());
    return otherAssetElementType;
  }

  public static RecoveryAmountElementType buildRecoveryAmountElementType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    RecoveryAmountElementType recoveryAmountElementType = new RecoveryAmountElementType();
    recoveryAmountElementType.setAmount(BigDecimal.TEN);
    recoveryAmountElementType.setDateReceived(datatypeFactory.newXMLGregorianCalendar());
    recoveryAmountElementType.setPaidToLSC(BigDecimal.TEN);
    return recoveryAmountElementType;
  }

  public static TimeRelatedElementType buildTimeRelatedElementType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    TimeRelatedElementType timeRelatedElementType = new TimeRelatedElementType();
    timeRelatedElementType.setAmount(BigDecimal.TEN);
    timeRelatedElementType.setAwardType("awardType");
    timeRelatedElementType.setDescription("adescr");
    timeRelatedElementType.setOtherDetails("otherdets");
    timeRelatedElementType.setAwardDate(datatypeFactory.newXMLGregorianCalendar());
    timeRelatedElementType.setAwardTrigeringEvent("trigger");
    return timeRelatedElementType;
  }

  public static Client buildClient() {
    Client client = new Client();
    client.setFirstName("firstname");
    client.setSurname("asurname");
    client.setClientReferenceNumber("clientref");

    return client;
  }

  public static LinkedCaseType buildLinkedCaseType() {
    LinkedCaseType linkedCaseType = new LinkedCaseType();
    linkedCaseType.setCaseStatus("casestatus");
    linkedCaseType.setCaseReferenceNumber("caseref");
    linkedCaseType.setClient(buildClient());
    linkedCaseType.setLinkType("linkType");
    linkedCaseType.setFeeEarnerName("afeeearner");
    linkedCaseType.setCategoryOfLawCode("catlawcode");
    linkedCaseType.setCategoryOfLawDesc("catlawdesc");
    linkedCaseType.setFeeEarnerID("feeearnerid");
    linkedCaseType.setProviderReferenceNumber("providerref");
    linkedCaseType.setPublicFundingAppliedInd(Boolean.TRUE);

    return linkedCaseType;
  }

  public static CaseDocsElementType buildCaseDocsElementType() {
    CaseDocsElementType caseDocsElementType = new CaseDocsElementType();
    caseDocsElementType.setCCMSDocumentID("docid");
    caseDocsElementType.setDocumentSubject("docsubj");

    return caseDocsElementType;
  }

  public static CaseStatusElementType buildCaseStatus() {
    CaseStatusElementType caseStatusElementType = new CaseStatusElementType();
    caseStatusElementType.setActualCaseStatus("actstat");
    caseStatusElementType.setDisplayCaseStatus("dispstat");
    caseStatusElementType.setStatusUpdateInd(true);

    return caseStatusElementType;
  }

  public static uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails
      buildApplicationDetails() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails
        applicationDetails =
            new uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails();
    applicationDetails.setClient(buildClient());
    applicationDetails.setApplicationAmendmentType("appamendtype");
    applicationDetails.setLARDetails(buildLARDetails());
    applicationDetails.setProviderDetails(buildProviderDetails());
    applicationDetails.setCategoryOfLaw(buildCategoryOfLawElementType());
    applicationDetails.setCertificateType("certtype");
    applicationDetails.setCorrespondenceAddress(buildAddress());
    applicationDetails.setDateOfFirstAttendance(datatypeFactory.newXMLGregorianCalendar());
    applicationDetails.setDateOfHearing(datatypeFactory.newXMLGregorianCalendar());
    applicationDetails.setDevolvedPowersDate(datatypeFactory.newXMLGregorianCalendar());
    applicationDetails.setExternalResources(new ExternalResources());
    applicationDetails
        .getExternalResources()
        .getExternalResource()
        .add(buildExtResourceElementType());
    applicationDetails.setFixedHearingDateInd(Boolean.TRUE);
    applicationDetails.setHighProfileCaseInd(Boolean.TRUE);
    applicationDetails.setMeansAssesments(new MeansAssesments());
    applicationDetails.getMeansAssesments().getAssesmentResults().add(buildAssesmentResultType());
    applicationDetails.setMeritsAssesments(new MeritsAssesments());
    applicationDetails.getMeritsAssesments().getAssesmentResults().add(buildAssesmentResultType());
    applicationDetails.setOtherParties(new OtherParties());
    applicationDetails.getOtherParties().getOtherParty().add(buildOtherPartyElementType());
    applicationDetails.setPreferredAddress("preferredaddr");
    applicationDetails.setProceedings(new Proceedings());
    applicationDetails.getProceedings().getProceeding().add(buildProceedingElementType());
    applicationDetails.setPurposeOfApplication("purpose");
    applicationDetails.setPurposeOfHearing("hearingpurpose");

    return applicationDetails;
  }

  public static ProceedingElementType buildProceedingElementType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    OutcomeDetailElementType outcomeDetailElementType = buildOutcomeDetailElementType();

    ScopeLimitationElementType scopeLimitationElementType = buildScopeLimitationElementType();

    ProceedingDetElementType.ScopeLimitations scopeLimitations = new ScopeLimitations();
    scopeLimitations.getScopeLimitation().add(scopeLimitationElementType);

    ProceedingDetElementType proceedingDetElementType = new ProceedingDetElementType();
    proceedingDetElementType.setClientInvolvementType("clientinvtype");
    proceedingDetElementType.setProceedingType("proctype");
    proceedingDetElementType.setMatterType("mattype");
    proceedingDetElementType.setProceedingDescription("procdescr");
    proceedingDetElementType.setOrderType("ordtype");
    proceedingDetElementType.setDateCostsValid(datatypeFactory.newXMLGregorianCalendar());
    proceedingDetElementType.setDateDevolvedPowersUsed(datatypeFactory.newXMLGregorianCalendar());
    proceedingDetElementType.setDateGranted(datatypeFactory.newXMLGregorianCalendar());
    proceedingDetElementType.setDevolvedPowersInd(Boolean.TRUE);
    proceedingDetElementType.setLevelOfService("level");
    proceedingDetElementType.setOutcome(outcomeDetailElementType);
    proceedingDetElementType.setScopeLimitationApplied("alimit");
    proceedingDetElementType.setScopeLimitations(scopeLimitations);
    proceedingDetElementType.setStage("stage");

    ActionListElementType actionListElementType = new ActionListElementType();
    actionListElementType.getFunction().add("action");

    ProceedingElementType proceedingElementType = new ProceedingElementType();
    proceedingElementType.setProceedingDetails(proceedingDetElementType);
    proceedingElementType.setProceedingCaseID("caseid");
    proceedingElementType.setAvailableFunctions(actionListElementType);
    proceedingElementType.setLeadProceedingIndicator(Boolean.TRUE);
    proceedingElementType.setStatus("status");
    proceedingElementType.setDateApplied(datatypeFactory.newXMLGregorianCalendar());
    proceedingElementType.setOutcomeCourtCaseNumber("occn");

    return proceedingElementType;
  }

  public static ScopeLimitationElementType buildScopeLimitationElementType() {
    ScopeLimitationElementType scopeLimitationElementType = new ScopeLimitationElementType();
    scopeLimitationElementType.setScopeLimitation("limit");
    scopeLimitationElementType.setScopeLimitationID("id");
    scopeLimitationElementType.setScopeLimitationWording("wording");
    scopeLimitationElementType.setDelegatedFunctionsApply(Boolean.TRUE);
    return scopeLimitationElementType;
  }

  public static OutcomeDetailElementType buildOutcomeDetailElementType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    OutcomeDetailElementType outcomeDetailElementType = new OutcomeDetailElementType();
    outcomeDetailElementType.setOutcomeCourtCaseNumber("casenum");
    outcomeDetailElementType.setAdditionalResultInfo("addresult");
    outcomeDetailElementType.setAltAcceptanceReason("altacceptreason");
    outcomeDetailElementType.setCourtCode("courtcode");
    outcomeDetailElementType.setResult("result");
    outcomeDetailElementType.setAltDisputeResolution("dispute");
    outcomeDetailElementType.setFinalWorkDate(datatypeFactory.newXMLGregorianCalendar());
    outcomeDetailElementType.setIssueDate(datatypeFactory.newXMLGregorianCalendar());
    outcomeDetailElementType.setResolutionMethod("method");
    outcomeDetailElementType.setStageEnd("end");
    outcomeDetailElementType.setWiderBenifits("ben");
    return outcomeDetailElementType;
  }

  public static OtherPartyElementType buildOtherPartyElementType() {
    OtherPartyDetail otherPartyDetail = new OtherPartyDetail();
    otherPartyDetail.setOrganization(buildOtherPartyOrgType());
    otherPartyDetail.setPerson(buildOtherPartyPersonType());

    OtherPartyElementType otherPartyElementType = new OtherPartyElementType();
    otherPartyElementType.setOtherPartyDetail(otherPartyDetail);
    otherPartyElementType.setOtherPartyID("partyid");
    otherPartyElementType.setSharedInd(true);

    return otherPartyElementType;
  }

  public static OtherPartyOrgType buildOtherPartyOrgType() {
    OtherPartyOrgType otherPartyOrgType = new OtherPartyOrgType();
    otherPartyOrgType.setAddress(buildAddress());
    otherPartyOrgType.setOtherInformation("otherinf");
    otherPartyOrgType.setContactDetails(buildContactDetails());
    otherPartyOrgType.setOrganizationType("orgtype");
    otherPartyOrgType.setContactName("name");
    otherPartyOrgType.setCurrentlyTrading("N");
    otherPartyOrgType.setOrganizationName("name");
    otherPartyOrgType.setRelationToCase("relation");
    otherPartyOrgType.setRelationToClient("toclient");
    return otherPartyOrgType;
  }

  public static OtherPartyPersonType buildOtherPartyPersonType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    OtherPartyPersonType otherPartyPersonType = new OtherPartyPersonType();
    otherPartyPersonType.setAddress(buildAddress());
    otherPartyPersonType.setContactDetails(buildContactDetails());
    otherPartyPersonType.setOtherInformation("otherinf");
    otherPartyPersonType.setName(buildName());
    otherPartyPersonType.setContactName("name");
    otherPartyPersonType.setDateOfBirth(datatypeFactory.newXMLGregorianCalendar());
    otherPartyPersonType.setAssessedAsstes(BigDecimal.TEN);
    otherPartyPersonType.setAssessedIncome(BigDecimal.TEN);
    otherPartyPersonType.setAssessedIncomeFrequency("freq");
    otherPartyPersonType.setAssessmentDate(datatypeFactory.newXMLGregorianCalendar());
    return otherPartyPersonType;
  }

  public static Name buildName() {
    Name name = new Name();
    name.setFirstName("firstName");
    name.setFullName("fullname");
    name.setMiddleName("middle");
    name.setSurname("surname");
    name.setTitle("mr");
    name.setSurnameAtBirth("atbirth");

    return name;
  }

  public static ContactDetails buildContactDetails() {
    ContactDetails contactDetails = new ContactDetails();
    contactDetails.setFax("faxnum");
    contactDetails.setEmailAddress("email");
    contactDetails.setMobileNumber("mobile");
    contactDetails.setTelephoneHome("telhome");
    contactDetails.setTelephoneWork("telwork");

    return contactDetails;
  }

  public static ExtResourceElementType buildExtResourceElementType() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    ExtResourceElementType extResourceElementType = new ExtResourceElementType();
    extResourceElementType.setExternalResourceType("exttype");
    extResourceElementType.setExternalResourceRef("extref");
    extResourceElementType.setChambers("chambers");
    extResourceElementType.setLocation("loc");
    extResourceElementType.setCostCeiling(new CostLimitations());
    extResourceElementType
        .getCostCeiling()
        .getCostLimitation()
        .add(buildCostLimitationElementType());
    extResourceElementType.setDateInstructed(datatypeFactory.newXMLGregorianCalendar());

    return extResourceElementType;
  }

  public static CostLimitationElementType buildCostLimitationElementType() {
    CostLimitationElementType costLimitationElementType = new CostLimitationElementType();
    costLimitationElementType.setAmount(BigDecimal.TEN);
    costLimitationElementType.setCostLimitID("limitid");
    costLimitationElementType.setCostCategory("costcat");
    costLimitationElementType.setPaidToDate(BigDecimal.TEN);
    costLimitationElementType.setBillingProviderID("provid");
    costLimitationElementType.setBillingProviderName("provname");

    return costLimitationElementType;
  }

  public static CategoryOfLawElementType buildCategoryOfLawElementType() {
    CategoryOfLawElementType categoryOfLawElementType = new CategoryOfLawElementType();
    categoryOfLawElementType.setCategoryOfLawCode("catcode");
    categoryOfLawElementType.setCategoryOfLawDescription("catdesc");
    categoryOfLawElementType.setCostLimitations(new CostLimitations());
    categoryOfLawElementType
        .getCostLimitations()
        .getCostLimitation()
        .add(buildCostLimitationElementType());
    categoryOfLawElementType.setGrantedAmount(BigDecimal.TEN);
    categoryOfLawElementType.setRequestedAmount(BigDecimal.TEN);
    categoryOfLawElementType.setTotalPaidToDate(BigDecimal.TEN);

    return categoryOfLawElementType;
  }

  public static ProviderDetails buildProviderDetails() {
    ProviderDetails providerDetails = new ProviderDetails();
    providerDetails.setContactDetails(buildContactDetails());
    providerDetails.setProviderFirmID("firmid");
    providerDetails.setProviderOfficeID("officeid");
    providerDetails.setProviderCaseReferenceNumber("caseref");
    providerDetails.setContactUserID(buildUser());
    providerDetails.setFeeEarnerContactID("feeearner");
    providerDetails.setSupervisorContactID("superid");

    return providerDetails;
  }

  public static LARDetails buildLARDetails() {
    LARDetails larDetails = new LARDetails();
    larDetails.setLARScopeFlag(Boolean.TRUE);
    larDetails.setLegalHelpUFN("ufn");
    larDetails.setLegalHelpOfficeCode("offcode");

    return larDetails;
  }
}
