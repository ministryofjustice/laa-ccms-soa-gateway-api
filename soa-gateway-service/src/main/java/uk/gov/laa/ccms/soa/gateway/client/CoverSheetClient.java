package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentCoverRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentCoverRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;

/**
 * Provides a client interface for interacting with Cover Sheet Services in the SOA-based system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on cover sheet services. It allows for the download of document cover
 * sheets. Service name and URL details are injected at runtime.
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class CoverSheetClient extends AbstractSoaClient {

  private final String serviceName;

  private final String serviceUrl;

  private static final ObjectFactory CASE_BIM_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link CoverSheetClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName The name of the cover sheet service.
   * @param serviceUrl The URL endpoint for the cover sheet service.
   */
  public CoverSheetClient(
      WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.cover-sheet.service-name}") String serviceName,
      @Value("${laa.ccms.soa-gateway.cover-sheet.service-url}") String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Download a document cover sheet by ebs registered document id.
   *
   * @param loggedInUserId - the logged in UserId
   * @param loggedInUserType - the logged in UserType
   * @param registeredDocumentId - the id of the ebs-registered document
   * @return Response object containing the cover sheet for the document.
   */
  public byte[] downloadDocumentCoverSheet(
      final String loggedInUserId,
      final String loggedInUserType,
      final String registeredDocumentId) {

    final String soapAction = String.format("%s/GetCoverSheet", serviceName);
    DocumentCoverRQ documentCoverRq = CASE_BIM_FACTORY.createDocumentCoverRQ();
    documentCoverRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    documentCoverRq.setExpectedDocumentID(registeredDocumentId);

    JAXBElement<DocumentCoverRS> response =
        (JAXBElement<DocumentCoverRS>)
            getWebServiceTemplate()
                .marshalSendAndReceive(
                    serviceUrl,
                    CASE_BIM_FACTORY.createDocumentCoverRQ(documentCoverRq),
                    new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());

    return response.getValue().getCoverSheetDoc();
  }
}
