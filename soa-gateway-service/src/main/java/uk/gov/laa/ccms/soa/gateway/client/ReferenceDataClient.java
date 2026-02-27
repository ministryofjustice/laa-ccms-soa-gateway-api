package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ObjectFactory;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRQ;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.KeyType;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.SearchContext;

/**
 * Client for interacting with the Reference Data service. Provides methods to query and retrieve
 * reference data relevant to case management.
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class ReferenceDataClient extends AbstractSoaClient {

  private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

  /**
   * Constructor for initializing the ReferenceDataClient with necessary dependencies.
   *
   * @param webServiceTemplate Template for constructing SOAP web service calls.
   * @param serviceName The name of the Reference Data service.
   * @param serviceUrl The URL endpoint for the Reference Data service.
   */
  public ReferenceDataClient(
      final WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.reference-data.service-name}") final String serviceName,
      @Value("${laa.ccms.soa-gateway.reference-data.service-url}") final String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Fetches case reference data.
   *
   * @param loggedInUserId ID of the logged-in user.
   * @param loggedInUserType Type of the logged-in user.
   * @return The reference data inquiry response containing case reference details.
   */
  public ReferenceDataInqRS getCaseReference(
      final String loggedInUserId, final String loggedInUserType) {

    final String soapAction = String.format("%s/process", serviceName);
    ReferenceDataInqRQ referenceDataInqRq = CASE_FACTORY.createReferenceDataInqRQ();
    referenceDataInqRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    List<SearchContext> contexts = referenceDataInqRq.getSearchCriteria();
    SearchContext context = new SearchContext();
    List<KeyType> keyTypes = context.getContextKey();
    keyTypes.add(KeyType.CASE_REFERENCE_NUMBER);
    contexts.add(context);

    JAXBElement<ReferenceDataInqRS> response =
        (JAXBElement<ReferenceDataInqRS>)
            getWebServiceTemplate()
                .marshalSendAndReceive(
                    serviceUrl,
                    CASE_FACTORY.createReferenceDataInqRQ(referenceDataInqRq),
                    new SoapActionCallback(soapAction));

    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }
}
