package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseAdd;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseInfo;

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
   * Retrieves the status of a case addition or update based on the provided transaction ID.
   *
   * <p>This method communicates with the service endpoint to fetch the status related to a
   * specific transaction pertaining to case addition or update. If the SOA call is not
   * successful, an exception will be thrown.</p>
   *
   * @param loggedInUserId   The ID of the user who is currently logged in.
   * @param loggedInUserType The type of the user who is currently logged in.
   * @param transactionId    The unique ID of the transaction for which the status is to be
   *                         fetched.
   * @return CaseAddUpdtStatusRS The response object containing the status of the case addition
   *         or update.
   */
  public CaseAddUpdtStatusRS getCaseTransactionStatus(
      final String loggedInUserId,
      final String loggedInUserType,
      final String transactionId
  ) {
    final String soapAction = String.format("%s/GetCaseTxnStatus", serviceName);
    CaseAddUpdtStatusRQ caseAddUpdtStatusRq = CASE_FACTORY.createCaseAddUpdtStatusRQ();
    caseAddUpdtStatusRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    caseAddUpdtStatusRq.setTransactionID(transactionId);

    JAXBElement<CaseAddUpdtStatusRS> response =
        (JAXBElement<CaseAddUpdtStatusRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                serviceUrl,
                CASE_FACTORY.createCaseAddUpdtStatusRQ(caseAddUpdtStatusRq),
                new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
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

    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());
    return response.getValue();
  }

  private JAXBElement<CaseInqRS> retrieveCaseDetails(String soapAction, CaseInqRQ caseInqRq) {
    JAXBElement<CaseInqRS> response =
        (JAXBElement<CaseInqRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                serviceUrl,
                CASE_FACTORY.createCaseInqRQ(caseInqRq),
                new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());
    return response;
  }
}
