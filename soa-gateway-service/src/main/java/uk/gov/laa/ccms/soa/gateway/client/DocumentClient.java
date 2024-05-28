package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentDownloadRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentDownloadRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentUploadRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentUploadRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.DocumentUploadElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;

/**
 * Provides a client interface for interacting with Document Services in the SOA-based
 * system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on document services. It allows for the registering, upload
 * and download of documents. Service name and URL details are injected at runtime.</p>
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class DocumentClient extends AbstractSoaClient {

  private final String serviceName;

  private final String serviceUrl;

  private static final ObjectFactory CASE_BIM_FACTORY = new ObjectFactory();

  /**
   * Value to indicate that the document is not related to a notification.
   * Passing this value, and omitting a documentId will also trigger SOA to route
   * the message to RegisterDocument.
   */
  public static final String NO_RELATED_NOTIFICATION = "-1";


  /**
   * Constructs a new {@link DocumentClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName        The name of the documentt service.
   * @param serviceUrl         The URL endpoint for the document service.
   */
  public DocumentClient(WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.document.service-name}") String serviceName,
      @Value("${laa.ccms.soa-gateway.document.service-url}") String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Register a new document in Ebs to receive a document id.
   *
   * @param loggedInUserId      - the logged in UserId
   * @param loggedInUserType    - the logged in UserType
   * @param documentUploadElementType - the document upload details
   * @return The response from the register request.
   */
  public DocumentUploadRS registerDocument(
      final String loggedInUserId,
      final String loggedInUserType,
      final DocumentUploadElementType documentUploadElementType) {

    return this.uploadDocument(
        loggedInUserId,
        loggedInUserType,
        NO_RELATED_NOTIFICATION,
        documentUploadElementType);
  }

  /**
   * Upload a document.
   *
   * @param loggedInUserId      - the logged in UserId
   * @param loggedInUserType    - the logged in UserType
   * @param notificationReference - the notification reference number
   * @param documentUploadElementType - the document upload details
   * @return The response from the upload request.
   */
  public DocumentUploadRS uploadDocument(
      final String loggedInUserId,
      final String loggedInUserType,
      final String notificationReference,
      final DocumentUploadElementType documentUploadElementType) {

    final String soapAction = String.format("%s/UploadDocument", serviceName);
    DocumentUploadRQ documentUploadRq = CASE_BIM_FACTORY.createDocumentUploadRQ();
    documentUploadRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    documentUploadRq.setNotificationID(notificationReference);

    documentUploadRq.setDocument(documentUploadElementType);

    JAXBElement<DocumentUploadRS> response =
        (JAXBElement<DocumentUploadRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                serviceUrl,
                CASE_BIM_FACTORY.createDocumentUploadRQ(documentUploadRq),
                new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }

  /**
   * Download a document by ebs registered document id.
   *
   * @param loggedInUserId      - the logged in UserId
   * @param loggedInUserType    - the logged in UserType
   * @param registeredDocumentId - the id of the ebs-registered document
   * @return Response object containing the full details for a single Case
   */
  public DocumentElementType downloadDocument(
      final String loggedInUserId,
      final String loggedInUserType,
      final String registeredDocumentId) {

    final String soapAction = String.format("%s/DownloadDocument", serviceName);
    DocumentDownloadRQ documentDownloadRq = CASE_BIM_FACTORY.createDocumentDownloadRQ();
    documentDownloadRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    documentDownloadRq.setDocumentID(registeredDocumentId);

    JAXBElement<DocumentDownloadRS> response =
        (JAXBElement<DocumentDownloadRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                serviceUrl,
                CASE_BIM_FACTORY.createDocumentDownloadRQ(documentDownloadRq),
                new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

    return response.getValue().getDocument();
  }
}
