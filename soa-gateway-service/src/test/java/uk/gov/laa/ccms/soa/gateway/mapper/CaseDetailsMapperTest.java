package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildAddress;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildAssesmentResultType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildRecordHistory;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildUser;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.jupiter.api.BeforeAll;
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
import uk.gov.laa.ccms.soa.gateway.model.OtherAsset;
import uk.gov.laa.ccms.soa.gateway.model.OtherParty;
import uk.gov.laa.ccms.soa.gateway.model.OtherPartyOrganisation;
import uk.gov.laa.ccms.soa.gateway.model.OtherPartyPerson;
import uk.gov.laa.ccms.soa.gateway.model.OutcomeDetail;
import uk.gov.laa.ccms.soa.gateway.model.PriorAuthority;
import uk.gov.laa.ccms.soa.gateway.model.ProceedingDetail;
import uk.gov.laa.ccms.soa.gateway.model.ProviderDetail;
import uk.gov.laa.ccms.soa.gateway.model.Recovery;
import uk.gov.laa.ccms.soa.gateway.model.RecoveryAmount;
import uk.gov.laa.ccms.soa.gateway.model.ScopeLimitation;
import uk.gov.laa.ccms.soa.gateway.model.ServiceAddress;
import uk.gov.laa.ccms.soa.gateway.model.TimeRelatedAward;
import uk.gov.laa.ccms.soa.gateway.model.TransactionStatus;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
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
import uk.gov.legalservices.enterprise.common._1_0.common.Name;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.Status;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;

@ExtendWith(MockitoExtension.class)
public class CaseDetailsMapperTest {

  private static DatatypeFactory datatypeFactory;

  @Mock
  CommonMapper commonMapper;

  @InjectMocks
  CaseDetailsMapperImpl caseDetailsMapper;

  @BeforeAll
  public static void setUp() throws DatatypeConfigurationException {
    datatypeFactory = DatatypeFactory.newInstance();
  }

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
  public void testToLarDetails() {
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
    assertEquals(otherAssetElementType.getHeldBy().getOtherPartyID().get(0),
        result.getHeldBy().get(0));
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
    assertEquals(landAwardElementType.getOtherProprietors().getOtherPartyID().get(0),
        result.getOtherProprietors().get(0));

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
    assertEquals(financialAwardElementType.getLiableParties().getOtherPartyID().get(0),
        result.getLiableParties().get(0));

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
    assertEquals(costAwardElementType.getLiableParties().getOtherParyID().get(0),
        result.getLiableParties().get(0));
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
    assertEquals(priorAuthorityElementType.getDetails().getAttribute().get(0).getName(),
        result.getDetails().get(0).getName());
    assertEquals(priorAuthorityElementType.getDetails().getAttribute().get(0).getValue(),
        result.getDetails().get(0).getValue());
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

  private CaseList buildCaseList() {
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

  private Case buildCase() {
    Case sourceCase = new Case();
    sourceCase.setCaseReferenceNumber("123");
    sourceCase.setCaseDetails(buildCaseDetails());
    return sourceCase;
  }

  private uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDetails buildCaseDetails() {
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

  private PriorAuthorityElementType buildPriorAuthorityElementType() {
    PriorAuthorityAttribElementType priorAuthorityAttribElementType = new PriorAuthorityAttribElementType();
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

  private DischargeElementType buildDischargeStatus() {
    DischargeElementType dischargeElementType = new DischargeElementType();
    dischargeElementType.setOtherDetails("otherdets");
    dischargeElementType.setReason("reason");
    dischargeElementType.setClientContinuePvtInd(Boolean.TRUE);

    return dischargeElementType;
  }

  private ActionListElementType buildAvailableFunctions() {
    ActionListElementType actionListElementType = new ActionListElementType();
    actionListElementType.getFunction().add("afunction");

    return actionListElementType;
  }

  private AwardElementType buildAwardElementType() {
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

  private CostAwardElementType buildCostAwardElementType() {
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

  private FinancialAwardElementType buildFinancialAwardElementType() {
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

  private RecoveryElementType buildRecoveryElementType() {
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

  private static LiableParties buildLiableParties() {
    LiableParties liableParties = new LiableParties();
    liableParties.getOtherParyID().add("liableparty");
    return liableParties;
  }

  private LandAwardElementType buildLandAwardElementType() {
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

  private static ServiceAddrElementType buildServiceAddrElementType() {
    ServiceAddrElementType serviceAddrElementType = new ServiceAddrElementType();
    serviceAddrElementType.setAddressLine1("addr1");
    serviceAddrElementType.setAddressLine2("addr2");
    serviceAddrElementType.setAddressLine3("addr3");
    return serviceAddrElementType;
  }

  private OtherAssetElementType buildOtherAssetElementType() {
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

  private RecoveryAmountElementType buildRecoveryAmountElementType() {
    RecoveryAmountElementType recoveryAmountElementType = new RecoveryAmountElementType();
    recoveryAmountElementType.setAmount(BigDecimal.TEN);
    recoveryAmountElementType.setDateReceived(datatypeFactory.newXMLGregorianCalendar());
    recoveryAmountElementType.setPaidToLSC(BigDecimal.TEN);
    return recoveryAmountElementType;
  }

  private TimeRelatedElementType buildTimeRelatedElementType() {
    TimeRelatedElementType timeRelatedElementType = new TimeRelatedElementType();
    timeRelatedElementType.setAmount(BigDecimal.TEN);
    timeRelatedElementType.setAwardType("awardType");
    timeRelatedElementType.setDescription("adescr");
    timeRelatedElementType.setOtherDetails("otherdets");
    timeRelatedElementType.setAwardDate(datatypeFactory.newXMLGregorianCalendar());
    timeRelatedElementType.setAwardTrigeringEvent("trigger");
    return timeRelatedElementType;
  }

  private Client buildClient() {
    Client client = new Client();
    client.setFirstName("firstname");
    client.setSurname("asurname");
    client.setClientReferenceNumber("clientref");

    return client;
  }

  private LinkedCaseType buildLinkedCaseType() {
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

  private CaseDocsElementType buildCaseDocsElementType() {
    CaseDocsElementType caseDocsElementType = new CaseDocsElementType();
    caseDocsElementType.setCCMSDocumentID("docid");
    caseDocsElementType.setDocumentSubject("docsubj");

    return caseDocsElementType;
  }

  private CaseStatusElementType buildCaseStatus() {
    CaseStatusElementType caseStatusElementType = new CaseStatusElementType();
    caseStatusElementType.setActualCaseStatus("actstat");
    caseStatusElementType.setDisplayCaseStatus("dispstat");
    caseStatusElementType.setStatusUpdateInd(true);

    return caseStatusElementType;
  }

  private uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails buildApplicationDetails() {
    uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ApplicationDetails applicationDetails =
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
    applicationDetails.getExternalResources().getExternalResource()
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

  private ProceedingElementType buildProceedingElementType() {
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

  private ScopeLimitationElementType buildScopeLimitationElementType() {
    ScopeLimitationElementType scopeLimitationElementType = new ScopeLimitationElementType();
    scopeLimitationElementType.setScopeLimitation("limit");
    scopeLimitationElementType.setScopeLimitationID("id");
    scopeLimitationElementType.setScopeLimitationWording("wording");
    scopeLimitationElementType.setDelegatedFunctionsApply(Boolean.TRUE);
    return scopeLimitationElementType;
  }

  private OutcomeDetailElementType buildOutcomeDetailElementType() {
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

  private OtherPartyElementType buildOtherPartyElementType() {
    OtherPartyDetail otherPartyDetail = new OtherPartyDetail();
    otherPartyDetail.setOrganization(buildOtherPartyOrgType());
    otherPartyDetail.setPerson(buildOtherPartyPersonType());

    OtherPartyElementType otherPartyElementType = new OtherPartyElementType();
    otherPartyElementType.setOtherPartyDetail(otherPartyDetail);
    otherPartyElementType.setOtherPartyID("partyid");
    otherPartyElementType.setSharedInd(true);

    return otherPartyElementType;
  }

  private OtherPartyOrgType buildOtherPartyOrgType() {
    OtherPartyOrgType otherPartyOrgType = new OtherPartyOrgType();
    otherPartyOrgType.setAddress(buildAddress());
    otherPartyOrgType.setOtherInformation("otherinf");
    otherPartyOrgType.setContactDetails(buildContactDetails());
    otherPartyOrgType.setOrganizationType("orgtype");
    otherPartyOrgType.setContactName("name");
    otherPartyOrgType.setCurrentlyTrading("trading");
    otherPartyOrgType.setOrganizationName("name");
    otherPartyOrgType.setRelationToCase("relation");
    otherPartyOrgType.setRelationToClient("toclient");
    return otherPartyOrgType;
  }

  private OtherPartyPersonType buildOtherPartyPersonType() {
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

  private Name buildName() {
    Name name = new Name();
    name.setFirstName("firstName");
    name.setFullName("fullname");
    name.setMiddleName("middle");
    name.setSurname("surname");
    name.setTitle("mr");
    name.setSurnameAtBirth("atbirth");

    return name;
  }

  private ContactDetails buildContactDetails() {
    ContactDetails contactDetails = new ContactDetails();
    contactDetails.setFax("faxnum");
    contactDetails.setEmailAddress("email");
    contactDetails.setMobileNumber("mobile");
    contactDetails.setTelephoneHome("telhome");
    contactDetails.setTelephoneWork("telwork");

    return contactDetails;
  }

  private ExtResourceElementType buildExtResourceElementType() {
    ExtResourceElementType extResourceElementType = new ExtResourceElementType();
    extResourceElementType.setExternalResourceType("exttype");
    extResourceElementType.setExternalResourceRef("extref");
    extResourceElementType.setChambers("chambers");
    extResourceElementType.setLocation("loc");
    extResourceElementType.setCostCeiling(new CostLimitations());
    extResourceElementType.getCostCeiling().getCostLimitation()
        .add(buildCostLimitationElementType());
    extResourceElementType.setDateInstructed(datatypeFactory.newXMLGregorianCalendar());

    return extResourceElementType;
  }

  private CostLimitationElementType buildCostLimitationElementType() {
    CostLimitationElementType costLimitationElementType = new CostLimitationElementType();
    costLimitationElementType.setAmount(BigDecimal.TEN);
    costLimitationElementType.setCostLimitID("limitid");
    costLimitationElementType.setCostCategory("costcat");
    costLimitationElementType.setPaidToDate(BigDecimal.TEN);
    costLimitationElementType.setBillingProviderID("provid");
    costLimitationElementType.setBillingProviderName("provname");

    return costLimitationElementType;
  }

  private CategoryOfLawElementType buildCategoryOfLawElementType() {
    CategoryOfLawElementType categoryOfLawElementType = new CategoryOfLawElementType();
    categoryOfLawElementType.setCategoryOfLawCode("catcode");
    categoryOfLawElementType.setCategoryOfLawDescription("catdesc");
    categoryOfLawElementType.setCostLimitations(new CostLimitations());
    categoryOfLawElementType.getCostLimitations().getCostLimitation()
        .add(buildCostLimitationElementType());
    categoryOfLawElementType.setGrantedAmount(BigDecimal.TEN);
    categoryOfLawElementType.setRequestedAmount(BigDecimal.TEN);
    categoryOfLawElementType.setTotalPaidToDate(BigDecimal.TEN);

    return categoryOfLawElementType;
  }

  private ProviderDetails buildProviderDetails() {
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

  private LARDetails buildLARDetails() {
    LARDetails larDetails = new LARDetails();
    larDetails.setLARScopeFlag(Boolean.TRUE);
    larDetails.setLegalHelpUFN("ufn");
    larDetails.setLegalHelpOfficeCode("offcode");

    return larDetails;
  }
}
