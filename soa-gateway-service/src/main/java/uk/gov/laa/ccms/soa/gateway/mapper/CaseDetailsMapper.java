package uk.gov.laa.ccms.soa.gateway.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
import uk.gov.laa.ccms.soa.gateway.model.CaseSummary;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseList;


/**
 * Mapper interface for converting case data between different representations.
 */
@Mapper(componentModel = "spring")
public interface CaseDetailsMapper {

  List<CaseSummary> toCaseSummaryList(List<CaseList> caseList);


  /**
   * Converts the {@link CaseInqRS} object to a list of {@link CaseSummary} objects.
   *
   * <p>This default method takes a {@link CaseInqRS} instance and extracts the list of
   * cases from it. It then delegates the conversion of this list to the
   * {@link #toCaseSummaryList(List)} method. If the {@link CaseInqRS} instance is null,
   * or if it does not contain any case data, an empty list is returned.</p>
   *
   * @param clientInqRs The case inquiry response to be converted.
   * @return A list of {@link CaseSummary} objects or an empty list if no case data is available.
   */
  default List<CaseSummary> toCaseSummaryList(CaseInqRS clientInqRs) {
    if (clientInqRs != null) {
      List<CaseList> caseList = clientInqRs.getCaseList();
      if (caseList != null) {
        return toCaseSummaryList(caseList);
      }
    }
    return Collections.emptyList();
  }

  CaseDetails toCaseDetails(Page<CaseSummary> page);

  @Mapping(target = "caseStatusDisplay", source = "displayStatus")
  CaseSummary toCaseSummary(CaseList clientList);
}
