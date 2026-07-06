package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.CaseSOAInqRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.CaseSOAInqRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.ObjectFactory;

/**
 * Provides a client interface for retrieving a Case Statement of Account from the SOA-based system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and calls
 * the EBS {@code GetCaseStmtOfAccount} operation. Service name and URL details are injected at
 * runtime.
 *
 * <p>Note: unlike the Case Management services, the {@code GetCaseStmtOfAccount} WSDL declares its
 * soapAction as the bare service namespace (its single operation is generically named
 * {@code process}), so the soapAction is the service name itself with no operation suffix.
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class CaseStatementOfAccountClient extends AbstractSoaClient {

  private final String serviceName;

  private final String serviceUrl;

  private static final ObjectFactory SOA_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link CaseStatementOfAccountClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName The name of the statement of account service.
   * @param serviceUrl The URL endpoint for the statement of account service.
   */
  public CaseStatementOfAccountClient(
      WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.case-statement-of-account.service-name}") String serviceName,
      @Value("${laa.ccms.soa-gateway.case-statement-of-account.service-url}") String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Retrieve the statement of account for the supplied case reference number.
   *
   * @param loggedInUserId - the logged in UserId
   * @param loggedInUserType - the logged in UserType
   * @param caseReferenceNumber - the case reference number
   * @return Response object containing the statement of account for a single Case
   */
  public CaseSOAInqRS getCaseStatementOfAccount(
      String loggedInUserId, String loggedInUserType, String caseReferenceNumber) {

    final String soapAction = serviceName;
    CaseSOAInqRQ caseSoaInqRq = SOA_FACTORY.createCaseSOAInqRQ();
    caseSoaInqRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    caseSoaInqRq.setRecordCount(createRecordCount(1));
    caseSoaInqRq.setCaseReferenceNumber(caseReferenceNumber);

    JAXBElement<CaseSOAInqRS> response =
        (JAXBElement<CaseSOAInqRS>)
            getWebServiceTemplate()
                .marshalSendAndReceive(
                    serviceUrl,
                    SOA_FACTORY.createCaseSOAInqRQ(caseSoaInqRq),
                    new SoapActionCallback(soapAction));

    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());
    return response.getValue();
  }
}
