package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ProviderRequestAddRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ProviderRequestAddRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderRequestElementType;

/** Client for handling provider requests to the CCMS case management service. */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class ProviderRequestClient extends AbstractSoaClient {

  private final String serviceName;
  private final String serviceUrl;
  private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link ProviderRequestClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName The name of the case management service.
   * @param serviceUrl The URL endpoint for the case management service.
   */
  public ProviderRequestClient(
      final WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.provider-request.service-name}") final String serviceName,
      @Value("${laa.ccms.soa-gateway.provider-request.service-url}") final String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Register a new Provider Request in CCMS.
   *
   * @param loggedInUserId - the logged in UserId
   * @param loggedInUserType - the logged in UserType
   * @param providerRequestElementType - the provider request element type
   * @return Response object containing the result of the provider request creation.
   */
  public ProviderRequestAddRS submitProviderRequest(
      final String loggedInUserId,
      final String loggedInUserType,
      final ProviderRequestElementType providerRequestElementType) {

    final String soapAction = String.format("%s/", serviceName);
    final ProviderRequestAddRQ providerRequestAddRq = CASE_FACTORY.createProviderRequestAddRQ();
    providerRequestAddRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    providerRequestAddRq.setRequestDetails(providerRequestElementType);

    final JAXBElement<ProviderRequestAddRS> response =
        (JAXBElement<ProviderRequestAddRS>)
            getWebServiceTemplate()
                .marshalSendAndReceive(
                    serviceUrl,
                    CASE_FACTORY.createProviderRequestAddRQ(providerRequestAddRq),
                    new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());
    return response.getValue();
  }
}
