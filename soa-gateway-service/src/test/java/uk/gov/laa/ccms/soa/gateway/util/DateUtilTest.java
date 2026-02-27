package uk.gov.laa.ccms.soa.gateway.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DateUtilTest {

  @Test
  public void testConvertDateToXMLDateOnly() throws DatatypeConfigurationException {
    // Create a sample date
    Calendar calendar = new GregorianCalendar(2023, Calendar.JULY, 18);
    Date date = calendar.getTime();

    // Convert the date to XMLGregorianCalendar
    XMLGregorianCalendar result = DateUtil.convertDateToXmlDateOnly(date);

    // Verify the converted XMLGregorianCalendar
    assertEquals(18, result.getDay());
    assertEquals(7, result.getMonth());
    assertEquals(2023, result.getYear());
  }

  @Test
  public void testConvertDateToXMLDateOnly_NullDate() throws DatatypeConfigurationException {
    // Convert null date to XMLGregorianCalendar
    XMLGregorianCalendar result = DateUtil.convertDateToXmlDateOnly(null);

    // Verify that null is returned
    assertNull(result);
  }
}
