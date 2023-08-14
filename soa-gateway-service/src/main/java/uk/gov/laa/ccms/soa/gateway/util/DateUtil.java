package uk.gov.laa.ccms.soa.gateway.util;

import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;


/**
 * Utility class that provides helper functions for date conversions.
 *
 * <p>Provides functions for converting between common date types like
 * {@link Date} and {@link XMLGregorianCalendar}.</p>
 */
@Slf4j
public class DateUtil {

  /**
   * Converts a given {@link Date} object to {@link XMLGregorianCalendar} containing only year,
   * month, and day details.
   *
   * <p>If the provided date is null, the function returns null. Otherwise, it returns an
   * {@link XMLGregorianCalendar} without time-related fields.</p>
   *
   * @param date   The {@link Date} object to be converted.
   * @return       A date in {@link XMLGregorianCalendar} format containing only year, month,
   *               and day, or null if the input date is null.
   * @throws DatatypeConfigurationException If there is an exception during the date conversion.
   */
  public static XMLGregorianCalendar convertDateToXmlDateOnly(
          final Date date) throws DatatypeConfigurationException {
    XMLGregorianCalendar result = null;
    if (date == null) {
      return null;
    }
    GregorianCalendar gregCal = new GregorianCalendar();
    gregCal.setTime(date);
    DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
    result = dataTypeFactory.newXMLGregorianCalendar();
    result.setDay(gregCal.get(GregorianCalendar.DAY_OF_MONTH));
    result.setMonth(gregCal.get(GregorianCalendar.MONTH) + 1);
    result.setYear(gregCal.get(GregorianCalendar.YEAR));

    return result;
  }
}
