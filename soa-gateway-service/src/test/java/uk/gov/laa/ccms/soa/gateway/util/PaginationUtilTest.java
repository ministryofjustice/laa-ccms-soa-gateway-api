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
import uk.gov.laa.ccms.soa.gateway.model.CaseSummary;
import uk.gov.laa.ccms.soa.gateway.model.Notification;

class PaginationUtilTest {




  @Test
  void testSortDescendingCorrectInPaginationUtil() {
    Pageable pageable = PageRequest.of(0, 10,
        Sort.by("assignDate").descending());
    List<Notification> notificationList = new ArrayList<>();
    notificationList.add(new Notification()
        .clientReferenceNumber("12345"));
    notificationList.add(new Notification()
        .clientReferenceNumber("11234"));
    Page<Notification> notificationPage = PaginationUtil.paginateList(pageable,notificationList);
    assertEquals(2, notificationPage.getTotalElements());
    assertEquals("11234", notificationPage.getContent().get(0).getClientReferenceNumber());

  }

  @Test
  void testSortAscendingCorrectInPaginationUtil() {
    Pageable pageable = PageRequest.of(0, 10,
        Sort.by("clientReferenceNumber").ascending());
    List<Notification> notificationList = new ArrayList<>();
    notificationList.add(new Notification()
        .clientReferenceNumber("12345"));
    notificationList.add(new Notification()
        .clientReferenceNumber("11234"));
    Page<Notification> notificationPage = PaginationUtil.paginateList(pageable,notificationList);
    assertEquals(2, notificationPage.getTotalElements());
    assertEquals("12345", notificationPage.getContent().get(1).getClientReferenceNumber());
  }

  @Test
  void testSortEmptyListInPaginationUtil() {
    Pageable pageable = PageRequest.of(0, 10,
        Sort.by("clientReferenceNumber").ascending());
    List<Notification> notificationList = new ArrayList<>();

    Page<Notification> notificationPage = PaginationUtil.paginateList(pageable,notificationList);
    assertEquals(0, notificationPage.getTotalElements());
  }

  @Test
  void testInvalidSortThrowsException() {
    Pageable pageable = PageRequest.of(0, 10,
        Sort.by("chickenSoup").ascending());
    List<Notification> notificationList = new ArrayList<>();
    notificationList.add(new Notification()
        .clientReferenceNumber("12345"));
    notificationList.add(new Notification()
        .clientReferenceNumber("11234"));
   SoaGatewaySortingException soage = assertThrows(SoaGatewaySortingException.class,
        () -> PaginationUtil.paginateList(pageable,notificationList),
    "Expected PaginationUtils to throw exception, but didn't");
   assertTrue(soage.getLocalizedMessage().contains("Invalid sort."));
  }

  @Test
  void testSortOnOtherDomainObjects() {
    List<CaseSummary> summery = new ArrayList<>();
    summery.add(buildCaseSummary());
    CaseSummary caseSummary = buildCaseSummary()
        .caseReferenceNumber("1234678905")
        .feeEarnerName("flobbit");
    summery.add(caseSummary);

    Pageable pageable = PageRequest.of(0, 10,
        Sort.by("feeEarnerName").ascending());
    Page<CaseSummary> caseSummariesPage = PaginationUtil.paginateList(pageable,summery);
    assertEquals("feeearner", caseSummariesPage.getContent().get(0).getFeeEarnerName());

  }

  private uk.gov.laa.ccms.soa.gateway.model.CaseSummary buildCaseSummary(){
    return new uk.gov.laa.ccms.soa.gateway.model.CaseSummary()
        .caseReferenceNumber("1234567890")
        .providerCaseReferenceNumber("ABCDEF")
        .caseStatusDisplay("APPL")
        .feeEarnerName("feeearner")
        .categoryOfLaw("CAT1");
  }


}
