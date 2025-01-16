package uk.gov.laa.ccms.soa.gateway.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uk.gov.laa.ccms.soa.gateway.SoaGatewaySortingException;
import uk.gov.laa.ccms.soa.gateway.model.OrganisationSummary;

class PaginationUtilTest {


  @Test
  void testSortDescendingCorrectInPaginationUtil() {
    Pageable pageable = PageRequest.of(0, 10,
        Sort.by("partyId").descending());
    List<OrganisationSummary> OrganisationSummaryList = new ArrayList<>();
    OrganisationSummaryList.add(new OrganisationSummary()
        .partyId("12345"));
    OrganisationSummaryList.add(new OrganisationSummary()
        .partyId("11234"));
    Page<OrganisationSummary> OrganisationSummaryPage = PaginationUtil.paginateList(pageable, OrganisationSummaryList, OrganisationSummaryList.size());
    assertEquals(2, OrganisationSummaryPage.getTotalElements());
    assertEquals("12345", OrganisationSummaryPage.getContent().get(0).getPartyId());

  }

  @Test
  void testSortAscendingCorrectInPaginationUtil() {
    Pageable pageable = PageRequest.of(0, 10,
        Sort.by("partyId").ascending());
    List<OrganisationSummary> OrganisationSummaryList = new ArrayList<>();
    OrganisationSummaryList.add(new OrganisationSummary()
        .partyId("12345"));
    OrganisationSummaryList.add(new OrganisationSummary()
        .partyId("11234"));
    Page<OrganisationSummary> OrganisationSummaryPage = PaginationUtil.paginateList(pageable,OrganisationSummaryList, OrganisationSummaryList.size());
    assertEquals(2, OrganisationSummaryPage.getTotalElements());
    assertEquals("12345", OrganisationSummaryPage.getContent().get(1).getPartyId());
  }

  @Test
  void testSortEmptyListInPaginationUtil() {
    Pageable pageable = PageRequest.of(0, 10,
        Sort.by("partyId").ascending());
    List<OrganisationSummary> OrganisationSummaryList = new ArrayList<>();

    Page<OrganisationSummary> OrganisationSummaryPage = PaginationUtil.paginateList(pageable,OrganisationSummaryList,
        OrganisationSummaryList.size());
    assertEquals(0, OrganisationSummaryPage.getTotalElements());
  }

  @Test
  void testInvalidSortThrowsException() {
    Pageable pageable = PageRequest.of(0, 10,
        Sort.by("chickenSoup").ascending());
    List<OrganisationSummary> OrganisationSummaryList = new ArrayList<>();
    OrganisationSummaryList.add(new OrganisationSummary()
        .partyId("12345"));
    OrganisationSummaryList.add(new OrganisationSummary()
        .partyId("11234"));
   SoaGatewaySortingException soage = assertThrows(SoaGatewaySortingException.class,
        () -> PaginationUtil.paginateList(pageable, OrganisationSummaryList, OrganisationSummaryList.size()),
    "Expected PaginationUtils to throw exception, but didn't");
   assertTrue(soage.getLocalizedMessage().contains("Error sorting comparator"));
  }

  @Test
  void testTotalElementsTooManyResultsFromSoa(){
    final Pageable pageable = PageRequest.of(0, 10,
        Sort.by("partyId").ascending());
    final List<OrganisationSummary> OrganisationSummaryList = new ArrayList<>();
    final int totalElements = 100;

    final Page<OrganisationSummary> OrganisationSummaryPage = PaginationUtil.paginateList(
        pageable,
        OrganisationSummaryList,
        totalElements);

    assertEquals(totalElements, OrganisationSummaryPage.getTotalElements());
  }

}
