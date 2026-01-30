package uk.gov.laa.ccms.soa.gateway.mapper;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Objects;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import uk.gov.laa.ccms.soa.gateway.model.PriorAuthorityAttribute;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildApplicationDetails;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildAwardElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildCase;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildCaseDocsElementType;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentResult;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentScreen;
import uk.gov.laa.ccms.soa.gateway.model.Award;
import uk.gov.laa.ccms.soa.gateway.model.BaseClient;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.CaseDoc;
import uk.gov.laa.ccms.soa.gateway.model.CaseStatus;
import uk.gov.laa.ccms.soa.gateway.model.CategoryOfLaw;
import uk.gov.laa.ccms.soa.gateway.model.ContactDetail;
import uk.gov.laa.ccms.soa.gateway.model.CostAward;
import uk.gov.laa.ccms.soa.gateway.model.CostLimitation;
import uk.gov.laa.ccms.soa.gateway.model.Discharge;
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
import uk.gov.laa.ccms.soa.gateway.model.RecordHistory;
import uk.gov.laa.ccms.soa.gateway.model.RecoveredAmount;
import uk.gov.laa.ccms.soa.gateway.model.Recovery;
import uk.gov.laa.ccms.soa.gateway.model.RecoveryAmount;
import uk.gov.laa.ccms.soa.gateway.model.ScopeLimitation;
import uk.gov.laa.ccms.soa.gateway.model.ServiceAddress;
import uk.gov.laa.ccms.soa.gateway.model.SubmittedApplicationDetails;
import uk.gov.laa.ccms.soa.gateway.model.TimeRelatedAward;
import uk.gov.laa.ccms.soa.gateway.model.TransactionStatus;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.laa.ccms.soa.gateway.model.Valuation;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseUpdateRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ActionListElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AssessmentResultsElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardDetailElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardDetailElementType.AwardDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.AwardsElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Case;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseAdd;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDetailsAdd;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDocs;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDocsElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CategoryOfLawElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Client;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ContactDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostAwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostAwardElementType.LiableParties;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostLimitationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CostLimitations;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ExtResourceElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ExternalResources;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.FinancialAwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LARDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LandAwardElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LinkedCaseType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LinkedCaseUpdateType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.LinkedCasesUpdate;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.MeansAssesments;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.MeritsAssesments;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherAssetElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherParties;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyElementType.OtherPartyDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyOrgType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OtherPartyPersonType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OutcomeDetailElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OutcomeElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Outcomes;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorities;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityAttribElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityDetElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.PriorAuthorityElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingDetElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProceedingElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Proceedings;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderDetails;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryAmountElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.RecoveryElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ScopeLimitationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ServiceAddrElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.TimeRelatedElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.UndertakingElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.UpdateApplicationDetails;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentDetailType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentScreenType;
import uk.gov.legalservices.enterprise.common._1_0.common.Name;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAAttributesType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAEntityType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAGoalType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType.Attributes;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRQType;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.Status;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;

@ExtendWith(MockitoExtension.class)
public class CaseDetailsMapperTest {

  @Mock
  CommonMapper commonMapper;

  @InjectMocks
  CaseDetailsMapperImpl caseDetailsMapper;

  DatatypeFactory df;

  @BeforeEach
  void setup() {
    try {
      df = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }
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
    assertFalse(otherPartyOrganisation.isCurrentlyTrading());
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

    SubmittedApplicationDetails result = caseDetailsMapper.toSubmittedApplicationDetails(applicationDetails);

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
        .courtOrderedMeansAssessment(Boolean.TRUE)
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
    assertEquals(otherPartyPerson.isCourtOrderedMeansAssessment(), result.isCourtOrderedMeansAssesment());
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
            .currentlyTrading(false)
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

  @Test
  @DisplayName("Test mapping CaseDetail to CaseAdd")
  void testToCaseAdd() {
    final CaseDetail caseDetail = new CaseDetail();
    caseDetail.setCaseReferenceNumber("ref1");

    final CaseAdd result = caseDetailsMapper.toCaseAdd(caseDetail);

    assertNotNull(result);
    assertEquals(caseDetail.getCaseReferenceNumber(), result.getCaseReferenceNumber());
    assertNotNull(result.getCaseDetails());
  }

  @Test
  @DisplayName("Test mapping CaseDetail to CaseAdd with null CaseDetail")
  void testToCaseAddWithNullCaseDetail() {
    final CaseAdd result = caseDetailsMapper.toCaseAdd(null);
    assertNull(result);
  }

  @Test
  @DisplayName("Test mapping CaseDetail to CaseAdd and CaseDetailsAdd")
  void testToCaseAddAndCaseDetailsAdd() {
    final CaseDetail caseDetail = new CaseDetail();
    caseDetail.setCaseReferenceNumber("ref1");
    caseDetail.setCertificateType("CERT_TYPE");
    caseDetail.setCertificateDate(new Date());
    caseDetail.setPreCertificateCosts(BigDecimal.valueOf(1000));
    caseDetail.setLegalHelpCosts(BigDecimal.valueOf(2000));
    caseDetail.setUndertakingAmount(BigDecimal.valueOf(3000));
    caseDetail.setApplicationDetails(new SubmittedApplicationDetails());
    caseDetail.setLinkedCases(Collections.singletonList(new LinkedCase()));
    caseDetail.setAwards(Collections.singletonList(new Award()));
    caseDetail.setPriorAuthorities(Collections.singletonList(new PriorAuthority()));
    caseDetail.setDischargeStatus(new Discharge());
    caseDetail.setCaseStatus(new CaseStatus());
    caseDetail.setRecordHistory(new RecordHistory());
    caseDetail.setCaseDocs(Collections.singletonList(new CaseDoc()));

    final CaseAdd result = caseDetailsMapper.toCaseAdd(caseDetail);

    assertNotNull(result);
    assertEquals(caseDetail.getCaseReferenceNumber(), result.getCaseReferenceNumber());

    final CaseDetailsAdd caseDetailsAdd = result.getCaseDetails();
    assertNotNull(caseDetailsAdd);
    assertEquals(caseDetail.getCertificateType(), caseDetailsAdd.getCertificateType());
    assertEquals(caseDetail.getPreCertificateCosts(), caseDetailsAdd.getPreCertificateCosts());
    assertEquals(caseDetail.getLegalHelpCosts(), caseDetailsAdd.getLegalHelpCosts());
    assertEquals(caseDetail.getUndertakingAmount(), caseDetailsAdd.getUndertakingAmount());

    assertNotNull(caseDetailsAdd.getApplicationDetails());
    assertNotNull(caseDetailsAdd.getCertificateDate());
    assertNotNull(caseDetailsAdd.getLinkedCases());
    assertNotNull(caseDetailsAdd.getAwards());
    assertNotNull(caseDetailsAdd.getPriorAuthorities());
    assertNotNull(caseDetailsAdd.getDischargeStatus());
    assertNotNull(caseDetailsAdd.getCaseStatus());
    assertNotNull(caseDetailsAdd.getRecordHistory());
    assertNotNull(caseDetailsAdd.getCaseDocs());
  }

  @Test
  @DisplayName("Test mapping CaseDetail to CaseAdd and CaseDetailsAdd with null CaseDetail")
  void testToCaseAddAndCaseDetailsAddWithNullCaseDetail() {
    final CaseDetailsAdd result = caseDetailsMapper.caseDetailToCaseDetailsAdd(null);
    assertNull(result);
  }

  @Test
  @DisplayName("Test mapping ApplicationDetails to soa.ApplicationDetails")
  void testToSoaApplicationDetails() {
    final SubmittedApplicationDetails applicationDetails = new SubmittedApplicationDetails();
    applicationDetails.setLarDetails(new LarDetails());
    applicationDetails.setClient(new BaseClient());
    applicationDetails.setPreferredAddress("Preferred Address");
    applicationDetails.setCorrespondenceAddress(new AddressDetail());
    applicationDetails.setProviderDetails(new ProviderDetail());
    applicationDetails.setCategoryOfLaw(new CategoryOfLaw());
    applicationDetails.setDateOfFirstAttendance(new Date());
    applicationDetails.setPurposeOfApplication("Purpose of Application");
    applicationDetails.setFixedHearingDateInd(true);
    applicationDetails.setDateOfHearing(new Date());
    applicationDetails.setPurposeOfHearing("Purpose of Hearing");
    applicationDetails.setHighProfileCaseInd(true);
    applicationDetails.setDevolvedPowersDate(new Date());
    applicationDetails.setApplicationAmendmentType("Amendment Type");
    applicationDetails.setCertificateType("Certificate Type");

    when(commonMapper.toAddress(eq(applicationDetails.getCorrespondenceAddress()))).thenReturn(
        new Address());

    final uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails result =
        caseDetailsMapper.toSoaApplicationDetails(applicationDetails);

    assertNotNull(result);
    assertNotNull(result.getOtherParties());
    assertNotNull(result.getExternalResources());
    assertNotNull(result.getProceedings());
    assertNotNull(result.getMeansAssesments());
    assertNotNull(result.getMeritsAssesments());
    assertNotNull(result.getLARDetails());
    assertNotNull(result.getClient());
    assertEquals(applicationDetails.getPreferredAddress(), result.getPreferredAddress());
    assertNotNull(result.getCorrespondenceAddress());
    assertNotNull(result.getProviderDetails());
    assertNotNull(result.getCategoryOfLaw());
    assertNotNull(result.getDateOfFirstAttendance());
    assertEquals(applicationDetails.getPurposeOfApplication(), result.getPurposeOfApplication());
    assertEquals(applicationDetails.isFixedHearingDateInd(), result.isFixedHearingDateInd());
    assertNotNull(result.getDateOfHearing());
    assertEquals(applicationDetails.getPurposeOfHearing(), result.getPurposeOfHearing());
    assertEquals(applicationDetails.isHighProfileCaseInd(), result.isHighProfileCaseInd());
    assertNotNull(result.getDevolvedPowersDate());
    assertEquals(applicationDetails.getApplicationAmendmentType(), result.getApplicationAmendmentType());
    assertEquals(applicationDetails.getCertificateType(), result.getCertificateType());
  }

  @Test
  @DisplayName("Test mapping CostLimitation to CostLimitationElementType")
  void testToCostLimitationElementType() {
    final CostLimitation costLimitation = new CostLimitation();
    costLimitation.setCostLimitId("CL123");
    costLimitation.setBillingProviderId("BPID456");
    costLimitation.setBillingProviderName("Provider Name");
    costLimitation.setCostCategory("Category A");
    costLimitation.setPaidToDate(BigDecimal.valueOf(1000));
    costLimitation.setAmount(BigDecimal.valueOf(5000));

    final CostLimitationElementType result = caseDetailsMapper.toCostLimitationElementType(costLimitation);

    assertNotNull(result);
    assertEquals(costLimitation.getCostLimitId(), result.getCostLimitID());
    assertEquals(costLimitation.getBillingProviderId(), result.getBillingProviderID());
    assertEquals(costLimitation.getBillingProviderName(), result.getBillingProviderName());
    assertEquals(costLimitation.getCostCategory(), result.getCostCategory());
    assertEquals(costLimitation.getPaidToDate(), result.getPaidToDate());
    assertEquals(costLimitation.getAmount(), result.getAmount());
  }

  @Test
  @DisplayName("Test mapping CostLimitation to CostLimitationElementType with null CostLimitation")
  void testToCostLimitationElementTypeWithNullCostLimitation() {
    final CostLimitationElementType result = caseDetailsMapper.toCostLimitationElementType(null);
    assertNull(result);
  }

  @Test
  @DisplayName("Test mapping ExternalResource to ExtResourceElementType")
  void testToExtResourceElementType() {
    final ExternalResource externalResource = new ExternalResource();
    externalResource.setExternalResourceRef("ExtResRef123");
    externalResource.setExternalResourceType("ResourceTypeA");
    externalResource.setDateInstructed(new Date());
    externalResource.setLocation("LocationXYZ");
    externalResource.setChambers("ChambersABC");

    final ExtResourceElementType result = caseDetailsMapper.toExtResourceElementType(externalResource);

    assertNotNull(result);
    assertEquals(externalResource.getExternalResourceRef(), result.getExternalResourceRef());
    assertEquals(externalResource.getExternalResourceType(), result.getExternalResourceType());
    assertNotNull(result.getDateInstructed());
    assertEquals(externalResource.getLocation(), result.getLocation());
    assertEquals(externalResource.getChambers(), result.getChambers());
  }

  @Test
  @DisplayName("Test mapping ExternalResource to ExtResourceElementType with null ExternalResource")
  void testToExtResourceElementTypeWithNullExternalResource() {
    final ExtResourceElementType result = caseDetailsMapper.toExtResourceElementType(null);
    assertNull(result);
  }

  @Test
  @DisplayName("Test mapping ProceedingDetail to ProceedingElementType")
  void testToProceedingElementType() {
    final ProceedingDetail proceedingDetail = new ProceedingDetail();
    proceedingDetail.setProceedingCaseId("CaseID123");
    proceedingDetail.setDateApplied(new Date());
    proceedingDetail.setStatus("StatusABC");
    proceedingDetail.setLeadProceedingIndicator(true);
    proceedingDetail.setOutcomeCourtCaseNumber("Outcome123");

    final ProceedingElementType result = caseDetailsMapper.toProceedingElementType(proceedingDetail);

    assertNotNull(result);
    assertNotNull(result.getAvailableFunctions());
    assertNotNull(result.getProceedingDetails());
    assertEquals(proceedingDetail.getProceedingCaseId(), result.getProceedingCaseID());
    assertNotNull(result.getDateApplied());
    assertEquals(proceedingDetail.getStatus(), result.getStatus());
    assertEquals(proceedingDetail.isLeadProceedingIndicator(), result.isLeadProceedingIndicator());
    assertEquals(proceedingDetail.getOutcomeCourtCaseNumber(), result.getOutcomeCourtCaseNumber());
  }

  @Test
  @DisplayName("Test mapping ProceedingDetail to ProceedingElementType with null ProceedingDetail")
  void testToProceedingElementTypeWithNullProceedingDetail() {
    final ProceedingElementType result = caseDetailsMapper.toProceedingElementType(null);
    assertNull(result);
  }

  @Test
  @DisplayName("Test mapping CostAward to CostAwardElementType")
  void testToCostAwardElementType() {
    final CostAward costAward = new CostAward();
    costAward.setPreCertificateAwardLsc(BigDecimal.valueOf(1000));
    costAward.setCertificateCostRateLsc(BigDecimal.valueOf(1.5));
    costAward.setOrderDate(new Date());
    costAward.setCourtAssessmentStatus("Court Status");
    costAward.setPreCertificateAwardOth(BigDecimal.valueOf(2000));
    costAward.setCertificateCostRateMarket(BigDecimal.valueOf(2.5));
    costAward.setAwardedBy("Awarded By Name");
    costAward.setInterestAwardedRate(BigDecimal.valueOf(0.05));
    costAward.setInterestAwardedStartDate(new Date());
    costAward.setOtherDetails("Other details");
    costAward.setOrderDateServed(new Date());
    costAward.setServiceAddress(new ServiceAddress());
    costAward.setRecovery(new Recovery());

    final CostAwardElementType result = caseDetailsMapper.toCostAwardElementType(costAward);

    assertNotNull(result);
    assertNotNull(result.getLiableParties());
    assertEquals(costAward.getPreCertificateAwardLsc(), result.getPreCertificateAwardLSC());
    assertEquals(costAward.getCertificateCostRateLsc(), result.getCertificateCostRateLSC());
    assertNotNull(result.getOrderDate());
    assertEquals(costAward.getCourtAssessmentStatus(), result.getCourtAssessmentStatus());
    assertEquals(costAward.getPreCertificateAwardOth(), result.getPreCertificateAwardOth());
    assertEquals(costAward.getCertificateCostRateMarket(), result.getCertificateCostRateMarket());
    assertEquals(costAward.getAwardedBy(), result.getAwardedBy());
    assertEquals(costAward.getInterestAwardedRate(), result.getInterestAwardedRate());
    assertNotNull(result.getInterestAwardedStartDate());
    assertEquals(costAward.getOtherDetails(), result.getOtherDetails());
    assertNotNull(result.getOrderDateServed());
    assertNotNull(result.getServiceAddress());
    assertNotNull(result.getRecovery());
  }

  @Test
  @DisplayName("Test mapping CostAward to CostAwardElementType with null CostAward")
  void testToCostAwardElementTypeWithNullCostAward() {
    final CostAwardElementType result = caseDetailsMapper.toCostAwardElementType(null);
    assertNull(result);
  }

  @Test
  @DisplayName("Test mapping CaseDetail to CaseUpdateRQ")
  void testToCaseUpdateRq() throws DatatypeConfigurationException {

    CaseDetail caseDetail = buildCaseDetail();

    CaseUpdateRQ expected = buildExpectedCaseUpdateRq("caseUpdateType");

    // Mock mappings handled by commonMapper
    OtherPartyPersonType expectedOtherPerson = expected.getApplicationDetails().getOtherParties().getOtherParty().stream()
        .map(OtherPartyElementType::getOtherPartyDetail)
        .map(OtherPartyDetail::getPerson)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Expected person other party not found"));

    when(commonMapper.toAddress(eq(buildPersonAddressDetail()))).thenReturn(
        expectedOtherPerson.getAddress());

    OtherPartyOrgType expectedOtherOrganisation = expected.getApplicationDetails().getOtherParties().getOtherParty().stream()
        .map(OtherPartyElementType::getOtherPartyDetail)
        .map(OtherPartyDetail::getOrganization)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Expected organisation other party not found"));

    when(commonMapper.toAddress(eq(buildOrganisationAddressDetail()))).thenReturn(
        expectedOtherOrganisation.getAddress());

    when(commonMapper.toAddress(eq(caseDetail.getApplicationDetails().getCorrespondenceAddress()))).thenReturn(
        expected.getApplicationDetails().getCorrespondenceAddress());

    when(commonMapper.toYnString(true)).thenReturn("Y");

    when(commonMapper.toUser(buildCreatedBy())).thenReturn(buildExpectedCreatedBy());

    when(commonMapper.toUser(buildLastUpdatedBy())).thenReturn(buildExpectedLastUpdatedBy());

    when(commonMapper.toUser(caseDetail.getApplicationDetails().getProviderDetails().getContactUserId()))
        .thenReturn(expected.getApplicationDetails().getProviderDetails().getContactUserID());

    CaseUpdateRQ result = caseDetailsMapper.toCaseUpdateRq(caseDetail, "caseUpdateType");

    assertThat(result).usingRecursiveComparison()
        .isEqualTo(expected);
  }

  private CaseDetail buildCaseDetail() {
    BaseClient baseClient = buildBaseClient();

    SubmittedApplicationDetails submittedApplicationDetails
        = buildSubmittedApplicationDetails(baseClient);

    LinkedCase linkedCase = buildLinkedCase(baseClient);

    Award award = buildAward();

    PriorAuthority priorAuthority = buildPriorAuthority();

    CaseStatus caseStatus = buildCaseStatus();

    RecordHistory recordHistory = buildRecordHistory();

    CaseDoc caseDoc = buildCaseDoc();

    CaseDetail caseDetail = new CaseDetail();
    caseDetail.setCaseReferenceNumber("caseReferenceNumber");
    caseDetail.setApplicationDetails(submittedApplicationDetails);
    caseDetail.setCertificateType("certificateType");
    caseDetail.setCertificateDate(new Date());
    caseDetail.setPreCertificateCosts(BigDecimal.valueOf(1000));
    caseDetail.setLegalHelpCosts(BigDecimal.valueOf(2000));
    caseDetail.setUndertakingAmount(BigDecimal.valueOf(3000));
    caseDetail.setUndertakingMaximumAmount(BigDecimal.valueOf(4000));
    caseDetail.setLinkedCases(Collections.singletonList(linkedCase));
    caseDetail.setAwards(Collections.singletonList(award));
    caseDetail.setPriorAuthorities(Collections.singletonList(priorAuthority));
    caseDetail.setDischargeStatus(null);
    caseDetail.setAvailableFunctions(List.of("availableFunction"));
    caseDetail.setCaseStatus(caseStatus);
    caseDetail.setRecordHistory(recordHistory);
    caseDetail.setCaseDocs(Collections.singletonList(caseDoc));

    return caseDetail;
  }

  private CaseUpdateRQ buildExpectedCaseUpdateRq(final String caseUpdateType) {

    UpdateApplicationDetails applicationDetails = buildExpectedUpdateApplicationDetails();

    LinkedCasesUpdate linkedCasesUpdate = buildExpectedLinkedCasesUpdate();

    AwardsElementType awardsElementType = buildExpectedAwardsElementType();

    PriorAuthorities priorAuthorities = buildExpectedPriorAuthorities();

    Outcomes outcomes = buildExpectedOutcomes();

    uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory recordHistory = buildExpectedRecordHistory();

    CaseDocs caseDocs = buildExpectedCaseDocs();

    CaseUpdateRQ caseUpdateRQ = new CaseUpdateRQ();
    caseUpdateRQ.setCaseReferenceNumber("caseReferenceNumber");
    caseUpdateRQ.setUpdateMsgType(caseUpdateType);
    caseUpdateRQ.setApplicationDetails(applicationDetails);
    caseUpdateRQ.setPreCertificateCosts(BigDecimal.valueOf(1000L));
    caseUpdateRQ.setLegalHelpCosts(BigDecimal.valueOf(2000L));
    caseUpdateRQ.setActualCaseStatus(null);
    caseUpdateRQ.setMessages(null);
    caseUpdateRQ.setNotifications(null);
    caseUpdateRQ.setLinkedCases(linkedCasesUpdate);
    caseUpdateRQ.setAwards(awardsElementType);
    caseUpdateRQ.setPriorAuthorities(priorAuthorities);
    caseUpdateRQ.setDischargeStatus(null);
    caseUpdateRQ.setUndertakings(null);
    caseUpdateRQ.setOutcomes(outcomes);
    caseUpdateRQ.setRecordHistory(recordHistory);
    caseUpdateRQ.setCaseDocs(caseDocs);

    return caseUpdateRQ;
  }

  private CaseDoc buildCaseDoc() {
    CaseDoc caseDoc = new CaseDoc();
    caseDoc.setCcmsDocumentId("ccmsDocumentId");
    caseDoc.setDocumentSubject("documentSubject");

    return caseDoc;
  }

  private CaseDocs buildExpectedCaseDocs() {
    CaseDocsElementType caseDocsElementType = new CaseDocsElementType();
    caseDocsElementType.setCCMSDocumentID("ccmsDocumentId");
    caseDocsElementType.setDocumentSubject("documentSubject");

    CaseDocs caseDocs = new CaseDocs();
    caseDocs.getCaseDoc().add(caseDocsElementType);

    return caseDocs;
  }

  private UserDetail buildCreatedBy() {
    UserDetail createdBy = new UserDetail();
    createdBy.setUserType("createdByUserType");
    createdBy.setUserName("createdByUserName");
    createdBy.setUserLoginId("createdByUserLoginId");

    return createdBy;
  }

  private User buildExpectedCreatedBy() {
    User createdBy = new User();
    createdBy.setUserType("createdByUserType");
    createdBy.setUserName("createdByUserName");
    createdBy.setUserLoginID("createdByUserLoginId");

    return createdBy;
  }

  private UserDetail buildLastUpdatedBy() {
    UserDetail lastUpdatedBy = new UserDetail();
    lastUpdatedBy.setUserType("lastUpdatedByUserType");
    lastUpdatedBy.setUserName("lastUpdatedByUserName");
    lastUpdatedBy.setUserLoginId("lastUpdatedByUserLoginId");

    return lastUpdatedBy;
  }

  private User buildExpectedLastUpdatedBy() {
    User lastUpdatedBy = new User();
    lastUpdatedBy.setUserType("lastUpdatedByUserType");
    lastUpdatedBy.setUserName("lastUpdatedByUserName");
    lastUpdatedBy.setUserLoginID("lastUpdatedByUserLoginId");

    return lastUpdatedBy;
  }

  private RecordHistory buildRecordHistory() {
    UserDetail createdBy = buildCreatedBy();

    UserDetail lastUpdatedBy = buildLastUpdatedBy();

    RecordHistory recordHistory = new RecordHistory();
    recordHistory.setCreatedBy(createdBy);
    recordHistory.setDateCreated(Date.from(Instant.parse("2001-12-01T12:00:00.00Z")));
    recordHistory.setDateLastUpdated(Date.from(Instant.parse("2001-12-02T12:00:00.00Z")));
    recordHistory.setLastUpdatedBy(lastUpdatedBy);

    return recordHistory;
  }

  private uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory buildExpectedRecordHistory() {
    User createdBy = buildExpectedCreatedBy();

    User lastUpdatedBy = buildExpectedLastUpdatedBy();

    uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory recordHistory = new uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory();
    recordHistory.setCreatedBy(createdBy);
    recordHistory.setDateCreated(df.newXMLGregorianCalendar("2001-12-01T12:00:00.000Z"));
    recordHistory.setLastUpdatedBy(lastUpdatedBy);
    recordHistory.setDateLastUpdated(df.newXMLGregorianCalendar("2001-12-02T12:00:00.000Z"));

    return recordHistory;
  }

  private CaseStatus buildCaseStatus() {
    CaseStatus caseStatus = new CaseStatus();
    caseStatus.setActualCaseStatus("actualCaseStatus");
    caseStatus.setDisplayCaseStatus("displayCaseStatus");
    caseStatus.setStatusUpdateInd(false);

    return caseStatus;
  }

  private SubmittedApplicationDetails buildSubmittedApplicationDetails(BaseClient baseClient) {
    LarDetails larDetails = buildLarDetails();

    ProviderDetail providerDetail = buildProviderDetail();

    ProceedingDetail proceedingDetail = buildProceeding();

    AddressDetail addressDetail = buildAddressDetail();

    CategoryOfLaw categoryOfLaw = buildCategoryOfLaw();

    AssessmentResult meansAssessment = buildAssessmentResult("means", 100, "2001-01-01T12:00:00.00Z");
    AssessmentResult meritsAssessment = buildAssessmentResult("merits", 200, "2001-01-02T12:00:00.00Z");

    OtherParty personOtherParty = buildPersonOtherParty();
    OtherParty organisationOtherParty = buildOrganisationOtherParty();

    ExternalResource externalResource = buildExternalResource();

    final SubmittedApplicationDetails applicationDetails = new SubmittedApplicationDetails();
    applicationDetails.setLarDetails(larDetails);
    applicationDetails.setClient(baseClient);
    applicationDetails.setPreferredAddress("preferredAddress");
    applicationDetails.setCorrespondenceAddress(addressDetail);
    applicationDetails.setProviderDetails(providerDetail);
    applicationDetails.setCategoryOfLaw(categoryOfLaw);
    applicationDetails.setOtherParties(List.of(personOtherParty, organisationOtherParty));
    applicationDetails.setExternalResources(List.of(externalResource));
    applicationDetails.setDateOfFirstAttendance(Date.from(Instant.parse("2001-02-01T12:00:00.00Z")));
    applicationDetails.setPurposeOfApplication("purposeOfApplication");
    applicationDetails.setFixedHearingDateInd(true);
    applicationDetails.setDateOfHearing(Date.from(Instant.parse("2001-02-02T12:00:00.00Z")));
    applicationDetails.setPurposeOfHearing("purposeOfHearing");
    applicationDetails.setHighProfileCaseInd(true);
    applicationDetails.setDevolvedPowersDate(Date.from(Instant.parse("2001-02-03T12:00:00.00Z")));
    applicationDetails.setApplicationAmendmentType("applicationAmendmentType");
    applicationDetails.setCertificateType("certificateType");
    applicationDetails.setMeansAssessmentAmended(true);
    applicationDetails.setMeansAssessments(List.of(meansAssessment));
    applicationDetails.setMeritsAssessments(List.of(meritsAssessment));
    applicationDetails.setMeritsAssessmentAmended(true);
    applicationDetails.setProceedings(List.of(proceedingDetail));

    return applicationDetails;
  }

  private UpdateApplicationDetails buildExpectedUpdateApplicationDetails() {
    Address address = buildExpectedAddress();

    ProviderDetails providerDetails = buildExpectedProviderDetails();

    CategoryOfLawElementType categoryOfLawElementType = buildExpectedCategoryOfLawElementType();

    OtherPartyElementType personOtherPartyElementType = buildExpectedPersonOtherPartyElementType();

    OtherPartyElementType organisationOtherPartyElementType = buildExpectedOrgOtherPartyElementType();

    OtherParties otherParties = new OtherParties();
    otherParties.getOtherParty().addAll(List.of(personOtherPartyElementType, organisationOtherPartyElementType));

    ExternalResources externalResources = buildExpectedExternalResources();

    Proceedings proceedings = buildExpectedProceedings();

    MeansAssesments meansAssessments = new MeansAssesments();
    meansAssessments.getAssesmentResults().add(
        buildExpectedAssesmentResultType("means", BigInteger.valueOf(100L), "2001-01-01T12:00:00.000Z"));

    MeritsAssesments meritsAssessments = new MeritsAssesments();
    meritsAssessments.getAssesmentResults().add(
        buildExpectedAssesmentResultType("merits", BigInteger.valueOf(200L), "2001-01-02T12:00:00.000Z"));

    UndertakingElementType undertakingElementType = buildExpectedUndertakingElementType();

    LARDetails larDetails = buildExpectedLARDetails();

    UpdateApplicationDetails applicationDetails = new UpdateApplicationDetails();
    applicationDetails.setPreferredAddress("preferredAddress");
    applicationDetails.setCorrespondenceAddress(address);
    applicationDetails.setProviderDetails(providerDetails);
    applicationDetails.setCategoryOfLaw(categoryOfLawElementType);
    applicationDetails.setOtherParties(otherParties);
    applicationDetails.setExternalResources(externalResources);
    applicationDetails.setProceedings(proceedings);
    applicationDetails.setMeansAssesments(meansAssessments);
    applicationDetails.setMeritsAssesments(meritsAssessments);
    applicationDetails.setUndertakings(undertakingElementType);
    applicationDetails.setDateOfFirstAttendance(df.newXMLGregorianCalendar("2001-02-01T12:00:00.000Z"));
    applicationDetails.setPurposeOfApplication("purposeOfApplication");
    applicationDetails.setFixedHearingDateInd(true);
    applicationDetails.setDateOfHearing(df.newXMLGregorianCalendar("2001-02-02T12:00:00.000Z"));
    applicationDetails.setPurposeOfHearing("purposeOfHearing");
    applicationDetails.setHighProfileCaseInd(true);
    applicationDetails.setSupervisorContactID("supervisorContactId");
    applicationDetails.setFeeEarnerContactID("feeEarnerContactId");
    applicationDetails.setDevolvedPowersDate(df.newXMLGregorianCalendar("2001-02-03T12:00:00.000Z"));
    applicationDetails.setApplicationAmendmentType("applicationAmendmentType");
    applicationDetails.setLARDetails(larDetails);
    applicationDetails.setCertificateType("certificateType");

    return applicationDetails;
  }

  private PriorAuthority buildPriorAuthority() {
    PriorAuthorityAttribute priorAuthorityAttribute = new PriorAuthorityAttribute();
    priorAuthorityAttribute.setName("name");
    priorAuthorityAttribute.setValue("value");

    PriorAuthority priorAuthority = new PriorAuthority();
    priorAuthority.setDetails(List.of(priorAuthorityAttribute));
    priorAuthority.setPriorAuthorityType("priorAuthorityType");
    priorAuthority.setDescription("description");
    priorAuthority.setAssessedAmount(BigDecimal.valueOf(1010));
    priorAuthority.setDecisionStatus("decisionStatus");
    priorAuthority.setReasonForRequest("reasonForRequest");
    priorAuthority.setRequestAmount(BigDecimal.valueOf(1010));

    return priorAuthority;
  }

  private PriorAuthorities buildExpectedPriorAuthorities() {
    PriorAuthorityAttribElementType priorAuthorityAttribElementType = new PriorAuthorityAttribElementType();
    priorAuthorityAttribElementType.setName("name");
    priorAuthorityAttribElementType.setValue("value");

    PriorAuthorityDetElementType priorAuthorityDetElementType = new PriorAuthorityDetElementType();
    priorAuthorityDetElementType.getAttribute().add(priorAuthorityAttribElementType);

    PriorAuthorityElementType priorAuthorityElementType = new PriorAuthorityElementType();
    priorAuthorityElementType.setPriorAuthorityType("priorAuthorityType");
    priorAuthorityElementType.setDescription("description");
    priorAuthorityElementType.setReasonForRequest("reasonForRequest");
    priorAuthorityElementType.setRequestAmount(BigDecimal.valueOf(1010L));
    priorAuthorityElementType.setDecisionStatus("decisionStatus");
    priorAuthorityElementType.setAssessedAmount(BigDecimal.valueOf(1010L));
    priorAuthorityElementType.setDetails(priorAuthorityDetElementType);

    PriorAuthorities priorAuthorities = new PriorAuthorities();
    priorAuthorities.getPriorAuthority().add(priorAuthorityElementType);

    return priorAuthorities;
  }

  private Award buildAward() {
    CostAward costAward = buildCostAward();

    FinancialAward financialAward = buildFinancialAward();

    LandAward landAward = buildLandAward();

    OtherAsset otherAsset = buildOtherAsset();

    Award award = new Award();
    award.setAwardCategory("awardCategory");
    award.setAwardType("awardType");
    award.setAwardId("awardId");
    award.setCostAward(costAward);
    award.setFinancialAward(financialAward);
    award.setDeleteAllowed(false);
    award.setLandAward(landAward);
    award.setOtherAsset(otherAsset);
    award.setUpdateAllowed(false);

    return award;
  }

  private AwardsElementType buildExpectedAwardsElementType() {
    CostAwardElementType costAwardElementType = buildExpectedCostAwardElementType();

    FinancialAwardElementType financialAwardElementType = buildExpectedFinancialAwardElementType();

    LandAwardElementType landAwardElementType = buildExpectedLandAwardElementType();

    OtherAssetElementType otherAssetElementType = buildExpectedOtherAssetElementType();

    AwardDetails awardDetails = new AwardDetails();
    awardDetails.setCostAward(costAwardElementType);
    awardDetails.setFinancialAward(financialAwardElementType);
    awardDetails.setLandAward(landAwardElementType);
    awardDetails.setOtherAsset(otherAssetElementType);

    AwardDetailElementType awardDetailElementType = new AwardDetailElementType();
    awardDetailElementType.setAwardCategory("awardCategory");
    awardDetailElementType.setAwardDetails(awardDetails);

    AwardElementType awardElementType = new AwardElementType();
    awardElementType.setAwardID("awardId");
    awardElementType.setAwardType("awardType");
    awardElementType.setDeleteAllowed(false);
    awardElementType.setUpdateAllowed(false);
    awardElementType.setAwardDetails(awardDetailElementType);

    AwardsElementType awardsElementType = new AwardsElementType();
    awardsElementType.getAward().add(awardElementType);

    return awardsElementType;
  }

  private LandAward buildLandAward() {
    ServiceAddress laServiceAddress = new ServiceAddress();
    laServiceAddress.setAddressLine1("addressLine1");
    laServiceAddress.setAddressLine2("addressLine2");
    laServiceAddress.setAddressLine3("addressLine3");

    TimeRelatedAward laTimeRelatedAward = new TimeRelatedAward();
    laTimeRelatedAward.setAmount(BigDecimal.valueOf(1010));
    laTimeRelatedAward.setAwardDate(Date.from(Instant.parse("2001-06-01T12:00:00.00Z")));
    laTimeRelatedAward.setAwardType("awardType");
    laTimeRelatedAward.setOtherDetails("otherDetails");
    laTimeRelatedAward.setDescription("description");
    laTimeRelatedAward.setAwardTriggeringEvent("awardTriggeringEvent");

    Valuation laValuation = new Valuation();
    laValuation.setAmount(BigDecimal.valueOf(1010));
    laValuation.setCriteria("criteria");
    laValuation.setDate(Date.from(Instant.parse("2001-07-01T12:00:00.00Z")));

    LandAward landAward = new LandAward();
    landAward.setAwardedBy("awardedBy");
    landAward.setAwardedPercentage(BigDecimal.valueOf(1010));
    landAward.setDescription("description");
    landAward.setEquity("equity");
    landAward.setDisputedPercentage(BigDecimal.valueOf(1010));
    landAward.setLandChargeRegistration("landChargeRegistration");
    landAward.setMortgageAmountDue(BigDecimal.valueOf(1010));
    landAward.setNoRecoveryDetails("noRecoveryDetails");
    landAward.setOtherProprietors(List.of("otherProprietor"));
    landAward.setPropertyAddress(laServiceAddress);
    landAward.setRegistrationRef("registrationRef");
    landAward.setStatChargeExemptReason("statChargeExemptReason");
    landAward.setTimeRelatedAward(laTimeRelatedAward);
    landAward.setTitleNo("titleNo");
    landAward.setValuation(laValuation);
    landAward.setOrderDate(Date.from(Instant.parse("2001-08-01T12:00:00.00Z")));
    landAward.setRecovery("recovery");

    return landAward;
  }

  private LandAwardElementType buildExpectedLandAwardElementType() {
    ServiceAddrElementType laServiceAddrElementType = new ServiceAddrElementType();
    laServiceAddrElementType.setAddressLine1("addressLine1");
    laServiceAddrElementType.setAddressLine2("addressLine2");
    laServiceAddrElementType.setAddressLine3("addressLine3");

    LandAwardElementType.Valuation laValuation = new LandAwardElementType.Valuation();
    laValuation.setAmount(BigDecimal.valueOf(1010L));
    laValuation.setCriteria("criteria");
    laValuation.setDate(df.newXMLGregorianCalendar("2001-07-01T12:00:00.000Z"));

    LandAwardElementType.OtherProprietors laOtherProprietors = new LandAwardElementType.OtherProprietors();
    laOtherProprietors.getOtherPartyID().add("otherProprietor");

    TimeRelatedElementType laTimeRelatedElement = new TimeRelatedElementType();
    laTimeRelatedElement.setAwardType("awardType");
    laTimeRelatedElement.setDescription("description");
    laTimeRelatedElement.setAmount(BigDecimal.valueOf(1010L));
    laTimeRelatedElement.setAwardTrigeringEvent("awardTriggeringEvent");
    laTimeRelatedElement.setAwardDate(df.newXMLGregorianCalendar("2001-06-01T12:00:00.000Z"));
    laTimeRelatedElement.setOtherDetails("otherDetails");

    LandAwardElementType landAwardElementType = new LandAwardElementType();
    landAwardElementType.setOrderDate(df.newXMLGregorianCalendar("2001-08-01T12:00:00.000Z"));
    landAwardElementType.setDescription("description");
    landAwardElementType.setTitleNo("titleNo");
    landAwardElementType.setPropertyAddress(laServiceAddrElementType);
    landAwardElementType.setValuation(laValuation);
    landAwardElementType.setDisputedPercentage(BigDecimal.valueOf(1010L));
    landAwardElementType.setAwardedPercentage(BigDecimal.valueOf(1010L));
    landAwardElementType.setMortgageAmountDue(BigDecimal.valueOf(1010L));
    landAwardElementType.setEquity("equity");
    landAwardElementType.setAwardedBy("awardedBy");
    landAwardElementType.setRecovery("recovery");
    landAwardElementType.setNoRecoveryDetails("noRecoveryDetails");
    landAwardElementType.setStatChargeExemptReason("statChargeExemptReason");
    landAwardElementType.setLandChargeRegistration("landChargeRegistration");
    landAwardElementType.setRegistrationRef("registrationRef");
    landAwardElementType.setOtherProprietors(laOtherProprietors);
    landAwardElementType.setTimeRelatedAward(laTimeRelatedElement);

    return landAwardElementType;
  }

  private OtherAsset buildOtherAsset() {
    TimeRelatedAward oaTimeRelatedAward = new TimeRelatedAward();
    oaTimeRelatedAward.setAmount(BigDecimal.valueOf(1010));
    oaTimeRelatedAward.setAwardDate(Date.from(Instant.parse("2001-09-01T12:00:00.00Z")));
    oaTimeRelatedAward.setAwardType("awardType");
    oaTimeRelatedAward.setOtherDetails("otherDetails");
    oaTimeRelatedAward.setDescription("description");
    oaTimeRelatedAward.setAwardTriggeringEvent("awardTriggeringEvent");

    Valuation oaValuation = new Valuation();
    oaValuation.setAmount(BigDecimal.valueOf(1010));
    oaValuation.setCriteria("criteria");
    oaValuation.setDate(Date.from(Instant.parse("2001-10-01T12:00:00.00Z")));

    OtherAsset otherAsset = new OtherAsset();
    otherAsset.setAwardedBy("awardedBy");
    otherAsset.setAwardedAmount(BigDecimal.valueOf(1010));
    otherAsset.setAwardedPercentage(BigDecimal.valueOf(1010));
    otherAsset.setDescription("description");
    otherAsset.setDisputedAmount(BigDecimal.valueOf(1010));
    otherAsset.setDisputedPercentage(BigDecimal.valueOf(1010));
    otherAsset.setNoRecoveryDetails("noRecoveryDetails");
    otherAsset.setStatChargeExemptReason("statChargeExemptReason");
    otherAsset.setTimeRelatedAward(oaTimeRelatedAward);
    otherAsset.setValuation(oaValuation);
    otherAsset.setOrderDate(Date.from(Instant.parse("2001-11-01T12:00:00.00Z")));
    otherAsset.setRecovery("recovery");
    otherAsset.setValuation(oaValuation);
    otherAsset.setHeldBy(List.of("heldBy"));
    otherAsset.recoveredAmount(BigDecimal.valueOf(1010));
    otherAsset.recoveredPercentage(BigDecimal.valueOf(1010));

    return otherAsset;
  }

  private OtherAssetElementType buildExpectedOtherAssetElementType() {
    OtherAssetElementType.Valuation oaValuation = new OtherAssetElementType.Valuation();
    oaValuation.setAmount(BigDecimal.valueOf(1010L));
    oaValuation.setCriteria("criteria");
    oaValuation.setDate(df.newXMLGregorianCalendar("2001-10-01T12:00:00.000Z"));

    OtherAssetElementType.HeldBy oaHeldBy = new OtherAssetElementType.HeldBy();
    oaHeldBy.getOtherPartyID().add("heldBy");

    TimeRelatedElementType oaTimeRelatedElement = new TimeRelatedElementType();
    oaTimeRelatedElement.setAwardType("awardType");
    oaTimeRelatedElement.setDescription("description");
    oaTimeRelatedElement.setAmount(BigDecimal.valueOf(1010L));
    oaTimeRelatedElement.setAwardTrigeringEvent("awardTriggeringEvent");
    oaTimeRelatedElement.setAwardDate(df.newXMLGregorianCalendar("2001-09-01T12:00:00.000Z"));
    oaTimeRelatedElement.setOtherDetails("otherDetails");

    OtherAssetElementType otherAssetElementType = new OtherAssetElementType();
    otherAssetElementType.setOrderDate(df.newXMLGregorianCalendar("2001-11-01T12:00:00.000Z"));
    otherAssetElementType.setDescription("description");
    otherAssetElementType.setValuation(oaValuation);
    otherAssetElementType.setAwardedAmount(BigDecimal.valueOf(1010L));
    otherAssetElementType.setAwardedPercentage(BigDecimal.valueOf(1010L));
    otherAssetElementType.setRecoveredAmount(BigDecimal.valueOf(1010L));
    otherAssetElementType.setRecoveredPercentage(BigDecimal.valueOf(1010L));
    otherAssetElementType.setDisputedAmount(BigDecimal.valueOf(1010L));
    otherAssetElementType.setDisputedPercentage(BigDecimal.valueOf(1010L));
    otherAssetElementType.setRecoveredPercentage(BigDecimal.valueOf(1010L));
    otherAssetElementType.setAwardedBy("awardedBy");
    otherAssetElementType.setRecovery("recovery");
    otherAssetElementType.setNoRecoveryDetails("noRecoveryDetails");
    otherAssetElementType.setHeldBy(oaHeldBy);
    otherAssetElementType.setTimeRelatedAward(oaTimeRelatedElement);
    otherAssetElementType.setStatChargeExemptReason("statChargeExemptReason");

    return otherAssetElementType;
  }

  private FinancialAward buildFinancialAward() {
    OfferedAmount faOfferedAmount = new OfferedAmount();
    faOfferedAmount.setAmount(BigDecimal.valueOf(1001));
    faOfferedAmount.setConditionsOfOffer("conditionsOfOffer");

    RecoveryAmount faClient = new RecoveryAmount();
    faClient.setAmount(BigDecimal.valueOf(1001));
    faClient.setDateReceived(Date.from(Instant.parse("2001-03-01T12:00:00.00Z")));
    faClient.setPaidToLsc(BigDecimal.valueOf(1001));

    RecoveryAmount faCourt = new RecoveryAmount();
    faCourt.setAmount(BigDecimal.valueOf(1001));
    faCourt.setDateReceived(Date.from(Instant.parse("2001-03-01T12:00:00.00Z")));
    faCourt.setPaidToLsc(BigDecimal.valueOf(1001));

    RecoveryAmount faSolicitor = new RecoveryAmount();
    faSolicitor.setAmount(BigDecimal.valueOf(1001));
    faSolicitor.setDateReceived(Date.from(Instant.parse("2001-03-01T12:00:00.00Z")));
    faSolicitor.setPaidToLsc(BigDecimal.valueOf(1001));

    RecoveredAmount faRecoveredAmount = new RecoveredAmount();
    faRecoveredAmount.setClient(faClient);
    faRecoveredAmount.setCourt(faCourt);
    faRecoveredAmount.setSolicitor(faSolicitor);

    Recovery faRecovery = new Recovery();
    faRecovery.setAwardValue(BigDecimal.valueOf(1010));
    faRecovery.setOfferedAmount(faOfferedAmount);
    faRecovery.setLeaveOfCourtReqdInd(false);
    faRecovery.setRecoveredAmount(faRecoveredAmount);
    faRecovery.setUnRecoveredAmount(BigDecimal.valueOf(1010));

    ServiceAddress faServiceAddress = new ServiceAddress();
    faServiceAddress.setAddressLine1("addressLine1");
    faServiceAddress.setAddressLine2("addressLine2");
    faServiceAddress.setAddressLine3("addressLine3");

    FinancialAward financialAward = new FinancialAward();
    financialAward.setAwardedBy("awardedBy");
    financialAward.setAwardJustifications("awardJustifications");
    financialAward.setInterimAward(BigDecimal.valueOf(1011));
    financialAward.setStatutoryChangeReason("statutoryChangeReason");
    financialAward.setLiableParties(List.of("liableParty"));
    financialAward.setOrderDate(Date.from(Instant.parse("2001-05-01T12:00:00.00Z")));
    financialAward.setOrderDateServed(Date.from(Instant.parse("2001-05-02T12:00:00.00Z")));
    financialAward.setOtherDetails("otherDetails");
    financialAward.setRecovery(faRecovery);
    financialAward.setServiceAddress(faServiceAddress);
    financialAward.amount(BigDecimal.valueOf(1010));

    return financialAward;
  }

  private FinancialAwardElementType buildExpectedFinancialAwardElementType() {
    ServiceAddrElementType faServiceAddrElementType = new ServiceAddrElementType();
    faServiceAddrElementType.setAddressLine1("addressLine1");
    faServiceAddrElementType.setAddressLine2("addressLine2");
    faServiceAddrElementType.setAddressLine3("addressLine3");

    RecoveryAmountElementType faSolicitor = new RecoveryAmountElementType();
    faSolicitor.setAmount(BigDecimal.valueOf(1001L));
    faSolicitor.setDateReceived(df.newXMLGregorianCalendar("2001-03-01T12:00:00.000Z"));
    faSolicitor.setPaidToLSC(BigDecimal.valueOf(1001L));

    RecoveryAmountElementType faCourt = new RecoveryAmountElementType();
    faCourt.setAmount(BigDecimal.valueOf(1001L));
    faCourt.setDateReceived(df.newXMLGregorianCalendar("2001-03-01T12:00:00.000Z"));
    faCourt.setPaidToLSC(BigDecimal.valueOf(1001L));

    RecoveryAmountElementType faClient = new RecoveryAmountElementType();
    faClient.setAmount(BigDecimal.valueOf(1001L));
    faClient.setDateReceived(df.newXMLGregorianCalendar("2001-03-01T12:00:00.000Z"));
    faClient.setPaidToLSC(BigDecimal.valueOf(1001L));

    RecoveryElementType.RecoveredAmount faRecoveredAmount = new RecoveryElementType.RecoveredAmount();
    faRecoveredAmount.setSolicitor(faSolicitor);
    faRecoveredAmount.setCourt(faCourt);
    faRecoveredAmount.setClient(faClient);

    RecoveryElementType.OfferedAmount faOfferedAmount = new RecoveryElementType.OfferedAmount();
    faOfferedAmount.setAmount(BigDecimal.valueOf(1001L));
    faOfferedAmount.setConditionsOfOffer("conditionsOfOffer");

    RecoveryElementType faRecoveryElementType = new RecoveryElementType();
    faRecoveryElementType.setAwardValue(BigDecimal.valueOf(1010L));
    faRecoveryElementType.setRecoveredAmount(faRecoveredAmount);
    faRecoveryElementType.setUnRecoveredAmount(BigDecimal.valueOf(1010L));
    faRecoveryElementType.setLeaveOfCourtReqdInd(false);
    faRecoveryElementType.setOfferedAmount(faOfferedAmount);

    FinancialAwardElementType.LiableParties liableParties = new FinancialAwardElementType.LiableParties();
    liableParties.getOtherPartyID().add("liableParty");

    FinancialAwardElementType financialAwardElementType = new FinancialAwardElementType();
    financialAwardElementType.setOrderDate(df.newXMLGregorianCalendar("2001-05-01T12:00:00.000Z"));
    financialAwardElementType.setAmount(BigDecimal.valueOf(1010L));
    financialAwardElementType.setInterimAward(BigDecimal.valueOf(1011L));
    financialAwardElementType.setAwardedBy("awardedBy");
    financialAwardElementType.setAwardJustifications("awardJustifications");
    financialAwardElementType.setStatutoryChangeReason("statutoryChangeReason");
    financialAwardElementType.setOtherDetails("otherDetails");
    financialAwardElementType.setLiableParties(liableParties);
    financialAwardElementType.setOrderDateServed(df.newXMLGregorianCalendar("2001-05-02T12:00:00.000Z"));
    financialAwardElementType.setServiceAddress(faServiceAddrElementType);
    financialAwardElementType.setRecovery(faRecoveryElementType);

    return financialAwardElementType;
  }

  private CostAward buildCostAward() {
    OfferedAmount offeredAmount = new OfferedAmount();
    offeredAmount.setAmount(BigDecimal.valueOf(1001));
    offeredAmount.setConditionsOfOffer("conditionsOfOffer");

    RecoveryAmount client = new RecoveryAmount();
    client.setAmount(BigDecimal.valueOf(1001));
    client.setDateReceived(Date.from(Instant.parse("2001-03-01T12:00:00.00Z")));
    client.setPaidToLsc(BigDecimal.valueOf(1001));

    RecoveryAmount court = new RecoveryAmount();
    court.setAmount(BigDecimal.valueOf(1001));
    court.setDateReceived(Date.from(Instant.parse("2001-03-01T12:00:00.00Z")));
    court.setPaidToLsc(BigDecimal.valueOf(1001));

    RecoveryAmount solicitor = new RecoveryAmount();
    solicitor.setAmount(BigDecimal.valueOf(1001));
    solicitor.setDateReceived(Date.from(Instant.parse("2001-03-01T12:00:00.00Z")));
    solicitor.setPaidToLsc(BigDecimal.valueOf(1001));

    RecoveredAmount recoveredAmount = new RecoveredAmount();
    recoveredAmount.setClient(client);
    recoveredAmount.setCourt(court);
    recoveredAmount.setSolicitor(solicitor);

    Recovery recovery = new Recovery();
    recovery.setAwardValue(BigDecimal.valueOf(1010));
    recovery.setOfferedAmount(offeredAmount);
    recovery.setLeaveOfCourtReqdInd(false);
    recovery.setRecoveredAmount(recoveredAmount);
    recovery.setUnRecoveredAmount(BigDecimal.valueOf(1010));

    ServiceAddress serviceAddress = new ServiceAddress();
    serviceAddress.setAddressLine1("addressLine1");
    serviceAddress.setAddressLine2("addressLine2");
    serviceAddress.setAddressLine3("addressLine3");

    CostAward costAward = new CostAward();
    costAward.setAwardedBy("awardedBy");
    costAward.setCertificateCostRateLsc(BigDecimal.valueOf(1100));
    costAward.setCertificateCostRateMarket(BigDecimal.valueOf(1200));
    costAward.setInterestAwardedRate(BigDecimal.valueOf(1300));
    costAward.setInterestAwardedStartDate(Date.from(Instant.parse("2001-04-01T12:00:00.00Z")));
    costAward.setCourtAssessmentStatus("courtAssessmentStatus");
    costAward.setLiableParties(List.of("liableParty"));
    costAward.setOrderDate(Date.from(Instant.parse("2001-04-02T12:00:00.00Z")));
    costAward.setOrderDateServed(Date.from(Instant.parse("2001-04-03T12:00:00.00Z")));
    costAward.setOtherDetails("otherDetails");
    costAward.setPreCertificateAwardLsc(BigDecimal.valueOf(1400));
    costAward.setPreCertificateAwardOth(BigDecimal.valueOf(1500));
    costAward.setRecovery(recovery);
    costAward.setServiceAddress(serviceAddress);

    return costAward;
  }

  private CostAwardElementType buildExpectedCostAwardElementType() {
    ServiceAddrElementType caServiceAddrElementType = new ServiceAddrElementType();
    caServiceAddrElementType.setAddressLine1("addressLine1");
    caServiceAddrElementType.setAddressLine2("addressLine2");
    caServiceAddrElementType.setAddressLine3("addressLine3");

    RecoveryAmountElementType caSolicitor = new RecoveryAmountElementType();
    caSolicitor.setAmount(BigDecimal.valueOf(1001L));
    caSolicitor.setDateReceived(df.newXMLGregorianCalendar("2001-03-01T12:00:00.000Z"));
    caSolicitor.setPaidToLSC(BigDecimal.valueOf(1001L));

    RecoveryAmountElementType caCourt = new RecoveryAmountElementType();
    caCourt.setAmount(BigDecimal.valueOf(1001L));
    caCourt.setDateReceived(df.newXMLGregorianCalendar("2001-03-01T12:00:00.000Z"));
    caCourt.setPaidToLSC(BigDecimal.valueOf(1001L));

    RecoveryAmountElementType caClient = new RecoveryAmountElementType();
    caClient.setAmount(BigDecimal.valueOf(1001L));
    caClient.setDateReceived(df.newXMLGregorianCalendar("2001-03-01T12:00:00.000Z"));
    caClient.setPaidToLSC(BigDecimal.valueOf(1001L));

    RecoveryElementType.RecoveredAmount caRecoveredAmount = new RecoveryElementType.RecoveredAmount();
    caRecoveredAmount.setSolicitor(caSolicitor);
    caRecoveredAmount.setCourt(caCourt);
    caRecoveredAmount.setClient(caClient);

    RecoveryElementType.OfferedAmount caOfferedAmount = new RecoveryElementType.OfferedAmount();
    caOfferedAmount.setAmount(BigDecimal.valueOf(1001L));
    caOfferedAmount.setConditionsOfOffer("conditionsOfOffer");

    RecoveryElementType caRecoveryElementType = new RecoveryElementType();
    caRecoveryElementType.setAwardValue(BigDecimal.valueOf(1010L));
    caRecoveryElementType.setRecoveredAmount(caRecoveredAmount);
    caRecoveryElementType.setUnRecoveredAmount(BigDecimal.valueOf(1010L));
    caRecoveryElementType.setLeaveOfCourtReqdInd(false);
    caRecoveryElementType.setOfferedAmount(caOfferedAmount);

    CostAwardElementType.LiableParties caLiableParties = new CostAwardElementType.LiableParties();
    caLiableParties.getOtherParyID().add("liableParty");

    CostAwardElementType costAwardElementType = new CostAwardElementType();
    costAwardElementType.setOrderDate(df.newXMLGregorianCalendar("2001-04-02T12:00:00.000Z"));
    costAwardElementType.setCourtAssessmentStatus("courtAssessmentStatus");
    costAwardElementType.setPreCertificateAwardLSC(BigDecimal.valueOf(1400L));
    costAwardElementType.setPreCertificateAwardOth(BigDecimal.valueOf(1500L));
    costAwardElementType.setCertificateCostRateLSC(BigDecimal.valueOf(1100L));
    costAwardElementType.setCertificateCostRateMarket(BigDecimal.valueOf(1200L));
    costAwardElementType.setAwardedBy("awardedBy");
    costAwardElementType.setInterestAwardedRate(BigDecimal.valueOf(1300L));
    costAwardElementType.setInterestAwardedStartDate(df.newXMLGregorianCalendar("2001-04-01T12:00:00.000Z"));
    costAwardElementType.setOtherDetails("otherDetails");
    costAwardElementType.setLiableParties(caLiableParties);
    costAwardElementType.setOrderDateServed(df.newXMLGregorianCalendar("2001-04-03T12:00:00.000Z"));
    costAwardElementType.setServiceAddress(caServiceAddrElementType);
    costAwardElementType.setRecovery(caRecoveryElementType);

    return costAwardElementType;
  }

  private LinkedCase buildLinkedCase(BaseClient baseClient) {
    LinkedCase linkedCase = new LinkedCase();
    linkedCase.setCaseStatus("caseStatus");
    linkedCase.setCaseReferenceNumber("caseReferenceNumber");
    linkedCase.setClient(baseClient);
    linkedCase.setLinkType("linkType");
    linkedCase.setCategoryOfLawCode("categoryOfLawCode");
    linkedCase.setCategoryOfLawDesc("categoryOfLawDesc");
    linkedCase.setFeeEarnerId("feeEarnerId");
    linkedCase.setFeeEarnerName("feeEarnerName");
    linkedCase.setProviderReferenceNumber("providerReferenceNumber");
    linkedCase.setPublicFundingAppliedInd(false);

    return linkedCase;
  }

  private LinkedCasesUpdate buildExpectedLinkedCasesUpdate() {
    LinkedCaseUpdateType linkedCaseUpdateType = new LinkedCaseUpdateType();
    linkedCaseUpdateType.setCaseReferenceNumber("caseReferenceNumber");
    linkedCaseUpdateType.setLinkType("linkType");
    linkedCaseUpdateType.setPublicFundingAppliedInd(false);

    LinkedCasesUpdate linkedCasesUpdate = new LinkedCasesUpdate();
    linkedCasesUpdate.getLinkedCase().add(linkedCaseUpdateType);

    return linkedCasesUpdate;
  }

  private ExternalResource buildExternalResource() {
    CostLimitation costLimitation = new CostLimitation();
    costLimitation.setCostLimitId("costLimitId");
    costLimitation.setBillingProviderId("billingProviderId");
    costLimitation.setBillingProviderName("billingProviderName");
    costLimitation.setCostCategory("costCategory");
    costLimitation.setPaidToDate(BigDecimal.valueOf(1010));
    costLimitation.setAmount(BigDecimal.valueOf(1010));

    ExternalResource externalResource = new ExternalResource();
    externalResource.setExternalResourceRef("externalResourceRef");
    externalResource.setExternalResourceType("externalResourceType");
    externalResource.setDateInstructed(Date.from(Instant.parse("2001-04-01T12:00:00.00Z")));
    externalResource.setCostCeiling(List.of(costLimitation));
    externalResource.setLocation("location");
    externalResource.setChambers("chambers");

    return externalResource;
  }

  private ExternalResources buildExpectedExternalResources() {
    CostLimitationElementType erCostLimitationElementType = new CostLimitationElementType();
    erCostLimitationElementType.setCostLimitID("costLimitId");
    erCostLimitationElementType.setBillingProviderID("billingProviderId");
    erCostLimitationElementType.setBillingProviderName("billingProviderName");
    erCostLimitationElementType.setCostCategory("costCategory");
    erCostLimitationElementType.setPaidToDate(BigDecimal.valueOf(1010L));
    erCostLimitationElementType.setAmount(BigDecimal.valueOf(1010L));

    CostLimitations erCostLimitations = new CostLimitations();
    erCostLimitations.getCostLimitation().add(erCostLimitationElementType);

    ExtResourceElementType extResourceElementType = new ExtResourceElementType();
    extResourceElementType.setExternalResourceRef("externalResourceRef");
    extResourceElementType.setExternalResourceType("externalResourceType");
    extResourceElementType.setDateInstructed(df.newXMLGregorianCalendar("2001-04-01T12:00:00.00Z"));
    extResourceElementType.setCostCeiling(erCostLimitations);
    extResourceElementType.setLocation("location");
    extResourceElementType.setChambers("chambers");

    ExternalResources externalResources = new ExternalResources();
    externalResources.getExternalResource().add(extResourceElementType);

    return externalResources;
  }

  private OtherParty buildOrganisationOtherParty() {
    AddressDetail organisationAddressDetail = buildOrganisationAddressDetail();

    ContactDetail organisationContactDetail = new ContactDetail();
    organisationContactDetail.setPassword("organisationPassword");
    organisationContactDetail.setPasswordReminder("organisationPasswordReminder");
    organisationContactDetail.setCorrespondenceMethod("organisationCorrespondenceMethod");
    organisationContactDetail.setCorrespondenceLanguage("organisationCorrespondenceLanguage");
    organisationContactDetail.setTelephoneHome("organisationTelephoneHome");
    organisationContactDetail.setTelephoneWork("organisationTelephoneWork");
    organisationContactDetail.setMobileNumber("organisationMobileNumber");
    organisationContactDetail.setEmailAddress("organisationEmailAddress");
    organisationContactDetail.setFax("organisationFax");

    OtherPartyOrganisation otherPartyOrganisation = new OtherPartyOrganisation();
    otherPartyOrganisation.setOrganizationName("organisationOrganizationName");
    otherPartyOrganisation.setOrganizationType("organisationOrganizationType");
    otherPartyOrganisation.setCurrentlyTrading(true);
    otherPartyOrganisation.setRelationToClient("organisationRelationToClient");
    otherPartyOrganisation.setRelationToCase("organisationRelationToCase");
    otherPartyOrganisation.setAddress(organisationAddressDetail);
    otherPartyOrganisation.setContactName("organisationContactName");
    otherPartyOrganisation.setContactDetails(organisationContactDetail);
    otherPartyOrganisation.setOtherInformation("organisationOtherInformation");

    OtherParty organisationOtherParty = new OtherParty();
    organisationOtherParty.setOtherPartyId("organisationOtherPartyId");
    organisationOtherParty.setOrganisation(otherPartyOrganisation);
    organisationOtherParty.setPerson(null);
    organisationOtherParty.setSharedInd(false);

    return organisationOtherParty;
  }

  private OtherPartyElementType buildExpectedOrgOtherPartyElementType() {
    Address organisationAddress = new Address();
    organisationAddress.setAddressID("organisationAddressId");
    organisationAddress.setHouse("organisationHouse");
    organisationAddress.setCoffName("organisationCareOfName");
    organisationAddress.setAddressLine1("organisationAddressLine1");
    organisationAddress.setAddressLine2("organisationAddressLine2");
    organisationAddress.setAddressLine3("organisationAddressLine3");
    organisationAddress.setAddressLine4("organisationAddressLine4");
    organisationAddress.setCity("organisationCity");
    organisationAddress.setCountry("organisationCountry");
    organisationAddress.setCounty("organisationCounty");
    organisationAddress.setState("organisationState");
    organisationAddress.setProvince("organisationProvince");
    organisationAddress.setPostalCode("organisationPostalCode");

    ContactDetails organisationContactDetails = new ContactDetails();
    organisationContactDetails.setTelephoneHome("organisationTelephoneHome");
    organisationContactDetails.setTelephoneWork("organisationTelephoneWork");
    organisationContactDetails.setMobileNumber("organisationMobileNumber");
    organisationContactDetails.setEmailAddress("organisationEmailAddress");
    organisationContactDetails.setFax("organisationFax");

    OtherPartyOrgType otherPartyOrgType = new OtherPartyOrgType();
    otherPartyOrgType.setOrganizationName("organisationOrganizationName");
    otherPartyOrgType.setOrganizationType("organisationOrganizationType");
    otherPartyOrgType.setCurrentlyTrading("Y");
    otherPartyOrgType.setRelationToClient("organisationRelationToClient");
    otherPartyOrgType.setRelationToCase("organisationRelationToCase");
    otherPartyOrgType.setAddress(organisationAddress);
    otherPartyOrgType.setContactName("organisationContactName");
    otherPartyOrgType.setContactDetails(organisationContactDetails);
    otherPartyOrgType.setOtherInformation("organisationOtherInformation");

    OtherPartyDetail organisationOtherPartyDetail = new OtherPartyDetail();
    organisationOtherPartyDetail.setOrganization(otherPartyOrgType);

    OtherPartyElementType organisationOtherPartyElementType = new OtherPartyElementType();
    organisationOtherPartyElementType.setOtherPartyID("organisationOtherPartyId");
    organisationOtherPartyElementType.setSharedInd(false);
    organisationOtherPartyElementType.setOtherPartyDetail(organisationOtherPartyDetail);

    return organisationOtherPartyElementType;
  }

  private AddressDetail buildOrganisationAddressDetail() {
    AddressDetail organisationAddressDetail = new AddressDetail();
    organisationAddressDetail.setAddressId("organisationAddressId");
    organisationAddressDetail.setHouse("organisationHouse");
    organisationAddressDetail.setCareOfName("organisationCareOfName");
    organisationAddressDetail.setAddressLine1("organisationAddressLine1");
    organisationAddressDetail.setAddressLine2("organisationAddressLine2");
    organisationAddressDetail.setAddressLine3("organisationAddressLine3");
    organisationAddressDetail.setAddressLine4("organisationAddressLine4");
    organisationAddressDetail.setCity("organisationCity");
    organisationAddressDetail.setCountry("organisationCountry");
    organisationAddressDetail.setCounty("organisationCounty");
    organisationAddressDetail.setProvince("organisationProvince");
    organisationAddressDetail.setState("organisationState");
    organisationAddressDetail.setPostalCode("organisationPostalCode");

    return organisationAddressDetail;
  }

  private OtherParty buildPersonOtherParty() {
    NameDetail nameDetail = new NameDetail();
    nameDetail.setTitle("personTitle");
    nameDetail.setSurname("personSurname");
    nameDetail.setFirstName("personFirstName");
    nameDetail.setMiddleName("personMiddleName");
    nameDetail.setSurnameAtBirth("personSurnameAtBirth");
    nameDetail.setFullName("personFullName");

    AddressDetail personAddressDetail = buildPersonAddressDetail();

    ContactDetail personContactDetail = new ContactDetail();
    personContactDetail.setPassword("personPassword");
    personContactDetail.setPasswordReminder("personPasswordReminder");
    personContactDetail.setCorrespondenceMethod("personCorrespondenceMethod");
    personContactDetail.setCorrespondenceLanguage("personCorrespondenceLanguage");
    personContactDetail.setTelephoneHome("personTelephoneHome");
    personContactDetail.setTelephoneWork("personTelephoneWork");
    personContactDetail.setMobileNumber("personMobileNumber");
    personContactDetail.setEmailAddress("personEmailAddress");
    personContactDetail.setFax("personFax");

    OtherPartyPerson otherPartyPerson = new OtherPartyPerson();
    otherPartyPerson.setName(nameDetail);
    otherPartyPerson.setDateOfBirth(Date.from(Instant.parse("2001-03-01T12:00:00.00Z")));
    otherPartyPerson.setAddress(personAddressDetail);
    otherPartyPerson.setRelationToClient("personRelationToClient");
    otherPartyPerson.setRelationToCase("personRelationToCase");
    otherPartyPerson.setNiNumber("personNiNumber");
    otherPartyPerson.setContactName("personContactName");
    otherPartyPerson.setContactDetails(personContactDetail);
    otherPartyPerson.setOrganizationName("personOrganizationName");
    otherPartyPerson.setEmployersName("personEmployersName");
    otherPartyPerson.setEmploymentStatus("personEmploymentStatus");
    otherPartyPerson.setOrganizationAddress("personOrganizationAddress");
    otherPartyPerson.setPartyLegalAidedInd(false);
    otherPartyPerson.setCertificateNumber("personCertificateNumber");
    otherPartyPerson.setCourtOrderedMeansAssessment(false);
    otherPartyPerson.setAssessedIncomeFrequency("personAssessedIncomeFrequency");
    otherPartyPerson.setAssessedIncome(BigDecimal.valueOf(1010));
    otherPartyPerson.setAssessedAssets(BigDecimal.valueOf(1010));
    otherPartyPerson.setAssessmentDate(Date.from(Instant.parse("2001-03-02T12:00:00.00Z")));
    otherPartyPerson.setPublicFundingAppliedInd(false);
    otherPartyPerson.setOtherInformation("personOtherInformation");

    OtherParty personOtherParty = new OtherParty();
    personOtherParty.setOtherPartyId("personOtherPartyId");
    personOtherParty.setOrganisation(null);
    personOtherParty.setPerson(otherPartyPerson);
    personOtherParty.setSharedInd(false);

    return personOtherParty;
  }

  private OtherPartyElementType buildExpectedPersonOtherPartyElementType() {
    Name personName = new Name();
    personName.setTitle("personTitle");
    personName.setSurname("personSurname");
    personName.setFirstName("personFirstName");
    personName.setMiddleName("personMiddleName");
    personName.setSurnameAtBirth("personSurnameAtBirth");
    personName.setFullName("personFullName");

    Address personAddress = new Address();
    personAddress.setAddressID("personAddressId");
    personAddress.setHouse("personHouse");
    personAddress.setCoffName("personCareOfName");
    personAddress.setAddressLine1("personAddressLine1");
    personAddress.setAddressLine2("personAddressLine2");
    personAddress.setAddressLine3("personAddressLine3");
    personAddress.setAddressLine4("personAddressLine4");
    personAddress.setCity("personCity");
    personAddress.setCountry("personCountry");
    personAddress.setCounty("personCounty");
    personAddress.setState("personState");
    personAddress.setProvince("personProvince");
    personAddress.setPostalCode("personPostalCode");

    ContactDetails personContactDetails = new ContactDetails();
    personContactDetails.setTelephoneHome("personTelephoneHome");
    personContactDetails.setTelephoneWork("personTelephoneWork");
    personContactDetails.setMobileNumber("personMobileNumber");
    personContactDetails.setEmailAddress("personEmailAddress");
    personContactDetails.setFax("personFax");

    OtherPartyPersonType otherPartyPersonType = new OtherPartyPersonType();
    otherPartyPersonType.setName(personName);
    otherPartyPersonType.setDateOfBirth(df.newXMLGregorianCalendar("2001-03-01T12:00:00.000Z"));
    otherPartyPersonType.setAddress(personAddress);
    otherPartyPersonType.setRelationToClient("personRelationToClient");
    otherPartyPersonType.setRelationToCase("personRelationToCase");
    otherPartyPersonType.setNINumber("personNiNumber");
    otherPartyPersonType.setContactName("personContactName");
    otherPartyPersonType.setContactDetails(personContactDetails);
    otherPartyPersonType.setOrganizationName("personOrganizationName");
    otherPartyPersonType.setEmployersName("personEmployersName");
    otherPartyPersonType.setEmploymentStatus("personEmploymentStatus");
    otherPartyPersonType.setOrganizationAddress("personOrganizationAddress");
    otherPartyPersonType.setPartyLegalAidedInd(false);
    otherPartyPersonType.setCertificateNumber("personCertificateNumber");
    otherPartyPersonType.setCourtOrderedMeansAssesment(false);
    otherPartyPersonType.setAssessedIncomeFrequency("personAssessedIncomeFrequency");
    otherPartyPersonType.setAssessedIncome(BigDecimal.valueOf(1010));
    otherPartyPersonType.setAssessedAsstes(BigDecimal.valueOf(1010L));
    otherPartyPersonType.setAssessmentDate(df.newXMLGregorianCalendar("2001-03-02T12:00:00.000Z"));
    otherPartyPersonType.setPublicFundingAppliedInd(false);
    otherPartyPersonType.setOtherInformation("personOtherInformation");

    OtherPartyDetail personOtherPartyDetail = new OtherPartyDetail();
    personOtherPartyDetail.setPerson(otherPartyPersonType);

    OtherPartyElementType personOtherPartyElementType = new OtherPartyElementType();
    personOtherPartyElementType.setOtherPartyID("personOtherPartyId");
    personOtherPartyElementType.setSharedInd(false);
    personOtherPartyElementType.setOtherPartyDetail(personOtherPartyDetail);

    return personOtherPartyElementType;
  }

  private AddressDetail buildPersonAddressDetail() {
    AddressDetail personAddressDetail = new AddressDetail();
    personAddressDetail.setAddressId("personAddressId");
    personAddressDetail.setHouse("personHouse");
    personAddressDetail.setCareOfName("personCareOfName");
    personAddressDetail.setAddressLine1("personAddressLine1");
    personAddressDetail.setAddressLine2("personAddressLine2");
    personAddressDetail.setAddressLine3("personAddressLine3");
    personAddressDetail.setAddressLine4("personAddressLine4");
    personAddressDetail.setCity("personCity");
    personAddressDetail.setCountry("personCountry");
    personAddressDetail.setCounty("personCounty");
    personAddressDetail.setProvince("personProvince");
    personAddressDetail.setState("personState");
    personAddressDetail.setPostalCode("personPostalCode");

    return personAddressDetail;
  }

  private AssessmentResult buildAssessmentResult(String prefix, Integer opaEntitySequence, String assessmentDate) {
    OpaAttribute meansOpaAttribute = new OpaAttribute();
    meansOpaAttribute.setCaption(prefix + "OpaAttributeCaption");
    meansOpaAttribute.setResponseText(prefix + "OpaAttributeResponseText");
    meansOpaAttribute.setResponseType(prefix + "OpaAttributeResponseType");
    meansOpaAttribute.setResponseValue(prefix + "OpaAttributeResponseValue");
    meansOpaAttribute.setUserDefinedInd(false);
    meansOpaAttribute.setAttribute(prefix + "OpaAttributeAttribute");

    OpaInstance meansOpaInstance = new OpaInstance();
    meansOpaInstance.setCaption(prefix + "OpaInstanceCaption");
    meansOpaInstance.setInstanceLabel(prefix + "OpaInstanceLabel");
    meansOpaInstance.setAttributes(List.of(meansOpaAttribute));

    OpaEntity meansOpaEntity = new OpaEntity();
    meansOpaEntity.setCaption(prefix + "OpaCaption");
    meansOpaEntity.setEntityName(prefix + "OpaEntityName");
    meansOpaEntity.setSequenceNumber(opaEntitySequence);
    meansOpaEntity.setInstances(List.of(meansOpaInstance));

    AssessmentScreen meansAssessmentScreen = new AssessmentScreen();
    meansAssessmentScreen.setCaption(prefix + "Caption");
    meansAssessmentScreen.setScreenName(prefix + "ScreenName");
    meansAssessmentScreen.setEntity(List.of(meansOpaEntity));

    OpaGoal meansOpaGoal = new OpaGoal();
    meansOpaGoal.setAttribute(prefix + "OpaGoalAttribute");
    meansOpaGoal.setAttributeValue(prefix + "OpaGoalAttributeValue");

    AssessmentResult meansAssessment = new AssessmentResult();
    meansAssessment.setAssessmentDetails(List.of(meansAssessmentScreen));
    meansAssessment.setAssessmentId(prefix + "AssessmentId");
    meansAssessment.setDate(Date.from(Instant.parse(assessmentDate)));
    meansAssessment.setResults(List.of(meansOpaGoal));
    meansAssessment.setDefaultInd(true);

    return meansAssessment;
  }

  private AssesmentResultType buildExpectedAssesmentResultType(String prefix, BigInteger opaEntitySequence, String assessmentDate) {
    OPAGoalType meansOpaGoalType = new OPAGoalType();
    meansOpaGoalType.setAttribute(prefix + "OpaGoalAttribute");
    meansOpaGoalType.setAttributeValue(prefix + "OpaGoalAttributeValue");

    OPAResultType meansOpaResultType = new OPAResultType();
    meansOpaResultType.getGoal().add(meansOpaGoalType);

    OPAAttributesType meansOpaAttributesType = new OPAAttributesType();
    meansOpaAttributesType.setAttribute(prefix + "OpaAttributeAttribute");
    meansOpaAttributesType.setCaption(prefix + "OpaAttributeCaption");
    meansOpaAttributesType.setResponseType(prefix + "OpaAttributeResponseType");
    meansOpaAttributesType.setResponseValue(prefix + "OpaAttributeResponseValue");
    meansOpaAttributesType.setResponseText(prefix + "OpaAttributeResponseText");
    meansOpaAttributesType.setUserDefinedInd(false);

    Attributes meansAttributes = new Attributes();
    meansAttributes.getAttribute().add(meansOpaAttributesType);

    OPAInstanceType meansOpaInstanceType = new OPAInstanceType();
    meansOpaInstanceType.setInstanceLabel(prefix + "OpaInstanceLabel");
    meansOpaInstanceType.setCaption(prefix + "OpaInstanceCaption");
    meansOpaInstanceType.setAttributes(meansAttributes);

    OPAEntityType meansOpaEntityType = new OPAEntityType();
    meansOpaEntityType.setSequenceNumber(opaEntitySequence);
    meansOpaEntityType.setEntityName(prefix + "OpaEntityName");
    meansOpaEntityType.setCaption(prefix + "OpaCaption");
    meansOpaEntityType.getInstances().add(meansOpaInstanceType);

    AssessmentScreenType meansAssessmentScreenType = new AssessmentScreenType();
    meansAssessmentScreenType.setScreenName(prefix + "ScreenName");
    meansAssessmentScreenType.setCaption(prefix + "Caption");
    meansAssessmentScreenType.getEntity().add(meansOpaEntityType);

    AssessmentDetailType meansAssessmentDetailType = new AssessmentDetailType();
    meansAssessmentDetailType.getAssessmentScreens().add(meansAssessmentScreenType);

    AssesmentResultType assessmentResultType = new AssesmentResultType();
    assessmentResultType.setAssesmentID(prefix + "AssessmentId");
    assessmentResultType.setDefault(true);
    assessmentResultType.setDate(df.newXMLGregorianCalendar(assessmentDate));
    assessmentResultType.setResults(meansOpaResultType);
    assessmentResultType.setAssesmentDetails(meansAssessmentDetailType);

    return assessmentResultType;
  }

  private BaseClient buildBaseClient() {
    BaseClient baseClient = new BaseClient();
    baseClient.setClientReferenceNumber("clientReferenceNumber");
    baseClient.setFirstName("firstName");
    baseClient.setSurname("surname");

    return baseClient;
  }

  private CategoryOfLaw buildCategoryOfLaw() {
    CostLimitation costLimitation = new CostLimitation();
    costLimitation.setAmount(BigDecimal.valueOf(11L));
    costLimitation.setCostLimitId("costLimitId");
    costLimitation.setCostCategory("costCategory");
    costLimitation.setBillingProviderId("billingProviderId");
    costLimitation.setBillingProviderName("billingProviderName");
    costLimitation.setPaidToDate(BigDecimal.valueOf(12L));

    CategoryOfLaw categoryOfLaw = new CategoryOfLaw();
    categoryOfLaw.setCategoryOfLawCode("categoryOfLawCode");
    categoryOfLaw.setCategoryOfLawDescription("categoryOfLawDescription");
    categoryOfLaw.setCostLimitations(List.of(costLimitation));
    categoryOfLaw.setGrantedAmount(BigDecimal.valueOf(1L));
    categoryOfLaw.setRequestedAmount(BigDecimal.valueOf(2L));
    categoryOfLaw.setTotalPaidToDate(BigDecimal.valueOf(3L));

    return categoryOfLaw;
  }

  private CategoryOfLawElementType buildExpectedCategoryOfLawElementType() {
    CostLimitationElementType costLimitationElementType = new CostLimitationElementType();
    costLimitationElementType.setCostLimitID("costLimitId");
    costLimitationElementType.setBillingProviderID("billingProviderId");
    costLimitationElementType.setBillingProviderName("billingProviderName");
    costLimitationElementType.setCostCategory("costCategory");
    costLimitationElementType.setPaidToDate(BigDecimal.valueOf(12L));
    costLimitationElementType.setAmount(BigDecimal.valueOf(11L));

    CostLimitations costLimitations = new CostLimitations();
    costLimitations.getCostLimitation().add(costLimitationElementType);

    CategoryOfLawElementType categoryOfLawElementType = new CategoryOfLawElementType();
    categoryOfLawElementType.setCategoryOfLawCode("categoryOfLawCode");
    categoryOfLawElementType.setCategoryOfLawDescription("categoryOfLawDescription");
    categoryOfLawElementType.setRequestedAmount(BigDecimal.valueOf(2L));
    categoryOfLawElementType.setGrantedAmount(BigDecimal.valueOf(1L));
    categoryOfLawElementType.setTotalPaidToDate(BigDecimal.valueOf(3L));
    categoryOfLawElementType.setCostLimitations(costLimitations);

    return categoryOfLawElementType;
  }

  private AddressDetail buildAddressDetail() {
    AddressDetail addressDetail = new AddressDetail();
    addressDetail.setAddressId("addressId");
    addressDetail.setAddressLine1("addressLine1");
    addressDetail.setAddressLine2("addressLine2");
    addressDetail.setAddressLine3("addressLine3");
    addressDetail.setAddressLine4("addressLine4");
    addressDetail.setCareOfName("careOfName");
    addressDetail.setCity("city");
    addressDetail.setCountry("country");
    addressDetail.setCounty("county");
    addressDetail.setHouse("house");
    addressDetail.setPostalCode("postalCode");
    addressDetail.setProvince("province");
    addressDetail.setState("state");

    return addressDetail;
  }

  private Address buildExpectedAddress() {
    Address address = new Address();
    address.setAddressID("addressId");
    address.setAddressLine1("addressLine1");
    address.setAddressLine2("addressLine2");
    address.setAddressLine3("addressLine3");
    address.setAddressLine4("addressLine4");
    address.setCoffName("careOfName");
    address.setCity("city");
    address.setCountry("country");
    address.setCounty("county");
    address.setHouse("house");
    address.setPostalCode("postalCode");
    address.setProvince("province");
    address.setState("state");

    return address;
  }

  private LarDetails buildLarDetails() {
    LarDetails larDetails = new LarDetails();
    larDetails.setLarScopeFlag(true);
    larDetails.setLegalHelpUfn("legalHelpUfn");
    larDetails.setLegalHelpOfficeCode("legalHelpOfficeCode");

    return larDetails;
  }

  private LARDetails buildExpectedLARDetails() {
    LARDetails larDetails = new LARDetails();
    larDetails.setLARScopeFlag(true);
    larDetails.setLegalHelpOfficeCode("legalHelpOfficeCode");
    larDetails.setLegalHelpUFN("legalHelpUfn");

    return larDetails;
  }

  private ProviderDetail buildProviderDetail() {
    ContactDetail contactDetail = new ContactDetail();
    contactDetail.setCorrespondenceLanguage("correspondenceLanguage");
    contactDetail.setEmailAddress("emailAddress");
    contactDetail.setFax("fax");
    contactDetail.setCorrespondenceMethod("correspondenceMethod");
    contactDetail.setMobileNumber("mobileNumber");
    contactDetail.setPasswordReminder("passwordReminder");
    contactDetail.setTelephoneHome("telephoneHome");
    contactDetail.setTelephoneWork("telephoneWork");
    contactDetail.setPassword("password");

    UserDetail userDetail = new UserDetail();
    userDetail.setUserLoginId("userLoginId");
    userDetail.setUserName("userName");
    userDetail.setUserType("userType");

    ProviderDetail providerDetail = new ProviderDetail();
    providerDetail.setProviderCaseReferenceNumber("providerCaseReferenceNumber");
    providerDetail.setProviderFirmId("providerFirmId");
    providerDetail.setProviderOfficeId("providerOfficeId");
    providerDetail.setFeeEarnerContactId("feeEarnerContactId");
    providerDetail.setSupervisorContactId("supervisorContactId");
    providerDetail.setContactDetails(contactDetail);
    providerDetail.setContactUserId(userDetail);

    return providerDetail;
  }

  private ProviderDetails buildExpectedProviderDetails() {
    ContactDetails contactDetails = new ContactDetails();
    contactDetails.setTelephoneHome("telephoneHome");
    contactDetails.setTelephoneWork("telephoneWork");
    contactDetails.setMobileNumber("mobileNumber");
    contactDetails.setEmailAddress("emailAddress");
    contactDetails.setFax("fax");

    ProviderDetails providerDetails = new ProviderDetails();
    providerDetails.setProviderCaseReferenceNumber("providerCaseReferenceNumber");
    providerDetails.setProviderFirmID("providerFirmId");
    providerDetails.setProviderOfficeID("providerOfficeId");
    providerDetails.setContactUserID(null);
    providerDetails.setContactDetails(contactDetails);
    providerDetails.setSupervisorContactID("supervisorContactId");
    providerDetails.setFeeEarnerContactID("feeEarnerContactId");

    return providerDetails;
  }

  private ProceedingDetail buildProceeding() {

    ScopeLimitation scopeLimitation = new ScopeLimitation();
    scopeLimitation.setScopeLimitation("scopeLimitation");
    scopeLimitation.setScopeLimitationId("scopeLimitationId");
    scopeLimitation.setScopeLimitationWording("scopeLimitationWording");
    scopeLimitation.setDelegatedFunctionsApply(false);

    OutcomeDetail outcomeDetail = new OutcomeDetail();
    outcomeDetail.setIssueDate(Date.from(Instant.parse("2001-06-01T12:00:00.00Z")));
    outcomeDetail.setFinalWorkDate(Date.from(Instant.parse("2001-06-02T12:00:00.00Z")));
    outcomeDetail.setStageEnd("stageEnd");
    outcomeDetail.setResolutionMethod("resolutionMethod");
    outcomeDetail.setResult("result");
    outcomeDetail.setAdditionalResultInfo("additionalResultInfo");
    outcomeDetail.setAltDisputeResolution("altDisputeResolution");
    outcomeDetail.setAltAcceptanceReason("altAcceptanceReason");
    outcomeDetail.setCourtCode("courtCode");
    outcomeDetail.setWiderBenefits("widerBenefits");
    outcomeDetail.setOutcomeCourtCaseNumber("outcomeCourtCaseNumber");

    ProceedingDetail proceeding = new ProceedingDetail();
    proceeding.setProceedingCaseId("proceedingCaseId");
    proceeding.setStatus("status");
    proceeding.setMatterType("matterType");
    proceeding.setProceedingType("proceedingType");
    proceeding.setLevelOfService("levelOfService");
    proceeding.setProceedingDescription("proceedingDescription");
    proceeding.setClientInvolvementType("clientInvolvementType");
    proceeding.setStage("stage");
    proceeding.setDateApplied(Date.from(Instant.parse("2001-01-01T12:00:00.00Z")));
    proceeding.setAvailableFunctions(List.of("proceedingAvailableFunction"));
    proceeding.setDateCostsValid(Date.from(Instant.parse("2001-01-02T12:00:00.00Z")));
    proceeding.setDateDevolvedPowersUsed(Date.from(Instant.parse("2001-01-03T12:00:00.00Z")));
    proceeding.setLeadProceedingIndicator(true);
    proceeding.setOrderType("orderType");
    proceeding.setDevolvedPowersInd(false);
    proceeding.setDateGranted(Date.from(Instant.parse("2001-01-04T12:00:00.00Z")));
    proceeding.setOutcome(outcomeDetail);
    proceeding.setOutcomeCourtCaseNumber("courtCaseNumber");
    proceeding.setScopeLimitationApplied("scopeLimitationApplied");
    proceeding.setScopeLimitations(List.of(scopeLimitation));

    return proceeding;
  }

  private Proceedings buildExpectedProceedings() {
    ActionListElementType actionListElementType = new ActionListElementType();
    actionListElementType.getFunction().add("proceedingAvailableFunction");

    ScopeLimitationElementType scopeLimitationElementType = new ScopeLimitationElementType();
    scopeLimitationElementType.setScopeLimitationID("scopeLimitationId");
    scopeLimitationElementType.setScopeLimitation("scopeLimitation");
    scopeLimitationElementType.setScopeLimitationWording("scopeLimitationWording");
    scopeLimitationElementType.setDelegatedFunctionsApply(false);

    ProceedingDetElementType.ScopeLimitations scopeLimitations = new ProceedingDetElementType.ScopeLimitations();
    scopeLimitations.getScopeLimitation().add(scopeLimitationElementType);

    OutcomeDetailElementType outcomeDetailElementType = buildExpectedOutcomeDetailElementType();

    ProceedingDetElementType proceedingDetElementType = new ProceedingDetElementType();
    proceedingDetElementType.setProceedingType("proceedingType");
    proceedingDetElementType.setProceedingDescription("proceedingDescription");
    proceedingDetElementType.setDateCostsValid(df.newXMLGregorianCalendar("2001-01-02T12:00:00.000Z"));
    proceedingDetElementType.setOrderType("orderType");
    proceedingDetElementType.setMatterType("matterType");
    proceedingDetElementType.setLevelOfService("levelOfService");
    proceedingDetElementType.setStage("stage");
    proceedingDetElementType.setClientInvolvementType("clientInvolvementType");
    proceedingDetElementType.setScopeLimitations(scopeLimitations);
    proceedingDetElementType.setScopeLimitationApplied("scopeLimitationApplied");
    proceedingDetElementType.setDevolvedPowersInd(false);
    proceedingDetElementType.setDateDevolvedPowersUsed(df.newXMLGregorianCalendar("2001-01-03T12:00:00.000Z"));
    proceedingDetElementType.setDateGranted(df.newXMLGregorianCalendar("2001-01-04T12:00:00.000Z"));
    proceedingDetElementType.setOutcome(outcomeDetailElementType);

    ProceedingElementType proceedingElementType = new ProceedingElementType();
    proceedingElementType.setProceedingCaseID("proceedingCaseId");
    proceedingElementType.setDateApplied(df.newXMLGregorianCalendar("2001-01-01T12:00:00.000Z"));
    proceedingElementType.setStatus("status");
    proceedingElementType.setLeadProceedingIndicator(true);
    proceedingElementType.setOutcomeCourtCaseNumber("courtCaseNumber");
    proceedingElementType.setAvailableFunctions(actionListElementType);
    proceedingElementType.setProceedingDetails(proceedingDetElementType);

    Proceedings proceedings = new Proceedings();
    proceedings.getProceeding().add(proceedingElementType);

    return proceedings;
  }

  private OutcomeDetailElementType buildExpectedOutcomeDetailElementType() {
    OutcomeDetailElementType outcomeDetailElementType = new OutcomeDetailElementType();
    outcomeDetailElementType.setIssueDate(df.newXMLGregorianCalendar("2001-06-01T12:00:00.000Z"));
    outcomeDetailElementType.setFinalWorkDate(df.newXMLGregorianCalendar("2001-06-02T12:00:00.000Z"));
    outcomeDetailElementType.setStageEnd("stageEnd");
    outcomeDetailElementType.setResolutionMethod("resolutionMethod");
    outcomeDetailElementType.setResult("result");
    outcomeDetailElementType.setAdditionalResultInfo("additionalResultInfo");
    outcomeDetailElementType.setAltDisputeResolution("altDisputeResolution");
    outcomeDetailElementType.setAltAcceptanceReason("altAcceptanceReason");
    outcomeDetailElementType.setCourtCode("courtCode");
    outcomeDetailElementType.setWiderBenifits("widerBenefits");
    outcomeDetailElementType.setOutcomeCourtCaseNumber("outcomeCourtCaseNumber");

    return outcomeDetailElementType;
  }

  private UndertakingElementType buildExpectedUndertakingElementType() {
    UndertakingElementType undertakingElementType = new UndertakingElementType();
    undertakingElementType.setMaxAmount(BigDecimal.valueOf(4000L));
    undertakingElementType.setEnteredAmount(BigDecimal.valueOf(3000L));
    undertakingElementType.setDetails(null);

    return undertakingElementType;
  }

  private Outcomes buildExpectedOutcomes() {
    OutcomeDetailElementType outcomeDetailElementType = buildExpectedOutcomeDetailElementType();

    OutcomeElementType outcomeElementType = new OutcomeElementType();
    outcomeElementType.setProceedingCaseID("proceedingCaseId");
    outcomeElementType.setOutcomeDetails(outcomeDetailElementType);

    Outcomes outcomes = new Outcomes();
    outcomes.getOutcome().add(outcomeElementType);

    return outcomes;
  }

}
