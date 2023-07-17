package uk.gov.laa.ccms.soa.gateway.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

@Slf4j
public class DateUtil {

    public static XMLGregorianCalendar convertDateToXMLDateOnly(final Date date) throws DatatypeConfigurationException {
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
