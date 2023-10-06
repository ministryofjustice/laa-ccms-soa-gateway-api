package uk.gov.laa.ccms.soa.gateway.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
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

  /**
   * Predicate field to determine if method is a getter.
   */
  private static final Predicate<Method> isGetter = method -> method.getName().startsWith("get");

  /**
   * Predictate to determine if method name contains property name.
   */
  private static Predicate<Method> containsPropertyName(String propertyName) {
    return method -> method.getName().toLowerCase().contains(propertyName.toLowerCase());
  }

  /**
   * Predicate that joins the above predicates into a single condition.
   */
  private static Predicate<Method> isMethodMatch(String propertyName) {
    return isGetter.and(containsPropertyName(propertyName));
  }

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
     if there's no sort in the request, just return the default page
    */
    if (!pageable.getSort().isSorted() || list.isEmpty()) {
      return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    /*
     Sort the list based on the Pageable Sort.
     Compare the methods of the class with the sort field and sort if that method exists.
     */
    Class<?> clazz = list.get(0).getClass();
    List<Method> methods = Arrays.asList(clazz.getDeclaredMethods());

    //
    Method sortMethod = null;
    Sort.Order toOrder = null;
    for (Sort.Order order : pageable.getSort()) {
      sortMethod = methods.stream()
          .filter(isMethodMatch(order.getProperty()))
          .findFirst()
          .orElseThrow(() -> new SoaGatewaySortingException(
              String.format("Invalid sort.  Sort field %s not found in class.",
                  order.getProperty())));
      toOrder = order;
      break;
    }
    if (sortMethod.getName().toLowerCase()
        .contains(Objects.requireNonNull(toOrder).getProperty().toLowerCase())) {
      Comparator<T> comparator;
      try {
        comparator = (Comparator<T>) newMethodComparator(clazz, sortMethod.getName());
      } catch (Exception e) {
        throw new SoaGatewaySortingException(
            String.format("Invalid sort.  Sort field %s not found in class.",
                toOrder.getProperty()));
      }

      if (toOrder.isAscending()) {
        list.sort(comparator);
      } else {
        list.sort(comparator.reversed());
      }
    }

    List<T> sublist = new ArrayList<>(list.subList(start, end));
    return new PageImpl<>(sublist, pageable, list.size());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <T> Comparator<T> newMethodComparator(Class<T> cls, String methodName)
      throws Exception {
    Method method = cls.getMethod(methodName);
    if (method.getParameterTypes().length != 0) {
      throw new Exception("Method " + method + " takes parameters");
    }

    Class<?> returnType = method.getReturnType();
    if (!Comparable.class.isAssignableFrom(returnType)) {
      throw new Exception("The return type " + returnType + " is not Comparable");
    }

    return newMethodComparator(method, (Class<? extends Comparable>) returnType);
  }

  private static <T, R extends Comparable<R>> Comparator<T> newMethodComparator(
      final Method method, final Class<R> returnType) {
    return new Comparator<T>() {
      @Override
      public int compare(T o1, T o2) {
        try {
          R a = invoke(method, o1);
          R b = invoke(method, o2);
          if (a == null) {
            return -1;
          }
          return a.compareTo(b);
        } catch (Exception e) {
          throw new SoaGatewaySortingException("Error sorting comparator on sortfield");
        }
      }

      private R invoke(Method method, T o) throws Exception {
        return returnType.cast(method.invoke(o));
      }
    };
  }
}
