package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.DocumentClient;
import uk.gov.laa.ccms.soa.gateway.mapper.CommonMapper;
import uk.gov.laa.ccms.soa.gateway.mapper.DocumentMapper;
import uk.gov.laa.ccms.soa.gateway.model.BaseDocument;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentUploadRS;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;

/**
 * Service responsible for handling document operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService extends AbstractSoaService {

  private final DocumentClient documentClient;

  private final DocumentMapper documentMapper;

  private final CommonMapper commonMapper;

  /**
   * Register a new document in EBS, and get a registered document id.
   *
   * @param soaGatewayUserLoginId  The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole     The user role in the SOA Gateway.
   * @param document               The document details to register
   * @return                       A {@link ClientTransactionResponse} object containing the
   *                               transaction id and ebs registered document id.
   */
  public ClientTransactionResponse registerDocument(
          final String soaGatewayUserLoginId,
          final String soaGatewayUserRole,
          final BaseDocument document) {

    final DocumentUploadRS response = documentClient.registerDocument(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        documentMapper.toDocumentUploadElementType(document));

    return documentMapper.toClientTransactionResponse(response);
  }

  /**
   * Upload a new document in EBS, including its file data.
   *
   * @param soaGatewayUserLoginId  The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole     The user role in the SOA Gateway.
   * @param documentId             The registered id of the document to download.
   * @param document               The document details to register
   * @return                       A {@link ClientTransactionResponse} object containing the
   *                               transaction id
   */
  public ClientTransactionResponse uploadDocument(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String documentId,
      final Document document) {

    final DocumentUploadRS response = documentClient.registerDocument(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        documentMapper.toDocumentUploadElementType(documentId, document));

    return documentMapper.toClientTransactionResponse(response);
  }

  /**
   * Download a document.
   *
   * @param soaGatewayUserLoginId  The user login ID for the SOA Gateway.
   * @param soaGatewayUserRole     The user role in the SOA Gateway.
   * @param documentId             The registered id of the document to download.
   * @return                       A {@link Document} object containing the document data.
   */
  public Document downloadDocument(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String documentId) {

    final DocumentElementType response = documentClient.downloadDocument(
        soaGatewayUserLoginId,
        soaGatewayUserRole,
        documentId);

    return commonMapper.toDocument(response);
  }


}
