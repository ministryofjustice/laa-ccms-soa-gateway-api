package uk.gov.laa.ccms.soa.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ReferenceDataMapper {

    @Mapping(target = "caseReferenceNumber", expression = "java( mapFirstResultToString(response.getResults()) )")
    CaseReferenceSummary toCaseReferenceSummary(ReferenceDataInqRS response);

    default String mapFirstResultToString(List<String> results) {
        return Optional.ofNullable(results)
                .flatMap(r -> r.stream().findFirst())
                .orElse(null);
    }
}
