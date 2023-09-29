package uk.gov.laa.ccms.soa.gateway.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uk.gov.laa.ccms.soa.gateway.SoaGatewaySortingException;

/**
 * Utility class that provides helper functions for pagination.
 *
 * <p>This class offers a set of methods to help in the pagination of data, especially
 * when working with lists and {@link Page} objects.</p>
 */
public final class PaginationUtil {

  private PaginationUtil() {
  }

  /**
   * Paginates a given list based on the provided {@link Pageable} object.
   *
   * <p>This method takes a list and a {@link Pageable} object to return a paginated
   * subset of the list wrapped in a {@link Page} object.</p>
   *
   * @param <T>      The type of the elements within the list.
   * @param pageable The {@link Pageable} object containing pagination instructions.
   * @param list     The list of items to be paginated.
   * @return A {@link Page} object containing a paginated and possibly sorted subset of the list.
   */
  public static <T> Page<T> paginateList(final Pageable pageable, List<T> list) {
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), list.size());


    /*
     Sort the list based on the Pageable Sort.
     Compare the fields of the class with the sort field and sort if that field exists.
    */
    if (!pageable.getSort().isSorted() || list.isEmpty()) {
      return new PageImpl<>(list, pageable, list.size());
    }
    Class<?> clazz = list.get(0).getClass();
    List<Field> allFields = Arrays.asList(clazz.getDeclaredFields());

    Comparator<T> comparator = Comparator.comparing(list::indexOf); // Default comparator
    for (Sort.Order order : pageable.getSort()) {
      Field toBeSorted = allFields
          .stream()
          .filter(field -> field.getName().equals(order.getProperty()))
          .findFirst()
          .orElseThrow(() -> new SoaGatewaySortingException(
              String.format("Invalid sort.  Sort field %s not found in class.",
                  order.getProperty())));
      if (order.getProperty().equals(toBeSorted.getName())) {
        if (order.isAscending()) {
          list.sort(comparator);
        } else  {
          list.sort(comparator.reversed());
        }
      }
    }
    List<T> sublist = new ArrayList<>(list.subList(start, end));
    return new PageImpl<>(sublist, pageable, list.size());
  }


}





