package uk.gov.laa.ccms.soa.gateway.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
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

  private static final Predicate<Field> isDomain = field -> field.getType().getPackage() != null;
  private static final Predicate<Field> isPackage = field -> field.getType()
      .getPackage().getName().equals("uk.gov.laa.ccms.soa.gateway.model");
  private static final Predicate<Field> isInDomainPackage = isDomain.and(isPackage);

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
    List<Field> allFields = getAllFields(clazz);

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
        } else {
          list.sort(comparator.reversed());
        }
      }
    }
    List<T> sublist = new ArrayList<>(list.subList(start, end));
    return new PageImpl<>(sublist, pageable, list.size());
  }

  /**
   * Get the fields from the outer and nested objects in case they can be sorted on.
   *
   * @param clazz the class type from the domain.
   * @return the list of fields and nested fields within the class.
   */
  private static List<Field> getAllFields(Class<?> clazz) {
    List<Field> allFields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

    List<Field> nestedDomainObjectFields = allFields
        .stream()
        .filter(isInDomainPackage)
        .toList();

    if (!nestedDomainObjectFields.isEmpty()) {
      allFields.removeAll(nestedDomainObjectFields);
      nestedDomainObjectFields.stream()
          .flatMap(field -> Stream.of(field.getType().getDeclaredFields()))
          .filter(field -> !isInDomainPackage.test(field))
          .forEachOrdered(allFields::add);
    }
    return allFields;
  }




}





