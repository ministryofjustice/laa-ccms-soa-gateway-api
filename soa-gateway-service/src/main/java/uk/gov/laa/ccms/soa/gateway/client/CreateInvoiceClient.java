package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.ObjectFactory;

/**
 * Client for submitting invoices to the CCMS finance (payables) service.
 *
 * <p>This client calls the EBS {@code CreateInvoice} operation, which accepts either a bill or a
 * payment on account within its invoice details.
 *
 * <p>Note: like {@code GetCaseStmtOfAccount}, the {@code CreateInvoice} WSDL declares its
 * soapAction as the bare service namespace (its single operation is generically named {@code
 * process}), so the soapAction is the service name itself with no operation suffix.
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class CreateInvoiceClient extends AbstractSoaClient {

  private final String serviceName;

  private final String serviceUrl;

  private static final ObjectFactory BILLING_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link CreateInvoiceClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName The name of the create invoice service.
   * @param serviceUrl The URL endpoint for the create invoice service.
   */
  public CreateInvoiceClient(
      final WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.create-invoice.service-name}") final String serviceName,
      @Value("${laa.ccms.soa-gateway.create-invoice.service-url}") final String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Submit an invoice to CCMS.
   *
   * @param loggedInUserId - the logged in UserId
   * @param loggedInUserType - the logged in UserType
   * @param invoiceDetails - the bill or payment on account to submit
   * @return Response object containing the result of the invoice submission.
   */
  public InvoiceAddRS createInvoice(
      final String loggedInUserId,
      final String loggedInUserType,
      final InvoiceAddRQ.InvoiceDetails invoiceDetails) {

    final String soapAction = serviceName;
    final InvoiceAddRQ invoiceAddRq = BILLING_FACTORY.createInvoiceAddRQ();
    invoiceAddRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    invoiceAddRq.setInvoiceDetails(invoiceDetails);

    final JAXBElement<InvoiceAddRS> response =
        (JAXBElement<InvoiceAddRS>)
            getWebServiceTemplate()
                .marshalSendAndReceive(
                    serviceUrl,
                    BILLING_FACTORY.createInvoiceAddRQ(invoiceAddRq),
                    new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());
    return response.getValue();
  }
}
