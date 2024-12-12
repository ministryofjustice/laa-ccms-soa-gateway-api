package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.DocumentsApi;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.CoverSheet;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.laa.ccms.soa.gateway.service.DocumentService;

/**
 * Controller that manages the upload and retrieval of documents.
 *
 * <p>Acts as an implementation of the {@link DocumentsApi}, utilizing the
 * {@link DocumentService} to register, upload and fetch relevant document data.</p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class DocumentController implements DocumentsApi {

  private final DocumentService documentService;

  /**
   * Register a document in Ebs.
   *
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param document  (required) - the document details to register.
   * @param notificationReference  The ID of the notification to which this document
   *                               is related.
   * @param caseReferenceNumber    The reference id of the case to which this document
   *                               is related.
   * @return ResponseEntity wrapping the transaction id and registered document id.
   */
  @Override
  public ResponseEntity<ClientTransactionResponse> registerDocument(
          final String soaGatewayUserLoginId,
          final String soaGatewayUserRole,
          final Document document,
          final String notificationReference,
          final String caseReferenceNumber) {

    try {
      final ClientTransactionResponse response = documentService.registerDocument(
              soaGatewayUserLoginId,
              soaGatewayUserRole,
              document,
              notificationReference,
              caseReferenceNumber);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("DocumentController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Upload a document in Ebs.
   *
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @param document  (required) - the document data to upload.
   * @param notificationReference  The ID of the notification to which this document
   *                               is related.
   * @return ResponseEntity wrapping the transaction id and registered document id.
   */
  @Override
  public ResponseEntity<ClientTransactionResponse> uploadDocument(
      final String documentId,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final Document document,
      final String notificationReference,
      final String caseReferenceNumber) {

    try {
      final ClientTransactionResponse response = documentService.uploadDocument(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          documentId,
          document,
          notificationReference,
          caseReferenceNumber);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("DocumentController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Download a document from Ebs.
   *
   * @param documentId  (required) - the registered id of the document to download.
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @return ResponseEntity wrapping the {@link Document} data.
   */
  @Override
  public ResponseEntity<Document> downloadDocument(
      final String documentId,
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole) {

    try {
      Document response = documentService.downloadDocument(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          documentId);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("DocumentController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Download a document cover sheet from Ebs.
   *
   * @param documentId  (required) - the registered id of the document to download the
   *                    cover sheet for.
   * @param soaGatewayUserLoginId  (required) - the user requesting the data.
   * @param soaGatewayUserRole  (required) - the user role requesting the data.
   * @return ResponseEntity wrapping the {@link CoverSheet} data.
   */
  @Override
  public ResponseEntity<CoverSheet> downloadDocumentCoverSheet(String documentId,
      String soaGatewayUserLoginId, String soaGatewayUserRole) {

    try {
      CoverSheet response = documentService.downloadDocumentCoverSheet(
          soaGatewayUserLoginId,
          soaGatewayUserRole,
          documentId);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("DocumentController caught exception", e);
      return ResponseEntity.internalServerError().build();
    }
  }

}
