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
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Outcomes;
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
   * @param caseUpdateRq        - the details of the case update request
   * @return Response object containing the result of the case creation.
   */
  public CaseUpdateRS updateCase(
      String loggedInUserId,
      String loggedInUserType,
      CaseUpdateRQ caseUpdateRq
  ) {
    final String soapAction = String.format("%s/UpdateCaseApplication", serviceName);
    caseUpdateRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

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
