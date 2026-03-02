package uk.gov.laa.ccms.soa.gateway.service;

import uk.gov.legalservices.enterprise.common._1_0.common.RecordCount;

/** Abstract Service class used for common methods for all soa services. */
public abstract class AbstractSoaService {

  /**
   * This method calculates the total number of elements from the record count. If the record count
   * or the fetched records are null, it returns the default total elements.
   *
   * @param recordCount The record count object which may contain the number of fetched records.
   * @param defaultTotalElements The default number of total elements to return if the record count
   *     is null or doesn't contain fetched records.
   * @return The number of fetched records if available, otherwise the default total elements.
   */
  protected int getTotalElementsFromRecordCount(
      final RecordCount recordCount, final int defaultTotalElements) {
    return (recordCount != null && recordCount.getRecordsFetched() != null)
        ? recordCount.getRecordsFetched().intValue()
        : defaultTotalElements;
  }
}
