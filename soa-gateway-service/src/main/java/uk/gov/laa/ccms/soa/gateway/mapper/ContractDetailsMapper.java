package uk.gov.laa.ccms.soa.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.laa.ccms.soa.gateway.model.ContractDetails;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;


/**
 * Maps between the SoA gateway's contract data model and the Legal Services endpoint's domain
 * model.
 */
@Mapper(componentModel = "spring")
public interface ContractDetailsMapper {
  @Mapping(target = "contracts", source = "contractDetails.contractDetail")
  ContractDetails toContractDetails(ContractDetailsInqRS contractDetailsInqRs);
}
