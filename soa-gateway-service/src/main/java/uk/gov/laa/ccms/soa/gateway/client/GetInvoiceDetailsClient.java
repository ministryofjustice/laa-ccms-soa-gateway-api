package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.GetInvoiceDetailsRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.GetInvoiceDetailsRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.ObjectFactory;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.ReturnDataType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.SearchConditionType;

/**
 * Client for retrieving the assessment data held against an invoice from the CCMS finance
 * (payables) service.
 *
 * <p>Note: unlike {@code CreateInvoice}, whose single operation is generically named {@code
 * process}, this WSDL declares a named {@code GetInvoiceData} operation whose soapAction is the
 * bare operation name rather than the service namespace.
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class GetInvoiceDetailsClient extends AbstractSoaClient {

  /**
   * The soapAction declared by the GetInvoiceDetails WSDL. It is the bare operation name, not the
   * service namespace, so it is fixed by the contract rather than configured per environment.
   */
  private static final String SOAP_ACTION = "GetInvoiceData";

  private final String serviceName;

  private final String serviceUrl;

  private static final ObjectFactory BILLING_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link GetInvoiceDetailsClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName The name of the get invoice details service.
   * @param serviceUrl The URL endpoint for the get invoice details service.
   */
  public GetInvoiceDetailsClient(
      final WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.get-invoice-details.service-name}") final String serviceName,
      @Value("${laa.ccms.soa-gateway.get-invoice-details.service-url}") final String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Retrieve the assessment data held against an invoice.
   *
   * @param loggedInUserId - the logged in UserId
   * @param loggedInUserType - the logged in UserType
   * @param billingId - the billing id to retrieve the assessment data for
   * @return Response object containing the assessment data for the invoice.
   */
  public GetInvoiceDetailsRS getInvoiceData(
      final String loggedInUserId, final String loggedInUserType, final String billingId) {

    final SearchConditionType searchCondition = new SearchConditionType();
    searchCondition.setBillingId(billingId);

    final GetInvoiceDetailsRQ getInvoiceDetailsRq = BILLING_FACTORY.createGetInvoiceDetailsRQ();
    getInvoiceDetailsRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    getInvoiceDetailsRq.setSearchCondition(searchCondition);
    // BasicAssessmentData is the only value the schema permits.
    getInvoiceDetailsRq.setReturnData(ReturnDataType.BASIC_ASSESSMENT_DATA);

    final JAXBElement<GetInvoiceDetailsRS> response =
        (JAXBElement<GetInvoiceDetailsRS>)
            getWebServiceTemplate()
                .marshalSendAndReceive(
                    serviceUrl,
                    BILLING_FACTORY.createGetInvoiceDetailsRQ(getInvoiceDetailsRq),
                    new SoapActionCallback(SOAP_ACTION));

    // Check and throw exception if the SOA call was not successful
    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());
    return response.getValue();
  }
}
