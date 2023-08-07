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

@Mapper(componentModel = "spring")
public interface CaseDetailsMapper {

    List<CaseSummary> toCaseSummaryList(List<CaseList> caseList);

    CaseDetails toCaseDetails(Page<CaseSummary> page);

    default List<CaseSummary> toCaseSummaryList(CaseInqRS clientInqRS) {
        if (clientInqRS != null){
            List<CaseList> caseList = clientInqRS.getCaseList();
            if (caseList != null) {
                return toCaseSummaryList(caseList);
            }
        }
        return Collections.emptyList();
    }

    @Mapping(target = "caseStatusDisplay", source = "displayStatus")
    CaseSummary toCaseSummary(CaseList clientList);
}
