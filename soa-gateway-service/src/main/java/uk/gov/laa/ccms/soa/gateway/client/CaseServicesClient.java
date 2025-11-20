package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseUpdateRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseUpdateRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Case;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseAdd;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseDetailsAdd;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseInfo;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.OutcomeElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.UndertakingElementType;

/**
 * Provides a client interface for interacting with Case Management Services in the SOA-based
 * system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on case management services. It allows for the retrieval of case details
 * based on search criteria provided. Service name and URL details are injected at runtime.</p>
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class CaseServicesClient extends AbstractSoaClient {

  public static final String MESSAGE_TYPE_UNDERTAKING = "Undertaking";
  public static final String MESSAGE_TYPE_OUTCOME = "Outcome";

  /**
   * The Message Type to be used for Means Assessments Legal Amendment amendments.
   */
  private static final String MESSAGE_TYPE_MEANS_ASSESSMENT_LEGAL_AMENDMENT = "LegalAmendment";

  private final String serviceName;

  private final String serviceUrl;

  private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link CaseServicesClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName        The name of the case management service.
   * @param serviceUrl         The URL endpoint for the case management service.
   */
  public CaseServicesClient(WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.case.service-name}") String serviceName,
      @Value("${laa.ccms.soa-gateway.case.service-url}") String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Retrieve a List of Cases based on the supplied CaseInfo search criteria.
   *
   * @param loggedInUserId   - the logged in UserId
   * @param loggedInUserType - the logged in UserType
   * @param maxRecords       - the maximum records to be returned
   * @param caseInfo         - the search criteria
   * @return List of Cases
   */
  public CaseInqRS getCaseDetails(
      String loggedInUserId,
      String loggedInUserType,
      Integer maxRecords,
      CaseInfo caseInfo
  ) {

    final String soapAction = String.format("%s/GetCaseDetails", serviceName);
    CaseInqRQ caseInqRq = CASE_FACTORY.createCaseInqRQ();
    caseInqRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    CaseInqRQ.SearchCriteria searchCriteria = CASE_FACTORY.createCaseInqRQSearchCriteria();
    searchCriteria.setCaseInfo(caseInfo);

    caseInqRq.setSearchCriteria(searchCriteria);
    caseInqRq.setRecordCount(createRecordCount(maxRecords));

    return retrieveCaseDetails(soapAction, caseInqRq).getValue();
  }

  /**
   * Retrieve a single Case for the supplied case reference number.
   *
   * @param loggedInUserId      - the logged in UserId
   * @param loggedInUserType    - the logged in UserType
   * @param caseReferenceNumber - the case reference number
   * @return Response object containing the full details for a single Case
   */
  public CaseInqRS getCaseDetail(
      String loggedInUserId,
      String loggedInUserType,
      String caseReferenceNumber) {

    final String soapAction = String.format("%s/GetCaseDetails", serviceName);
    CaseInqRQ caseInqRq = CASE_FACTORY.createCaseInqRQ();
    caseInqRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    caseInqRq.setSearchCriteria(CASE_FACTORY.createCaseInqRQSearchCriteria());
    caseInqRq.getSearchCriteria().setCaseReferenceNumber(caseReferenceNumber);
    caseInqRq.setRecordCount(createRecordCount(1));

    return retrieveCaseDetails(soapAction, caseInqRq).getValue();
  }

  /**
   * Register a new Case in CCMS.
   *
   * @param loggedInUserId      - the logged in UserId
   * @param loggedInUserType    - the logged in UserType
   * @param caseAdd             - the case to add
   * @return Response object containing the result of the case creation.
   */
  public CaseAddRS createCaseApplication(
      String loggedInUserId,
      String loggedInUserType,
      CaseAdd caseAdd) {

    final String soapAction = String.format("%s/CreateCaseApplication", serviceName);
    CaseAddRQ caseAddRq = CASE_FACTORY.createCaseAddRQ();
    caseAddRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    caseAddRq.setCase(caseAdd);

    JAXBElement<CaseAddRS> response =
        (JAXBElement<CaseAddRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                serviceUrl,
                CASE_FACTORY.createCaseAddRQ(caseAddRq),
                new SoapActionCallback(soapAction));

    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());
    return response.getValue();
  }

  /**
   * Amends an existing Case in CCMS.
   *
   * @param loggedInUserId      - the logged in UserId
   * @param loggedInUserType    - the logged in UserType
   * @param caseDetail          - the case details to update
   * @return Response object containing the result of the case creation.
   */
  public CaseUpdateRS updateCase(
        String loggedInUserId,
        String loggedInUserType,
        CaseDetail caseDetail
  ) {
    final String soapAction = String.format("%s/UpdateCaseApplication", serviceName);
    CaseUpdateRQ caseUpdateRq = CASE_FACTORY.createCaseUpdateRQ();
    caseUpdateRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    /* UpdateApplicationDetails applicationDetails = caseUpdateRQ.getApplicationDetails();
    applicationDetails.setApplicationAmendmentType(caseAdd.getCaseDetails()
    .getApplicationDetails().getApplicationAmendmentType()); */


    // MY UPDATES

//    if (caseDetail != null) {
//      recordHistory
//          .setDateCreated(DateUtils.convertDateToXMLDateWithTimestamp(caseDetail.getDateCreated()));
//    }
//    result.setRecordHistory(recordHistory);

    // TODO CCMSPUI-695 enter undertaking
    boolean isUndertakingUpdate = false;

    // TODO CCMSPUI-695 enter undertaking
    boolean isOutcomeUpdate = false;

    if (isUndertakingUpdate) {
      caseUpdateRq.setUpdateMsgType(MESSAGE_TYPE_UNDERTAKING);
      UndertakingElementType undertakingElementType = new UndertakingElementType();
      undertakingElementType.setEnteredAmount(caseDetail.getUndertakingAmount());
      undertakingElementType.setMaxAmount(caseDetail.getUndertakingMaximumAmount());
      caseUpdateRq.setUndertakings(undertakingElementType);
    } else if (isOutcomeUpdate) {
      caseUpdateRq.setUpdateMsgType(MESSAGE_TYPE_OUTCOME);
      caseUpdateRq.setPreCertificateCosts(caseDetail.getPreCertificateCosts());
      caseUpdateRq.setLegalHelpCosts(caseDetail.getLegalHelpCosts());
      OutcomeElementType outcomeElementType = new OutcomeElementType();
      outcomeElementType.setProceedingCaseID(caseDetail.getApplicationDetails().proceedings().);
      caseUpdateRq.setOutcomes(CaseToEBSCaseConverter.convertToEBSOutcome(caseOutcome));
      caseUpdateRq.setAwards(CaseToEBSCaseConverter.convertToEBSAwards(caseOutcome));
      caseUpdateRq.setDischargeStatus(CaseToEBSCaseConverter.convertToEBSDischargeCase(caseOutcome));
      caseUpdateRq.setApplicationDetails(
          CaseToEBSCaseConverter.convertToEBSOutcomeDetails(caseDetail, caseOutcome));
    } else {
      if (app != null && app.isAmendmentQuick()) {
        caseUpdateRq.setUpdateMsgType(app.getAmendmentQuickType()); // Added 09/04
      } else {
        caseUpdateRq.setUpdateMsgType(MESSAGE_TYPE_MEANS_ASSESSMENT_LEGAL_AMENDMENT);
      }
    }

    if (caseDetail != null) {
      caseUpdateRq.setCaseReferenceNumber(caseDetail.getCaseReferenceNumber());
    }
    if (app != null) {
      caseUpdateRq.setApplicationDetails(CaseToEBSCaseConverter
          .convertToEBSApplicationDetails(app, caseDetail, userInfo.getCcmsUser()));
      caseUpdateRq.setPriorAuthorities(CaseToEBSCaseConverter.addAmendmentPriorAuthorities(app, caseDetail));

      boolean hasLinkedCases = !app.getLinkedCases().isEmpty();
      if (hasLinkedCases) {
        caseUpdateRq.setLinkedCases(
            CaseToEBSCaseConverter.convertToEBSLinkedCasesUpdate(app.getLinkedCases()));
      }
    }

    //To add caseDocs for uploaded document
    if (app != null || caseOutcome != null) {
      caseUpdateRq.setCaseDocs(convertToEbsCaseDocs(app, caseOutcome));
    }

    // MY UPDATES


//    if (caseAdd.getCaseReferenceNumber() != null) {
//      caseUpdateRq.setCaseReferenceNumber(caseAdd.getCaseReferenceNumber());
//    }
//
//    if (caseAdd.getCaseDetails() != null) {
//      CaseDetailsAdd caseDetails = caseAdd.getCaseDetails();
//
//      caseUpdateRq.setActualCaseStatus(caseUpdateRq.getActualCaseStatus());
//      caseUpdateRq.setAwards(caseDetails.getAwards());
//      caseUpdateRq.setCaseDocs(caseDetails.getCaseDocs());
//      caseUpdateRq.setDischargeStatus(caseDetails.getDischargeStatus());
//      caseUpdateRq.setLinkedCases(caseDetails.getLinkedCases());
//      caseUpdateRq.setLegalHelpCosts(caseDetails.getLegalHelpCosts());
//      caseUpdateRq.setMessages(caseUpdateRq.getMessages());
//      caseUpdateRq.setCaseDocs(caseDetails.getCaseDocs());
//      caseUpdateRq.setNotifications(caseUpdateRq.getNotifications());
//      caseUpdateRq.setOutcomes(caseUpdateRq.getOutcomes());
//      caseUpdateRq.setPreCertificateCosts(caseDetails.getPreCertificateCosts());
//      caseUpdateRq.setPriorAuthorities(caseDetails.getPriorAuthorities());
//      caseUpdateRq.setRecordHistory(caseDetails.getRecordHistory());
//      caseUpdateRq.setUndertakings(caseUpdateRq.getUndertakings());
//      caseUpdateRq.setUpdateMsgType(caseUpdateRq.getUpdateMsgType());
//    }

    JAXBElement<CaseUpdateRS> response =
            (JAXBElement<CaseUpdateRS>) getWebServiceTemplate()
                    .marshalSendAndReceive(
                            serviceUrl,
                            CASE_FACTORY.createCaseUpdateRQ(caseUpdateRq),
                            new SoapActionCallback(soapAction));

    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());
    return response.getValue();
  }

  private JAXBElement<CaseInqRS> retrieveCaseDetails(String soapAction, CaseInqRQ caseInqRq) {
    JAXBElement<CaseInqRS> response =
        (JAXBElement<CaseInqRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                serviceUrl,
                CASE_FACTORY.createCaseInqRQ(caseInqRq),
                new SoapActionCallback(soapAction));

    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());
    return response;
  }
}
