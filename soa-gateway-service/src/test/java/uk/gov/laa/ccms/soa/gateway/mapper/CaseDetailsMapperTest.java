package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildApplicationDetails;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildAwardElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildCase;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildCaseDocsElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildCaseList;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildCategoryOfLawElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildContactDetails;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildCostAwardElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildCostLimitationElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildExtResourceElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildFinancialAwardElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildLARDetails;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildLandAwardElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildLinkedCaseType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildOtherAssetElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildOtherPartyElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildOtherPartyPersonType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildOutcomeDetailElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildPriorAuthorityElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildProceedingElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildProviderDetails;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildRecoveryAmountElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildScopeLimitationElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildTimeRelatedElementType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.ApplicationDetails;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentResult;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentScreen;
import uk.gov.laa.ccms.soa.gateway.model.Award;
import uk.gov.laa.ccms.soa.gateway.model.BaseClient;
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
import uk.gov.laa.ccms.soa.gateway.model.NameDetail;
import uk.gov.laa.ccms.soa.gateway.model.OfferedAmount;
import uk.gov.laa.ccms.soa.gateway.model.OpaAttribute;
import uk.gov.laa.ccms.soa.gateway.model.OpaEntity;
import uk.gov.laa.ccms.soa.gateway.model.OpaGoal;
import uk.gov.laa.ccms.soa.gateway.model.OpaInstance;
import uk.gov.laa.ccms.soa.gateway.model.OtherAsset;
import uk.gov.laa.ccms.soa.gateway.model.OtherParty;
import uk.gov.laa.ccms.soa.gateway.model.OtherPartyOrganisation;
import uk.gov.laa.ccms.soa.gateway.model.OtherPartyPerson;
import uk.gov.laa.ccms.soa.gateway.model.OutcomeDetail;
import uk.gov.laa.ccms.soa.gateway.model.PriorAuthority;
import uk.gov.laa.ccms.soa.gateway.model.ProceedingDetail;
import uk.gov.laa.ccms.soa.gateway.model.ProviderDetail;
import uk.gov.laa.ccms.soa.gateway.model.RecoveredAmount;
import uk.gov.laa.ccms.soa.gateway.model.Recovery;
import uk.gov.laa.ccms.soa.gateway.model.RecoveryAmount;
import uk.gov.laa.ccms.soa.gateway.model.ScopeLimitation;
import uk.gov.laa.ccms.soa.gateway.model.ServiceAddress;
import uk.gov.laa.ccms.soa.gateway.model.TimeRelatedAward;
import uk.gov.laa.ccms.soa.gateway.model.TransactionStatus;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.laa.ccms.soa.gateway.model.Valuation;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Case;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDocsElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseList;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CategoryOfLawElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Client;
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
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyOrgType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyPersonType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OutcomeDetailElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryAmountElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ScopeLimitationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ServiceAddrElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.TimeRelatedElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.Name;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAAttributesType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.Status;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;

@ExtendWith(MockitoExtension.class)
public class CaseDetailsMapperTest {

  @Mock
  CommonMapper commonMapper;

  @InjectMocks
  CaseDetailsMapperImpl caseDetailsMapper;

  @Test
  public void testToCaseDetails() {
    List<CaseSummary> caseSummaryList = Collections.singletonList(new CaseSummary());
    Page<CaseSummary> caseDetailPage = new PageImpl<>(caseSummaryList, Pageable.unpaged(),
        caseSummaryList.size());

    CaseDetails result = caseDetailsMapper.toCaseDetails(caseDetailPage);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
  }

  @Test
  public void testToCaseSummaryList() {
    CaseInqRS response = new CaseInqRS();
    response.getCaseList().add(buildCaseList());

    List<CaseSummary> result = caseDetailsMapper.toCaseSummaryList(response);

    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  public void testToCaseDetail() {
    Case sourceCase = buildCase();

    CaseDetail result = caseDetailsMapper.toCaseDetail(sourceCase);
    assertNotNull(result);
  }

  @Test
  public void testToContactDetail() {
    ContactDetails contactDetails = buildContactDetails();

    ContactDetail result = caseDetailsMapper.toContactDetail(contactDetails);

    assertNotNull(result);
    assertEquals(contactDetails.getEmailAddress(), result.getEmailAddress());
    assertEquals(contactDetails.getFax(), result.getFax());
    assertEquals(contactDetails.getMobileNumber(), result.getMobileNumber());
    assertEquals(contactDetails.getTelephoneHome(), result.getTelephoneHome());
    assertEquals(contactDetails.getTelephoneWork(), result.getTelephoneWork());
  }

  @Test
  public void testToOtherPartyPerson() {
    OtherPartyPersonType otherPartyPersonType = buildOtherPartyPersonType();

    when(commonMapper.toAddressDetail(otherPartyPersonType.getAddress())).thenReturn(
        new AddressDetail());

    OtherPartyPerson result = caseDetailsMapper.toOtherPartyPerson(otherPartyPersonType);
    assertNotNull(result);
    assertEquals(otherPartyPersonType.getAssessedIncomeFrequency(),
        result.getAssessedIncomeFrequency());

    assertNotNull(result.getAddress());

    compareContactDetails(otherPartyPersonType.getContactDetails(), result.getContactDetails());

    assertEquals(otherPartyPersonType.getAssessedIncome(), result.getAssessedIncome());
    assertEquals(otherPartyPersonType.getOtherInformation(), result.getOtherInformation());
    assertEquals(otherPartyPersonType.getAssessmentDate().toGregorianCalendar().getTime(),
        result.getAssessmentDate());
    assertEquals(otherPartyPersonType.getAssessedAsstes(), result.getAssessedAssets());
    assertEquals(otherPartyPersonType.getCertificateNumber(), result.getCertificateNumber());

    compareName(otherPartyPersonType.getName(), result.getName());

    assertEquals(otherPartyPersonType.getEmployersName(), result.getEmployersName());
    assertEquals(otherPartyPersonType.getDateOfBirth().toGregorianCalendar().getTime(),
        result.getDateOfBirth());
    assertEquals(otherPartyPersonType.getNINumber(), result.getNiNumber());
    assertEquals(otherPartyPersonType.getEmploymentStatus(), result.getEmploymentStatus());
    assertEquals(otherPartyPersonType.getContactName(), result.getContactName());
    assertEquals(otherPartyPersonType.getOrganizationName(), result.getOrganizationName());
    assertEquals(otherPartyPersonType.getOrganizationAddress(), result.getOrganizationAddress());
    assertEquals(otherPartyPersonType.getRelationToCase(), result.getRelationToCase());
    assertEquals(otherPartyPersonType.getRelationToClient(), result.getRelationToClient());
  }

  @Test
  public void testToTimeRelatedAward() {
    TimeRelatedElementType timeRelatedElementType = buildTimeRelatedElementType();

    TimeRelatedAward result = caseDetailsMapper.toTimeRelatedAward(timeRelatedElementType);

    assertNotNull(result);
    assertEquals(timeRelatedElementType.getAwardType(), result.getAwardType());
    assertEquals(timeRelatedElementType.getAmount(), result.getAmount());
    assertEquals(timeRelatedElementType.getAwardDate().toGregorianCalendar().getTime(),
        result.getAwardDate());
    assertEquals(timeRelatedElementType.getDescription(), result.getDescription());
    assertEquals(timeRelatedElementType.getAwardTrigeringEvent(), result.getAwardTriggeringEvent());
    assertEquals(timeRelatedElementType.getOtherDetails(), result.getOtherDetails());
  }

  @Test
  public void testToRecoveryAmount() {
    RecoveryAmountElementType recoveryAmountElementType = buildRecoveryAmountElementType();

    RecoveryAmount result = caseDetailsMapper.toRecoveryAmount(recoveryAmountElementType);
    assertNotNull(result);
    assertEquals(recoveryAmountElementType.getAmount(), result.getAmount());
    assertEquals(recoveryAmountElementType.getDateReceived().toGregorianCalendar().getTime(),
        result.getDateReceived());
    assertEquals(recoveryAmountElementType.getPaidToLSC(), result.getPaidToLsc());
  }

  @Test
  public void testToOutcomeDetail() {
    OutcomeDetailElementType outcomeDetailElementType = buildOutcomeDetailElementType();

    OutcomeDetail result = caseDetailsMapper.toOutcomeDetail(outcomeDetailElementType);

    assertNotNull(result);
    assertEquals(outcomeDetailElementType.getAdditionalResultInfo(),
        result.getAdditionalResultInfo());
    assertEquals(outcomeDetailElementType.getAltAcceptanceReason(),
        result.getAltAcceptanceReason());
    assertEquals(outcomeDetailElementType.getIssueDate().toGregorianCalendar().getTime(),
        result.getIssueDate());
    assertEquals(outcomeDetailElementType.getResult(), result.getResult());
    assertEquals(outcomeDetailElementType.getCourtCode(), result.getCourtCode());
    assertEquals(outcomeDetailElementType.getFinalWorkDate().toGregorianCalendar().getTime(),
        result.getFinalWorkDate());
    assertEquals(outcomeDetailElementType.getOutcomeCourtCaseNumber(),
        result.getOutcomeCourtCaseNumber());
    assertEquals(outcomeDetailElementType.getAltDisputeResolution(),
        result.getAltDisputeResolution());
    assertEquals(outcomeDetailElementType.getResolutionMethod(), result.getResolutionMethod());
    assertEquals(outcomeDetailElementType.getWiderBenifits(), result.getWiderBenefits());
    assertEquals(outcomeDetailElementType.getStageEnd(), result.getStageEnd());
  }

  @Test
  public void testToScopeLimitation() {
    ScopeLimitationElementType scopeLimitationElementType = buildScopeLimitationElementType();

    ScopeLimitation result = caseDetailsMapper.toScopeLimitation(scopeLimitationElementType);

    assertNotNull(result);
    assertEquals(scopeLimitationElementType.getScopeLimitation(), result.getScopeLimitation());
    assertEquals(scopeLimitationElementType.getScopeLimitationID(), result.getScopeLimitationId());
    assertEquals(scopeLimitationElementType.getScopeLimitationWording(),
        result.getScopeLimitationWording());
  }

  @Test
  public void testToLARDetails() {
    LARDetails larDetails = buildLARDetails();

    LarDetails result = caseDetailsMapper.toLarDetails(larDetails);

    assertNotNull(result);
    assertEquals(larDetails.getLegalHelpUFN(), result.getLegalHelpUfn());
    assertEquals(larDetails.getLegalHelpOfficeCode(), result.getLegalHelpOfficeCode());
  }

  @Test
  public void testToProviderDetail() {
    ProviderDetails providerDetails = buildProviderDetails();

    when(commonMapper.toUserDetail(providerDetails.getContactUserID()))
        .thenReturn(new UserDetail());

    ProviderDetail result = caseDetailsMapper.toProviderDetail(providerDetails);
    assertNotNull(result);

    compareContactDetails(providerDetails.getContactDetails(), result.getContactDetails());

    assertEquals(providerDetails.getProviderFirmID(), result.getProviderFirmId());
    assertEquals(providerDetails.getProviderOfficeID(), result.getProviderOfficeId());
    assertEquals(providerDetails.getProviderCaseReferenceNumber(),
        result.getProviderCaseReferenceNumber());

    assertNotNull(result.getContactUserId());

    assertEquals(providerDetails.getFeeEarnerContactID(), result.getFeeEarnerContactId());
    assertEquals(providerDetails.getSupervisorContactID(), result.getSupervisorContactId());
  }

  @Test
  public void testToOtherParty() {
    OtherPartyElementType otherPartyElementType = buildOtherPartyElementType();

    when(commonMapper.toAddressDetail(
        otherPartyElementType.getOtherPartyDetail().getPerson().getAddress()))
        .thenReturn(new AddressDetail());
    when(commonMapper.toAddressDetail(
        otherPartyElementType.getOtherPartyDetail().getOrganization().getAddress()))
        .thenReturn(new AddressDetail());

    OtherParty result = caseDetailsMapper.toOtherParty(otherPartyElementType);

    assertNotNull(result);
    assertEquals(otherPartyElementType.getOtherPartyID(), result.getOtherPartyId());

    OtherPartyPersonType otherPartyPersonType = otherPartyElementType.getOtherPartyDetail()
        .getPerson();
    OtherPartyPerson otherPartyPerson = result.getPerson();
    assertEquals(otherPartyPersonType.getOrganizationName(),
        otherPartyPerson.getOrganizationName());
    assertEquals(otherPartyPersonType.getOtherInformation(),
        otherPartyPerson.getOtherInformation());
    assertEquals(otherPartyPersonType.getNINumber(), otherPartyPerson.getNiNumber());
    assertEquals(otherPartyPersonType.getDateOfBirth().toGregorianCalendar().getTime(),
        otherPartyPerson.getDateOfBirth());
    assertEquals(otherPartyPersonType.getRelationToClient(),
        otherPartyPerson.getRelationToClient());
    assertEquals(otherPartyPersonType.getRelationToCase(), otherPartyPerson.getRelationToCase());
    assertEquals(otherPartyPersonType.getOrganizationAddress(),
        otherPartyPerson.getOrganizationAddress());
    assertEquals(otherPartyPersonType.getEmploymentStatus(),
        otherPartyPerson.getEmploymentStatus());
    assertEquals(otherPartyPersonType.getAssessedAsstes(), otherPartyPerson.getAssessedAssets());
    assertEquals(otherPartyPersonType.getAssessedIncome(), otherPartyPerson.getAssessedIncome());
    assertEquals(otherPartyPersonType.getAssessedIncomeFrequency(),
        otherPartyPerson.getAssessedIncomeFrequency());
    assertEquals(otherPartyPersonType.getAssessmentDate().toGregorianCalendar().getTime(),
        otherPartyPerson.getAssessmentDate());
    assertEquals(otherPartyPersonType.getContactName(), otherPartyPerson.getContactName());
    assertEquals(otherPartyPersonType.getEmployersName(), otherPartyPerson.getEmployersName());
    assertEquals(otherPartyPersonType.getCertificateNumber(),
        otherPartyPerson.getCertificateNumber());
    compareName(otherPartyPersonType.getName(), otherPartyPerson.getName());
    assertNotNull(otherPartyPerson.getAddress());
    compareContactDetails(otherPartyPersonType.getContactDetails(),
        otherPartyPerson.getContactDetails());

    OtherPartyOrgType otherPartyOrgType = otherPartyElementType.getOtherPartyDetail()
        .getOrganization();
    OtherPartyOrganisation otherPartyOrganisation = result.getOrganisation();
    compareContactDetails(otherPartyOrgType.getContactDetails(),
        otherPartyOrganisation.getContactDetails());
    assertEquals(otherPartyOrgType.getOtherInformation(),
        otherPartyOrganisation.getOtherInformation());
    assertEquals(otherPartyOrgType.getRelationToCase(), otherPartyOrganisation.getRelationToCase());
    assertEquals(otherPartyOrgType.getOrganizationType(),
        otherPartyOrganisation.getOrganizationType());
    assertEquals(otherPartyOrgType.getContactName(),
        otherPartyOrganisation.getContactName());
    assertEquals(otherPartyOrgType.getCurrentlyTrading(),
        otherPartyOrganisation.getCurrentlyTrading());
    assertEquals(otherPartyOrgType.getRelationToClient(),
        otherPartyOrganisation.getRelationToClient());
    assertNotNull(otherPartyOrganisation.getAddress());
    assertEquals(otherPartyOrgType.getOrganizationName(),
        otherPartyOrganisation.getOrganizationName());
  }

  @Test
  public void testToOtherAsset() {
    OtherAssetElementType otherAssetElementType = buildOtherAssetElementType();

    OtherAsset result = caseDetailsMapper.toOtherAsset(otherAssetElementType);

    assertNotNull(result);
    assertEquals(otherAssetElementType.getAwardedAmount(), result.getAwardedAmount());
    assertEquals(otherAssetElementType.getDescription(), result.getDescription());
    assertEquals(otherAssetElementType.getAwardedBy(), result.getAwardedBy());
    assertEquals(otherAssetElementType.getNoRecoveryDetails(), result.getNoRecoveryDetails());
    assertEquals(otherAssetElementType.getHeldBy().getOtherPartyID().getFirst(),
        result.getHeldBy().getFirst());
    assertEquals(otherAssetElementType.getRecovery(), result.getRecovery());
    assertEquals(otherAssetElementType.getAwardedPercentage(), result.getAwardedPercentage());
    assertEquals(otherAssetElementType.getDisputedAmount(), result.getDisputedAmount());
    assertEquals(otherAssetElementType.getDisputedPercentage(), result.getDisputedPercentage());
    assertEquals(otherAssetElementType.getOrderDate().toGregorianCalendar().getTime(),
        result.getOrderDate());

    assertEquals(otherAssetElementType.getValuation().getAmount(),
        result.getValuation().getAmount());
    assertEquals(otherAssetElementType.getValuation().getDate().toGregorianCalendar().getTime(),
        result.getValuation().getDate());
    assertEquals(otherAssetElementType.getValuation().getCriteria(),
        result.getValuation().getCriteria());

    assertEquals(otherAssetElementType.getRecoveredAmount(), result.getRecoveredAmount());
    assertEquals(otherAssetElementType.getRecoveredPercentage(), result.getRecoveredPercentage());
    assertEquals(otherAssetElementType.getStatChargeExemptReason(),
        result.getStatChargeExemptReason());
  }

  @Test
  public void testToLandAward() {
    LandAwardElementType landAwardElementType = buildLandAwardElementType();

    LandAward result = caseDetailsMapper.toLandAward(landAwardElementType);

    assertNotNull(result);
    assertEquals(landAwardElementType.getAwardedBy(), result.getAwardedBy());
    assertEquals(landAwardElementType.getAwardedPercentage(), result.getAwardedPercentage());
    assertEquals(landAwardElementType.getDescription(), result.getDescription());
    assertEquals(landAwardElementType.getRecovery(), result.getRecovery());
    assertEquals(landAwardElementType.getEquity(), result.getEquity());
    assertEquals(landAwardElementType.getDisputedPercentage(), result.getDisputedPercentage());
    assertEquals(landAwardElementType.getLandChargeRegistration(),
        result.getLandChargeRegistration());
    assertEquals(landAwardElementType.getMortgageAmountDue(), result.getMortgageAmountDue());
    assertEquals(landAwardElementType.getNoRecoveryDetails(), result.getNoRecoveryDetails());
    assertEquals(landAwardElementType.getOrderDate().toGregorianCalendar().getTime(),
        result.getOrderDate());

    assertEquals(landAwardElementType.getValuation().getCriteria(),
        result.getValuation().getCriteria());
    assertEquals(landAwardElementType.getValuation().getDate().toGregorianCalendar().getTime(),
        result.getValuation().getDate());
    assertEquals(landAwardElementType.getValuation().getAmount(),
        result.getValuation().getAmount());

    assertEquals(landAwardElementType.getTitleNo(), result.getTitleNo());
    assertEquals(landAwardElementType.getStatChargeExemptReason(),
        result.getStatChargeExemptReason());
    assertEquals(landAwardElementType.getRegistrationRef(), result.getRegistrationRef());
    assertEquals(landAwardElementType.getOtherProprietors().getOtherPartyID().getFirst(),
        result.getOtherProprietors().getFirst());

    compareServiceAddress(landAwardElementType.getPropertyAddress(), result.getPropertyAddress());
  }

  @Test
  public void testToFinancialAward() {
    FinancialAwardElementType financialAwardElementType = buildFinancialAwardElementType();

    FinancialAward result = caseDetailsMapper.toFinancialAward(financialAwardElementType);

    assertNotNull(result);
    assertEquals(financialAwardElementType.getAwardedBy(), result.getAwardedBy());

    RecoveryElementType recoveryElementType = financialAwardElementType.getRecovery();
    Recovery recovery = result.getRecovery();
    compareRecovery(recoveryElementType, recovery);

    assertEquals(recoveryElementType.getUnRecoveredAmount(), recovery.getUnRecoveredAmount());

    assertEquals(financialAwardElementType.getOrderDate().toGregorianCalendar().getTime(),
        result.getOrderDate());
    assertEquals(financialAwardElementType.getInterimAward(), result.getInterimAward());
    assertEquals(financialAwardElementType.getAwardJustifications(),
        result.getAwardJustifications());
    assertEquals(financialAwardElementType.getOtherDetails(), result.getOtherDetails());
    assertEquals(financialAwardElementType.getStatutoryChangeReason(),
        result.getStatutoryChangeReason());
    assertEquals(financialAwardElementType.getOrderDateServed().toGregorianCalendar().getTime(),
        result.getOrderDateServed());
    assertEquals(financialAwardElementType.getAmount(), result.getAmount());
    assertEquals(financialAwardElementType.getLiableParties().getOtherPartyID().getFirst(),
        result.getLiableParties().getFirst());

    compareServiceAddress(financialAwardElementType.getServiceAddress(),
        result.getServiceAddress());
  }

  @Test
  public void toCostAward() {
    CostAwardElementType costAwardElementType = buildCostAwardElementType();

    CostAward result = caseDetailsMapper.toCostAward(costAwardElementType);

    assertNotNull(result);
    assertEquals(costAwardElementType.getAwardedBy(), result.getAwardedBy());
    assertEquals(costAwardElementType.getOtherDetails(), result.getOtherDetails());
    compareRecovery(costAwardElementType.getRecovery(), result.getRecovery());

    assertEquals(costAwardElementType.getInterestAwardedRate(), result.getInterestAwardedRate());
    assertEquals(costAwardElementType.getCertificateCostRateMarket(),
        result.getCertificateCostRateMarket());
    assertEquals(costAwardElementType.getCertificateCostRateLSC(),
        result.getCertificateCostRateLsc());
    assertEquals(costAwardElementType.getInterestAwardedStartDate().toGregorianCalendar().getTime(),
        result.getInterestAwardedStartDate());
    assertEquals(costAwardElementType.getCourtAssessmentStatus(),
        result.getCourtAssessmentStatus());
    assertEquals(costAwardElementType.getPreCertificateAwardLSC(),
        result.getPreCertificateAwardLsc());
    assertEquals(costAwardElementType.getPreCertificateAwardOth(),
        result.getPreCertificateAwardOth());
    assertEquals(costAwardElementType.getOrderDateServed().toGregorianCalendar().getTime(),
        result.getOrderDateServed());
    assertEquals(costAwardElementType.getOrderDate().toGregorianCalendar().getTime(),
        result.getOrderDate());
    compareServiceAddress(costAwardElementType.getServiceAddress(), result.getServiceAddress());
    assertEquals(costAwardElementType.getLiableParties().getOtherParyID().getFirst(),
        result.getLiableParties().getFirst());
  }

  @Test
  public void testToCaseDoc() {
    CaseDocsElementType caseDocsElementType = buildCaseDocsElementType();

    CaseDoc result = caseDetailsMapper.toCaseDoc(caseDocsElementType);

    assertNotNull(result);
    assertEquals(caseDocsElementType.getCCMSDocumentID(), result.getCcmsDocumentId());
    assertEquals(caseDocsElementType.getDocumentSubject(), result.getDocumentSubject());
  }

  @Test
  public void testToAward() {
    AwardElementType awardElementType = buildAwardElementType();

    Award result = caseDetailsMapper.toAward(awardElementType);

    assertNotNull(result);
    assertEquals(awardElementType.getAwardDetails().getAwardCategory(), result.getAwardCategory());
    assertEquals(awardElementType.getAwardID(), result.getAwardId());
    assertEquals(awardElementType.getAwardType(), result.getAwardType());
  }

  @Test
  public void testToLinkedCase() {
    LinkedCaseType linkedCaseType = buildLinkedCaseType();

    LinkedCase result = caseDetailsMapper.toLinkedCase(linkedCaseType);

    assertNotNull(result);
    assertEquals(linkedCaseType.getCaseStatus(), result.getCaseStatus());
    assertEquals(linkedCaseType.getLinkType(), result.getLinkType());
    assertEquals(linkedCaseType.getCaseReferenceNumber(), result.getCaseReferenceNumber());

    assertEquals(linkedCaseType.getClient().getClientReferenceNumber(),
        result.getClient().getClientReferenceNumber());
    assertEquals(linkedCaseType.getClient().getFirstName(), result.getClient().getFirstName());
    assertEquals(linkedCaseType.getClient().getSurname(), result.getClient().getSurname());

    assertEquals(linkedCaseType.getFeeEarnerName(), result.getFeeEarnerName());
    assertEquals(linkedCaseType.getFeeEarnerID(), result.getFeeEarnerId());
    assertEquals(linkedCaseType.getCategoryOfLawCode(), result.getCategoryOfLawCode());
    assertEquals(linkedCaseType.getCategoryOfLawDesc(), result.getCategoryOfLawDesc());
    assertEquals(linkedCaseType.getProviderReferenceNumber(), result.getProviderReferenceNumber());
  }

  @Test
  public void testToPriorAuthority() {
    PriorAuthorityElementType priorAuthorityElementType = buildPriorAuthorityElementType();

    PriorAuthority result = caseDetailsMapper.toPriorAuthority(priorAuthorityElementType);

    assertNotNull(result);
    assertEquals(priorAuthorityElementType.getPriorAuthorityType(), result.getPriorAuthorityType());
    assertEquals(priorAuthorityElementType.getDetails().getAttribute().getFirst().getName(),
        result.getDetails().getFirst().getName());
    assertEquals(priorAuthorityElementType.getDetails().getAttribute().getFirst().getValue(),
        result.getDetails().getFirst().getValue());
    assertEquals(priorAuthorityElementType.getDescription(), result.getDescription());
    assertEquals(priorAuthorityElementType.getAssessedAmount(), result.getAssessedAmount());
    assertEquals(priorAuthorityElementType.getDecisionStatus(), result.getDecisionStatus());
    assertEquals(priorAuthorityElementType.getReasonForRequest(), result.getReasonForRequest());
    assertEquals(priorAuthorityElementType.getRequestAmount(), result.getRequestAmount());
  }

  @Test
  public void testToProceedingDetail() {
    ProceedingElementType proceedingElementType = buildProceedingElementType();

    ProceedingDetail result = caseDetailsMapper.toProceedingDetail(proceedingElementType);

    assertNotNull(result);
    assertEquals(proceedingElementType.getProceedingDetails().getProceedingType(),
        result.getProceedingType());
    assertEquals(proceedingElementType.getProceedingDetails().getMatterType(),
        result.getMatterType());
    assertEquals(proceedingElementType.getProceedingDetails().getOrderType(),
        result.getOrderType());
    assertEquals(proceedingElementType.getProceedingDetails().getProceedingDescription(),
        result.getProceedingDescription());
    assertEquals(proceedingElementType.getProceedingDetails().getClientInvolvementType(),
        result.getClientInvolvementType());
    assertEquals(
        proceedingElementType.getProceedingDetails().getDateCostsValid().toGregorianCalendar()
            .getTime(), result.getDateCostsValid());
    assertEquals(proceedingElementType.getProceedingDetails().getDateDevolvedPowersUsed()
        .toGregorianCalendar().getTime(), result.getDateDevolvedPowersUsed());
    assertEquals(proceedingElementType.getProceedingDetails().getDateGranted().toGregorianCalendar()
        .getTime(), result.getDateGranted());
    assertEquals(proceedingElementType.getProceedingDetails().getLevelOfService(),
        result.getLevelOfService());
    assertEquals(proceedingElementType.getProceedingDetails().getScopeLimitationApplied(),
        result.getScopeLimitationApplied());
    assertEquals(proceedingElementType.getProceedingDetails().getStage(), result.getStage());
  }

  @Test
  public void testToExternalResource() {
    ExtResourceElementType extResourceElementType = buildExtResourceElementType();

    ExternalResource result = caseDetailsMapper.toExternalResource(extResourceElementType);

    assertNotNull(result);
    assertEquals(extResourceElementType.getExternalResourceType(),
        result.getExternalResourceType());
    assertEquals(extResourceElementType.getExternalResourceRef(), result.getExternalResourceRef());
    assertEquals(extResourceElementType.getChambers(), result.getChambers());
    assertEquals(extResourceElementType.getLocation(), result.getLocation());
    assertEquals(extResourceElementType.getDateInstructed().toGregorianCalendar().getTime(),
        result.getDateInstructed());
  }

  @Test
  public void testToCategoryOfLaw() {
    CategoryOfLawElementType categoryOfLawElementType = buildCategoryOfLawElementType();

    CategoryOfLaw result = caseDetailsMapper.toCategoryOfLaw(categoryOfLawElementType);

    assertNotNull(result);
    assertEquals(categoryOfLawElementType.getCategoryOfLawCode(), result.getCategoryOfLawCode());
    assertEquals(categoryOfLawElementType.getCategoryOfLawDescription(),
        result.getCategoryOfLawDescription());
    assertEquals(categoryOfLawElementType.getGrantedAmount(), result.getGrantedAmount());
    assertEquals(categoryOfLawElementType.getRequestedAmount(), result.getRequestedAmount());
    assertEquals(categoryOfLawElementType.getTotalPaidToDate(), result.getTotalPaidToDate());
  }

  @Test
  public void testToCostLimitation() {
    CostLimitationElementType costLimitationElementType = buildCostLimitationElementType();

    CostLimitation result = caseDetailsMapper.toCostLimitation(costLimitationElementType);

    assertEquals(costLimitationElementType.getCostLimitID(), result.getCostLimitId());
    assertEquals(costLimitationElementType.getCostCategory(), result.getCostCategory());
    assertEquals(costLimitationElementType.getAmount(), result.getAmount());
    assertEquals(costLimitationElementType.getPaidToDate(), result.getPaidToDate());
    assertEquals(costLimitationElementType.getBillingProviderName(),
        result.getBillingProviderName());
    assertEquals(costLimitationElementType.getBillingProviderID(), result.getBillingProviderId());
  }

  @Test
  public void testToApplicationDetails() {
    uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails applicationDetails = buildApplicationDetails();

    ApplicationDetails result = caseDetailsMapper.toApplicationDetails(applicationDetails);

    assertNotNull(result);
    assertEquals(applicationDetails.getApplicationAmendmentType(),
        result.getApplicationAmendmentType());
    assertEquals(applicationDetails.getPurposeOfApplication(), result.getPurposeOfApplication());
    assertEquals(applicationDetails.getCertificateType(), result.getCertificateType());
    assertEquals(applicationDetails.getDateOfFirstAttendance().toGregorianCalendar().getTime(),
        result.getDateOfFirstAttendance());
    assertEquals(applicationDetails.getDateOfHearing().toGregorianCalendar().getTime(),
        result.getDateOfHearing());
    assertEquals(applicationDetails.getDevolvedPowersDate().toGregorianCalendar().getTime(),
        result.getDevolvedPowersDate());
    assertEquals(applicationDetails.getPreferredAddress(), result.getPreferredAddress());

    compareClients(applicationDetails.getClient(), result.getClient());
  }



  @Test
  void toTransactionStatus() {
    CaseAddUpdtStatusRS caseAddUpdtStatusRS = new CaseAddUpdtStatusRS();
    caseAddUpdtStatusRS.setCaseReferenceNumber("ref1");
    caseAddUpdtStatusRS.setHeaderRS(new HeaderRSType());
    caseAddUpdtStatusRS.getHeaderRS().setStatus(new Status());
    caseAddUpdtStatusRS.getHeaderRS().getStatus().setStatus(StatusTextType.ERROR);

    TransactionStatus result = caseDetailsMapper.toTransactionStatus(caseAddUpdtStatusRS);

    assertNotNull(result);
    assertEquals(caseAddUpdtStatusRS.getCaseReferenceNumber(), result.getReferenceNumber());
    assertEquals(caseAddUpdtStatusRS.getHeaderRS().getStatus().getStatus().name(),
        result.getSubmissionStatus());
  }


  private void compareRecovery(RecoveryElementType recoveryElementType, Recovery recovery) {
    assertEquals(recoveryElementType.getAwardValue(), recovery.getAwardValue());
    assertEquals(recoveryElementType.getOfferedAmount().getAmount(),
        recovery.getOfferedAmount().getAmount());
    assertEquals(
        recoveryElementType.getOfferedAmount().getConditionsOfOffer(),
        recovery.getOfferedAmount().getConditionsOfOffer());

    compareRecoveryAmount(
        recoveryElementType.getRecoveredAmount().getClient(),
        recovery.getRecoveredAmount().getClient());
    compareRecoveryAmount(
        recoveryElementType.getRecoveredAmount().getCourt(),
        recovery.getRecoveredAmount().getCourt());
    compareRecoveryAmount(
        recoveryElementType.getRecoveredAmount().getSolicitor(),
        recovery.getRecoveredAmount().getSolicitor());
  }

  private void compareRecoveryAmount(RecoveryAmountElementType recoveryAmountElementType,
      RecoveryAmount recoveryAmount) {
    assertEquals(recoveryAmountElementType.getPaidToLSC(), recoveryAmount.getPaidToLsc());
    assertEquals(recoveryAmountElementType.getAmount(), recoveryAmount.getAmount());
    assertEquals(recoveryAmountElementType.getDateReceived().toGregorianCalendar().getTime(),
        recoveryAmount.getDateReceived());
  }

  private void compareServiceAddress(ServiceAddrElementType serviceAddrElementType,
      ServiceAddress serviceAddress) {
    assertEquals(serviceAddrElementType.getAddressLine1(), serviceAddress.getAddressLine1());
    assertEquals(serviceAddrElementType.getAddressLine2(), serviceAddress.getAddressLine2());
    assertEquals(serviceAddrElementType.getAddressLine3(), serviceAddress.getAddressLine3());
  }

  private void compareName(Name name, NameDetail nameDetail) {
    assertNotNull(nameDetail);
    assertEquals(name.getFirstName(), nameDetail.getFirstName());
    assertEquals(name.getFullName(), nameDetail.getFullName());
    assertEquals(name.getMiddleName(), nameDetail.getMiddleName());
    assertEquals(name.getSurname(), nameDetail.getSurname());
    assertEquals(name.getTitle(), nameDetail.getTitle());
    assertEquals(name.getSurnameAtBirth(), nameDetail.getSurnameAtBirth());
  }

  private void compareContactDetails(ContactDetails contactDetails, ContactDetail contactDetail) {
    assertNotNull(contactDetail);
    assertEquals(contactDetails.getTelephoneWork(), contactDetail.getTelephoneWork());
    assertEquals(contactDetails.getEmailAddress(), contactDetail.getEmailAddress());
    assertEquals(contactDetails.getFax(), contactDetail.getFax());
    assertEquals(contactDetails.getTelephoneHome(), contactDetail.getTelephoneHome());
    assertEquals(contactDetails.getMobileNumber(), contactDetail.getMobileNumber());
  }

  @Test
  public void testToCaseSummary() {
    CaseList caseList = buildCaseList();

    CaseSummary result = caseDetailsMapper.toCaseSummary(caseList);

    assertNotNull(result);
    assertEquals(caseList.getCaseReferenceNumber(), result.getCaseReferenceNumber());
    assertEquals(caseList.getProviderCaseReferenceNumber(),
        result.getProviderCaseReferenceNumber());
    compareClients(caseList.getClient(), result.getClient());
    assertEquals(caseList.getDisplayStatus(), result.getCaseStatusDisplay());
    assertEquals(caseList.getCategoryOfLaw(), result.getCategoryOfLaw());
    assertEquals(caseList.getFeeEarnerName(), result.getFeeEarnerName());
  }

  private void compareClients(Client client, BaseClient baseClient) {
    assertNotNull(baseClient);
    assertEquals(client.getClientReferenceNumber(), baseClient.getClientReferenceNumber());
    assertEquals(client.getFirstName(), baseClient.getFirstName());
    assertEquals(client.getSurname(), baseClient.getSurname());
  }

  @Test
  public void testToOpaAttributesType() {
    OpaAttribute opaAttribute = new OpaAttribute()
        .attribute("attr")
        .caption("capt")
        .userDefinedInd(Boolean.TRUE)
        .responseText("rText")
        .responseType("rType")
        .responseValue("rValue");

    OPAAttributesType result = caseDetailsMapper.toOpaAttributesType(opaAttribute);

    assertNotNull(result);
    assertEquals(opaAttribute.getAttribute(), result.getAttribute());
    assertEquals(opaAttribute.getCaption(), result.getCaption());
    assertEquals(opaAttribute.getResponseText(), result.getResponseText());
    assertEquals(opaAttribute.getResponseType(), result.getResponseType());
    assertEquals(opaAttribute.getResponseValue(), result.getResponseValue());
  }

  @Test
  public void testToAssesmentResultType() {
    AssessmentResult assessmentResult = new AssessmentResult()
        .addAssessmentDetailsItem(new AssessmentScreen()
            .screenName("screen1")
            .caption("capt")
            .addEntityItem(new OpaEntity()
                .caption("entCapt")
                .entityName("entName")
                .addInstancesItem(new OpaInstance()
                    .addAttributesItem(new OpaAttribute())
                    .caption("instCapt")
                    .instanceLabel("instLab"))
                .sequenceNumber(1)))
        .assessmentId("assId")
        .date(new Date())
        .defaultInd(Boolean.TRUE)
        .addResultsItem(new OpaGoal()
            .attribute("att1")
            .attributeValue("val"));

    AssesmentResultType result = caseDetailsMapper.toAssesmentResultType(assessmentResult);

    assertNotNull(result);
    assertEquals(assessmentResult.getAssessmentId(), result.getAssesmentID());
    assertNotNull(result.getAssesmentDetails());
    assertNotNull(result.getAssesmentDetails().getAssessmentScreens());
    assertEquals(1, result.getAssesmentDetails().getAssessmentScreens().size());
    assertEquals(assessmentResult.getAssessmentDetails().getFirst().getCaption(), result.getAssesmentDetails().getAssessmentScreens().getFirst().getCaption());
    assertEquals(assessmentResult.getAssessmentDetails().getFirst().getScreenName(), result.getAssesmentDetails().getAssessmentScreens().getFirst().getScreenName());

    assertNotNull(result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity());
    assertEquals(1, result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity().size());
    assertEquals(assessmentResult.getAssessmentDetails().getFirst().getEntity().getFirst().getCaption(), result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity().getFirst().getCaption());
    assertEquals(assessmentResult.getAssessmentDetails().getFirst().getEntity().getFirst().getEntityName(), result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity().getFirst().getEntityName());
    assertEquals(assessmentResult.getAssessmentDetails().getFirst().getEntity().getFirst().getSequenceNumber(), result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity().getFirst().getSequenceNumber().intValue());

    assertNotNull(result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity().getFirst().getInstances());
    assertEquals(1, result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity().getFirst().getInstances().size());
    assertNotNull(result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity().getFirst().getInstances().getFirst().getAttributes());
    assertEquals(assessmentResult.getAssessmentDetails().getFirst().getEntity().getFirst().getInstances().getFirst().getCaption(),
        result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity().getFirst().getInstances().getFirst().getCaption());
    assertEquals(assessmentResult.getAssessmentDetails().getFirst().getEntity().getFirst().getInstances().getFirst().getInstanceLabel(),
        result.getAssesmentDetails().getAssessmentScreens().getFirst().getEntity().getFirst().getInstances().getFirst().getInstanceLabel());

  }

  @Test
  public void testToContactDetails() {
    ContactDetail contactDetail = new ContactDetail()
        .correspondenceLanguage("lang")
        .correspondenceMethod("meth")
        .emailAddress("email")
        .fax("fax")
        .mobileNumber("mob")
        .password("pass")
        .passwordReminder("rem")
        .telephoneHome("telhome")
        .telephoneWork("telwork");

    ContactDetails result = caseDetailsMapper.toContactDetails(contactDetail);

    assertNotNull(result);
    assertEquals(contactDetail.getEmailAddress(), result.getEmailAddress());
    assertEquals(contactDetail.getFax(), result.getFax());
    assertEquals(contactDetail.getMobileNumber(), result.getMobileNumber());
    assertEquals(contactDetail.getTelephoneHome(), result.getTelephoneHome());
    assertEquals(contactDetail.getTelephoneWork(), result.getTelephoneWork());
  }

  @Test
  public void testToOtherPartyPersonType() {
    OtherPartyPerson otherPartyPerson = new OtherPartyPerson()
        .address(new AddressDetail()
            .addressId("1")
            .addressLine1("add1")
            .addressLine2("add2")
            .addressLine3("add3")
            .addressLine4("add4")
            .careOfName("cof")
            .city("cty")
            .country("thecountry")
            .county("thecounty")
            .house("thehouse")
            .postalCode("postal")
            .province("prov")
            .state("thestate"))
        .assessedAssets(BigDecimal.ONE)
        .assessedIncome(BigDecimal.TEN)
        .assessedIncomeFrequency("freq")
        .assessmentDate(new Date())
        .certificateNumber("certnum")
        .contactDetails(new ContactDetail()) // tested elsewhere
        .contactName("name")
        .courtOrderedMeansAssesment(Boolean.TRUE)
        .dateOfBirth(new Date())
        .employersName("empname")
        .employmentStatus("empstat")
        .name(new NameDetail()
            .firstName("1stname")
            .fullName("fullname")
            .middleName("midname")
            .surname("sur")
            .surnameAtBirth("birth")
            .title("ttl"))
        .niNumber("ni")
        .organizationAddress("orgaddr")
        .organizationName("orgname")
        .otherInformation("otherinf")
        .partyLegalAidedInd(Boolean.FALSE)
        .publicFundingAppliedInd(Boolean.TRUE)
        .relationToCase("rel2case")
        .relationToClient("rel2client");

    when(commonMapper.toAddress(otherPartyPerson.getAddress())).thenReturn(new Address());

    OtherPartyPersonType result = caseDetailsMapper.toOtherPartyPersonType(otherPartyPerson);

    assertNotNull(result);
    assertNotNull(result.getAddress());
    assertEquals(otherPartyPerson.getAssessedAssets(), result.getAssessedAsstes());
    assertEquals(otherPartyPerson.getAssessedIncome(), result.getAssessedIncome());
    assertEquals(otherPartyPerson.getAssessedIncomeFrequency(), result.getAssessedIncomeFrequency());
    assertEquals(otherPartyPerson.getAssessmentDate(), result.getAssessmentDate().toGregorianCalendar().getTime());
    assertEquals(otherPartyPerson.getCertificateNumber(), result.getCertificateNumber());
    assertNotNull(result.getContactDetails());
    assertEquals(otherPartyPerson.getContactName(), result.getContactName());
    assertEquals(otherPartyPerson.getDateOfBirth(), result.getDateOfBirth().toGregorianCalendar().getTime());
    assertEquals(otherPartyPerson.getEmployersName(), result.getEmployersName());
    assertEquals(otherPartyPerson.getEmploymentStatus(), result.getEmploymentStatus());
    compareName(otherPartyPerson.getName(), result.getName());
    assertEquals(otherPartyPerson.getNiNumber(), result.getNINumber());
    assertEquals(otherPartyPerson.getOrganizationAddress(), result.getOrganizationAddress());
    assertEquals(otherPartyPerson.getOrganizationName(), result.getOrganizationName());
    assertEquals(otherPartyPerson.getOtherInformation(), result.getOtherInformation());
    assertEquals(otherPartyPerson.getRelationToCase(), result.getRelationToCase());
    assertEquals(otherPartyPerson.isCourtOrderedMeansAssesment(), result.isCourtOrderedMeansAssesment());
    assertEquals(otherPartyPerson.isPartyLegalAidedInd(), result.isPartyLegalAidedInd());
    assertEquals(otherPartyPerson.isPublicFundingAppliedInd(), result.isPublicFundingAppliedInd());
  }

  @Test
  public void testToTimeRelatedElementType() {
    TimeRelatedAward timeRelatedAward = new TimeRelatedAward()
        .amount(BigDecimal.ONE)
        .awardDate(new Date())
        .awardTriggeringEvent("event")
        .awardType("type")
        .description("descr")
        .otherDetails("otherdets");

    TimeRelatedElementType result = caseDetailsMapper.toTimeRelatedElementType(timeRelatedAward);

    assertNotNull(result);
    assertEquals(timeRelatedAward.getAmount(), result.getAmount());
    assertEquals(timeRelatedAward.getAwardDate(), result.getAwardDate().toGregorianCalendar().getTime());
    assertEquals(timeRelatedAward.getAwardTriggeringEvent(), result.getAwardTrigeringEvent());
    assertEquals(timeRelatedAward.getAwardType(), result.getAwardType());
    assertEquals(timeRelatedAward.getDescription(), result.getDescription());
    assertEquals(timeRelatedAward.getOtherDetails(), result.getOtherDetails());
  }

  @Test
  public void testToRecoveryAmountElementType() {
    RecoveryAmount recoveryAmount = new RecoveryAmount()
        .amount(BigDecimal.ONE)
        .dateReceived(new Date())
        .paidToLsc(BigDecimal.TEN);

    RecoveryAmountElementType result = caseDetailsMapper.toRecoveryAmountElementType(recoveryAmount);

    assertNotNull(result);
    assertEquals(recoveryAmount.getAmount(), result.getAmount());
    assertEquals(recoveryAmount.getDateReceived(), result.getDateReceived().toGregorianCalendar().getTime());
    assertEquals(recoveryAmount.getPaidToLsc(), result.getPaidToLSC());
  }

  @Test
  public void testToOutcomeDetailElementType() {
    OutcomeDetail outcomeDetail = new OutcomeDetail()
        .additionalResultInfo("res")
        .altAcceptanceReason("altRes")
        .altDisputeResolution("altDisp")
        .courtCode("code")
        .finalWorkDate(new Date())
        .issueDate(new Date())
        .outcomeCourtCaseNumber("num")
        .resolutionMethod("meth")
        .result("res")
        .stageEnd("end")
        .widerBenefits("widerBen");

    OutcomeDetailElementType result = caseDetailsMapper.toOutcomeDetailElementType(outcomeDetail);

    assertNotNull(result);
    assertEquals(outcomeDetail.getAdditionalResultInfo(), result.getAdditionalResultInfo());
    assertEquals(outcomeDetail.getAltAcceptanceReason(), result.getAltAcceptanceReason());
    assertEquals(outcomeDetail.getAltDisputeResolution(), result.getAltDisputeResolution());
    assertEquals(outcomeDetail.getCourtCode(), result.getCourtCode());
    assertEquals(outcomeDetail.getFinalWorkDate(), result.getFinalWorkDate().toGregorianCalendar().getTime());
    assertEquals(outcomeDetail.getIssueDate(), result.getIssueDate().toGregorianCalendar().getTime());
    assertEquals(outcomeDetail.getOutcomeCourtCaseNumber(), result.getOutcomeCourtCaseNumber());
    assertEquals(outcomeDetail.getResolutionMethod(), result.getResolutionMethod());
    assertEquals(outcomeDetail.getResult(), result.getResult());
    assertEquals(outcomeDetail.getStageEnd(), result.getStageEnd());
    assertEquals(outcomeDetail.getWiderBenefits(), result.getWiderBenifits());
  }

  @Test
  public void testToScopeLimitationElementType() {
    ScopeLimitation scopeLimitation = new ScopeLimitation()
        .delegatedFunctionsApply(Boolean.TRUE)
        .scopeLimitation("scopelim")
        .scopeLimitationId("id")
        .scopeLimitationWording("word");

    ScopeLimitationElementType result = caseDetailsMapper.toScopeLimitationElementType(scopeLimitation);

    assertNotNull(result);
    assertEquals(scopeLimitation.getScopeLimitation(), result.getScopeLimitation());
    assertEquals(scopeLimitation.getScopeLimitationId(), result.getScopeLimitationID());
    assertEquals(scopeLimitation.getScopeLimitationWording(), result.getScopeLimitationWording());
  }

  @Test
  public void testToLarDetails() {
    LarDetails larDetails = new LarDetails()
        .larScopeFlag(Boolean.TRUE)
        .legalHelpOfficeCode("code")
        .legalHelpUfn("ufn");

    LARDetails result = caseDetailsMapper.toLarDetails(larDetails);

    assertNotNull(result);
    assertEquals(larDetails.getLegalHelpOfficeCode(), result.getLegalHelpOfficeCode());
    assertEquals(larDetails.getLegalHelpUfn(), result.getLegalHelpUFN());
    assertEquals(larDetails.isLarScopeFlag(), result.isLARScopeFlag());
  }

  @Test
  public void testToProviderDetails() {
    ProviderDetail providerDetail = new ProviderDetail()
        .contactDetails(new ContactDetail()) // tested elsewhere
        .contactUserId(new UserDetail()) // common mapper
        .feeEarnerContactId("feeid")
        .providerCaseReferenceNumber("provcaseref")
        .providerFirmId("provfirmid")
        .providerOfficeId("provoffid")
        .supervisorContactId("supcontid");

    when(commonMapper.toUser(providerDetail.getContactUserId())).thenReturn(new User());

    ProviderDetails result = caseDetailsMapper.toProviderDetails(providerDetail);

    assertNotNull(result);
    assertNotNull(result.getContactDetails());
    assertNotNull(result.getContactUserID());
    assertEquals(providerDetail.getFeeEarnerContactId(), result.getFeeEarnerContactID());
    assertEquals(providerDetail.getProviderCaseReferenceNumber(), result.getProviderCaseReferenceNumber());
    assertEquals(providerDetail.getProviderFirmId(), result.getProviderFirmID());
    assertEquals(providerDetail.getProviderOfficeId(), result.getProviderOfficeID());
    assertEquals(providerDetail.getSupervisorContactId(), result.getSupervisorContactID());
  }

  @Test
  public void testToOtherPartyElementType() {
    OtherParty otherParty = new OtherParty()
        .organisation(new OtherPartyOrganisation()
            .address(new AddressDetail()) // common mapper
            .contactDetails(new ContactDetail()) // tested elsewhere
            .contactName("orgconname")
            .currentlyTrading("curTrad")
            .organizationName("orgname")
            .organizationType("orgtype")
            .otherInformation("orgotherinf")
            .relationToCase("orgrel2case")
            .relationToClient("orgrel2client"))
        .otherPartyId("otherid")
        .person(new OtherPartyPerson() // tested elsewhere
            .address(new AddressDetail()))
        .sharedInd(Boolean.TRUE);

    when(commonMapper.toAddress(any(AddressDetail.class))).thenReturn(new Address());

    OtherPartyElementType result = caseDetailsMapper.toOtherPartyElementType(otherParty);

    assertNotNull(result);
    assertEquals(otherParty.getOtherPartyId(), result.getOtherPartyID());
    assertEquals(otherParty.isSharedInd(), result.isSharedInd());

    assertNotNull(result.getOtherPartyDetail());
    assertNotNull(result.getOtherPartyDetail().getPerson());

    OtherPartyOrgType resultOrg = result.getOtherPartyDetail().getOrganization();
    assertNotNull(resultOrg);
    assertNotNull(resultOrg.getAddress());
    assertNotNull(resultOrg.getContactDetails());
    assertEquals(otherParty.getOrganisation().getContactName(), resultOrg.getContactName());
    assertEquals(otherParty.getOrganisation().getCurrentlyTrading(), resultOrg.getCurrentlyTrading());
    assertEquals(otherParty.getOrganisation().getOrganizationName(), resultOrg.getOrganizationName());
    assertEquals(otherParty.getOrganisation().getOrganizationType(), resultOrg.getOrganizationType());
    assertEquals(otherParty.getOrganisation().getOtherInformation(), resultOrg.getOtherInformation());
    assertEquals(otherParty.getOrganisation().getRelationToCase(), resultOrg.getRelationToCase());
    assertEquals(otherParty.getOrganisation().getRelationToClient(), resultOrg.getRelationToClient());
  }

  @Test
  public void testToOtherAssetElementType() {
    OtherAsset otherAsset = new OtherAsset()
        .awardedAmount(BigDecimal.TEN)
        .awardedBy("awby")
        .awardedPercentage(BigDecimal.ONE)
        .description("descr")
        .disputedAmount(BigDecimal.TWO)
        .disputedPercentage(BigDecimal.ZERO)
        .addHeldByItem("hb")
        .noRecoveryDetails("norec")
        .orderDate(new Date())
        .recoveredAmount(BigDecimal.ONE)
        .recoveredPercentage(BigDecimal.TWO)
        .recovery("recov")
        .statChargeExemptReason("scer")
        .timeRelatedAward(new TimeRelatedAward()) // tested elsewhere
        .valuation(new Valuation()
            .amount(BigDecimal.ONE)
            .criteria("valcrit")
            .date(new Date()));

    OtherAssetElementType result = caseDetailsMapper.toOtherAssetElementType(otherAsset);

    assertNotNull(result);
    assertEquals(otherAsset.getAwardedAmount(), result.getAwardedAmount());
    assertEquals(otherAsset.getAwardedBy(), result.getAwardedBy());
    assertEquals(otherAsset.getAwardedPercentage(), result.getAwardedPercentage());
    assertEquals(otherAsset.getDescription(), result.getDescription());
    assertEquals(otherAsset.getDisputedAmount(), result.getDisputedAmount());
    assertEquals(otherAsset.getDisputedPercentage(), result.getDisputedPercentage());
    assertNotNull(result.getHeldBy());
    assertNotNull(result.getHeldBy().getOtherPartyID());
    assertEquals(1, result.getHeldBy().getOtherPartyID().size());
    assertEquals(otherAsset.getHeldBy().getFirst(), result.getHeldBy().getOtherPartyID().getFirst());
    assertEquals(otherAsset.getNoRecoveryDetails(), result.getNoRecoveryDetails());
    assertEquals(otherAsset.getOrderDate(), result.getOrderDate().toGregorianCalendar().getTime());
    assertEquals(otherAsset.getRecoveredAmount(), result.getRecoveredAmount());
    assertEquals(otherAsset.getRecoveredPercentage(), result.getRecoveredPercentage());
    assertEquals(otherAsset.getRecovery(), result.getRecovery());
    assertEquals(otherAsset.getStatChargeExemptReason(), result.getStatChargeExemptReason());
    assertNotNull(result.getTimeRelatedAward());
    assertNotNull(result.getValuation());
    assertEquals(otherAsset.getValuation().getAmount(), result.getValuation().getAmount());
    assertEquals(otherAsset.getValuation().getCriteria(), result.getValuation().getCriteria());
    assertEquals(otherAsset.getValuation().getDate(), result.getValuation().getDate().toGregorianCalendar().getTime());
  }

  @Test
  public void testToLandAwardElementType() {
    LandAward landAward = new LandAward()
        .awardedBy("awBy")
        .awardedPercentage(BigDecimal.ONE)
        .description("descr")
        .disputedPercentage(BigDecimal.TEN)
        .equity("eq")
        .landChargeRegistration("reg")
        .mortgageAmountDue(BigDecimal.TEN)
        .noRecoveryDetails("norec")
        .orderDate(new Date())
        .addOtherProprietorsItem("otherProp")
        .propertyAddress(new ServiceAddress()
            .addressLine1("add1")
            .addressLine2("add2")
            .addressLine3("add3"))
        .recovery("recov")
        .registrationRef("regref")
        .statChargeExemptReason("statres")
        .timeRelatedAward(new TimeRelatedAward()) // tested elsewhere
        .titleNo("title")
        .valuation(new Valuation()
                    .amount(BigDecimal.ONE)
                    .criteria("valcrit")
                    .date(new Date()));

    LandAwardElementType result = caseDetailsMapper.toLandAwardElementType(landAward);

    assertNotNull(result);
    assertEquals(landAward.getAwardedBy(), result.getAwardedBy());
    assertEquals(landAward.getAwardedPercentage(), result.getAwardedPercentage());
    assertEquals(landAward.getDescription(), result.getDescription());
    assertEquals(landAward.getDisputedPercentage(), result.getDisputedPercentage());
    assertEquals(landAward.getEquity(), result.getEquity());
    assertEquals(landAward.getLandChargeRegistration(), result.getLandChargeRegistration());
    assertEquals(landAward.getMortgageAmountDue(), result.getMortgageAmountDue());
    assertEquals(landAward.getNoRecoveryDetails(), result.getNoRecoveryDetails());
    assertEquals(landAward.getOrderDate(), result.getOrderDate().toGregorianCalendar().getTime());
    assertNotNull(result.getOtherProprietors());
    assertNotNull(result.getOtherProprietors().getOtherPartyID());
    assertEquals(1, result.getOtherProprietors().getOtherPartyID().size());
    assertEquals(landAward.getOtherProprietors().getFirst(), result.getOtherProprietors().getOtherPartyID().getFirst());
    assertNotNull(result.getPropertyAddress());
    assertEquals(landAward.getPropertyAddress().getAddressLine1(), result.getPropertyAddress().getAddressLine1());
    assertEquals(landAward.getPropertyAddress().getAddressLine2(), result.getPropertyAddress().getAddressLine2());
    assertEquals(landAward.getPropertyAddress().getAddressLine3(), result.getPropertyAddress().getAddressLine3());
    assertEquals(landAward.getRecovery(), result.getRecovery());
    assertEquals(landAward.getRegistrationRef(), result.getRegistrationRef());
    assertEquals(landAward.getStatChargeExemptReason(), result.getStatChargeExemptReason());
    assertNotNull(result.getTimeRelatedAward());
    assertEquals(landAward.getTitleNo(), result.getTitleNo());
    assertEquals(landAward.getValuation().getAmount(), result.getValuation().getAmount());
    assertEquals(landAward.getValuation().getCriteria(), result.getValuation().getCriteria());
    assertEquals(landAward.getValuation().getDate(), result.getValuation().getDate().toGregorianCalendar().getTime());
  }

  @Test
  public void testToFinancialAwardElementType() {
    FinancialAward financialAward = new FinancialAward()
        .amount(BigDecimal.ONE)
        .awardedBy("by")
        .awardJustifications("just")
        .interimAward(BigDecimal.TWO)
        .addLiablePartiesItem("party")
        .orderDate(new Date())
        .orderDateServed(new Date())
        .otherDetails("otherdets")
        .recovery(new Recovery()
            .awardValue(BigDecimal.ZERO)
            .leaveOfCourtReqdInd(Boolean.FALSE)
            .offeredAmount(new OfferedAmount()
                .amount(BigDecimal.ONE)
                .conditionsOfOffer("cond"))
            .recoveredAmount(new RecoveredAmount()) // tested elsewhere
            .unRecoveredAmount(BigDecimal.TEN))
        .serviceAddress(new ServiceAddress()
            .addressLine1("add1")
            .addressLine2("add2")
            .addressLine3("add3"))
        .statutoryChangeReason("stat");

    FinancialAwardElementType result = caseDetailsMapper.toFinancialAwardElementType(financialAward);

    assertNotNull(result);
    assertEquals(financialAward.getAmount(), result.getAmount());
    assertEquals(financialAward.getAwardedBy(), result.getAwardedBy());
    assertEquals(financialAward.getAwardJustifications(), result.getAwardJustifications());
    assertEquals(financialAward.getInterimAward(), result.getInterimAward());
    assertNotNull(result.getLiableParties());
    assertNotNull(result.getLiableParties().getOtherPartyID());
    assertEquals(1, result.getLiableParties().getOtherPartyID().size());
    assertEquals(financialAward.getLiableParties().getFirst(), result.getLiableParties().getOtherPartyID().getFirst());
    assertEquals(financialAward.getOrderDate(), result.getOrderDate().toGregorianCalendar().getTime());
    assertEquals(financialAward.getOrderDateServed(), result.getOrderDateServed().toGregorianCalendar().getTime());
    assertEquals(financialAward.getOtherDetails(), result.getOtherDetails());
    assertNotNull(result.getRecovery());
    assertEquals(financialAward.getServiceAddress().getAddressLine1(), result.getServiceAddress().getAddressLine1());
    assertEquals(financialAward.getServiceAddress().getAddressLine2(), result.getServiceAddress().getAddressLine2());
    assertEquals(financialAward.getServiceAddress().getAddressLine3(), result.getServiceAddress().getAddressLine3());
    assertEquals(financialAward.getStatutoryChangeReason(), result.getStatutoryChangeReason());
  }

  private void compareName(NameDetail nameDetail, Name result) {
    assertNotNull(result);
    assertEquals(nameDetail.getFirstName(), result.getFirstName());
    assertEquals(nameDetail.getFullName(), result.getFullName());
    assertEquals(nameDetail.getMiddleName(), result.getMiddleName());
    assertEquals(nameDetail.getSurname(), result.getSurname());
    assertEquals(nameDetail.getTitle(), result.getTitle());
    assertEquals(nameDetail.getSurnameAtBirth(), result.getSurnameAtBirth());
  }
}
