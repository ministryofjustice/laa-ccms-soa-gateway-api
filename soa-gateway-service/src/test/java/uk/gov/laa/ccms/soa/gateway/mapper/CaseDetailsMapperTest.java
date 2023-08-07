package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
import uk.gov.laa.ccms.soa.gateway.model.CaseSummary;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseList;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Client;

@ExtendWith(MockitoExtension.class)
public class CaseDetailsMapperTest {

    @InjectMocks
    CaseDetailsMapperImpl caseDetailsMapper;

    @Test
    public void testToCaseDetails() {
        List<CaseSummary> caseSummaryList = Collections.singletonList(new CaseSummary());
        Page<CaseSummary> caseDetailPage = new PageImpl<>(caseSummaryList, Pageable.unpaged(), caseSummaryList.size());

        CaseDetails result = caseDetailsMapper.toCaseDetails(caseDetailPage);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testToCaseSummaryList() {
        CaseInqRS response = new CaseInqRS();
        response.getCaseList().add(buildCaseList());

        List<CaseSummary> result = caseDetailsMapper.toCaseSummaryList(response);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testToCaseSummary() {
        CaseList caseList = buildCaseList();

        CaseSummary result = caseDetailsMapper.toCaseSummary(caseList);

        assertNotNull(result);
        assertEquals(caseList.getCaseReferenceNumber(), result.getCaseReferenceNumber());
        assertEquals(caseList.getProviderCaseReferenceNumber(), result.getProviderCaseReferenceNumber());
        assertNotNull(result.getClient());
        assertEquals(caseList.getClient().getClientReferenceNumber(), result.getClient().getClientReferenceNumber());
        assertEquals(caseList.getClient().getFirstName(), result.getClient().getFirstName());
        assertEquals(caseList.getClient().getSurname(), result.getClient().getSurname());
        assertEquals(caseList.getDisplayStatus(), result.getCaseStatusDisplay());
        assertEquals(caseList.getCategoryOfLaw(), result.getCategoryOfLaw());
        assertEquals(caseList.getFeeEarnerName(), result.getFeeEarnerName());
    }

    private CaseList buildCaseList() {
        CaseList caseList = new CaseList();
        caseList.setCaseReferenceNumber("caseref");
        caseList.setProviderCaseReferenceNumber("provcaseref");
        caseList.setCategoryOfLaw("catoflaw");
        caseList.setFeeEarnerName("feeearner");
        caseList.setDisplayStatus("status");
        caseList.setClient(new Client());
        caseList.getClient().setClientReferenceNumber("clientrefnum");
        caseList.getClient().setFirstName("firstname");
        caseList.getClient().setSurname("surname");
        return caseList;
    }
}
