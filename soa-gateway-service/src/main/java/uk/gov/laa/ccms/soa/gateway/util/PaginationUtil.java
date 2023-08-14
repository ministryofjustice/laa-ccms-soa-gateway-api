package uk.gov.laa.ccms.soa.gateway.util;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Utility class that provides helper functions for pagination.
 *
 * <p>This class offers a set of methods to help in the pagination of data, especially
 * when working with lists and {@link Page} objects.</p>
 */
public final class PaginationUtil {
  private PaginationUtil() {}

  /**
   * Paginates a given list based on the provided {@link Pageable} object.
   *
   * <p>This method takes a list and a {@link Pageable} object to return a paginated
   * subset of the list wrapped in a {@link Page} object.</p>
   *
   * @param <T>       The type of the elements within the list.
   * @param pageable  The {@link Pageable} object containing pagination instructions.
   * @param list      The list of items to be paginated.
   * @return          A {@link Page} object containing a paginated subset of the list.
   */
  public static <T> Page<T> paginateList(final Pageable pageable, List<T> list) {
    int first = Math.min(Long.valueOf(pageable.getOffset()).intValue(), list.size());
    int last = Math.min(first + pageable.getPageSize(), list.size());
    return new PageImpl<>(list.subList(first, last), pageable, list.size());
  }
}
