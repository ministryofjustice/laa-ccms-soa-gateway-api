package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;

/**
 * Mapper for transforming reference data related to cases.
 *
 * <p>Utilizes the MapStruct framework to map between the Legal Services endpoint data model and the
 * SoA gateway's {@link CaseReferenceSummary} model.</p>
 */
@Mapper(componentModel = "spring")
public interface ReferenceDataMapper {

  @Mapping(target = "caseReferenceNumber",
          expression = "java( mapFirstResultToString(response.getResults()) )")
  CaseReferenceSummary toCaseReferenceSummary(ReferenceDataInqRS response);

  /**
   * Maps the first result from a list of strings.
   *
   * @param results List of results to be mapped.
   * @return The first result as a string, or null if the list is empty or null.
   */
  default String mapFirstResultToString(List<String> results) {
    return Optional.ofNullable(results)
            .flatMap(r -> r.stream().findFirst())
            .orElse(null);
  }
}
