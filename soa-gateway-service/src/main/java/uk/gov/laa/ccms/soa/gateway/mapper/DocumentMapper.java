package uk.gov.laa.ccms.soa.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.laa.ccms.soa.gateway.model.BaseDocument;
import uk.gov.laa.ccms.soa.gateway.model.ClientTransactionResponse;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.DocumentUploadRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.DocumentUploadElementType;

/** Mapper interface for converting between Document representations. */
@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface DocumentMapper {

  @Mapping(target = "channel", constant = "E")
  @Mapping(target = "CCMSDocumentID", ignore = true)
  @Mapping(target = "binData", ignore = true)
  @Mapping(target = "documentLink", ignore = true)
  DocumentUploadElementType toDocumentUploadElementType(final BaseDocument document);

  @Mapping(target = "CCMSDocumentID", source = "documentId")
  @Mapping(target = ".", source = "document")
  @Mapping(target = "binData", source = "document.fileData")
  DocumentUploadElementType toDocumentUploadElementType(
      final String documentId, final Document document);

  @Mapping(target = "CCMSDocumentID", ignore = true)
  @Mapping(target = "binData", source = "document.fileData")
  DocumentUploadElementType toDocumentUploadElementType(final Document document);

  @Mapping(target = "transactionId", source = "headerRS.transactionID")
  @Mapping(target = "referenceNumber", source = "documentID")
  ClientTransactionResponse toClientTransactionResponse(final DocumentUploadRS documentUploadRs);
}
