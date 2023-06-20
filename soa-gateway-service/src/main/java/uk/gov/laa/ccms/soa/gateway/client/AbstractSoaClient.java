package uk.gov.laa.ccms.soa.gateway.client;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordCount;
import uk.gov.legalservices.enterprise.common._1_0.common.SystemsList;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRQType;
import uk.gov.legalservices.enterprise.common._1_0.header.ObjectFactory;

@Slf4j
public abstract class AbstractSoaClient {

  private static final ObjectFactory COMMON_HEADER_FACTORY = new ObjectFactory();

  private static final uk.gov.legalservices.enterprise.common._1_0.common.ObjectFactory COMMON_FACTORY =
      new uk.gov.legalservices.enterprise.common._1_0.common.ObjectFactory();

  protected WebServiceTemplate webServiceTemplate;

  /**
   * Get the WebServiceTemplate for this SoaClient
   * @return WebServiceTemplate
   */
  public WebServiceTemplate getWebServiceTemplate() {
    return webServiceTemplate;
  }

  /**
   * @param loggedInUserId The logged in user id
   * @param loggedInUserType The type of the logged in user
   * @return HeaderRQType
   */
  protected HeaderRQType createHeaderRQ(final String loggedInUserId, final String loggedInUserType) {
    HeaderRQType headerRQType = COMMON_HEADER_FACTORY.createHeaderRQType();

//        if (userInfo.getLocale().getLanguage().equalsIgnoreCase("en")) {
    headerRQType.setLanguage("ENG");
//        } else {
//            // must be welsh
//            headerRQType.setLanguage("CYM");
//        }
    headerRQType.setUserLoginID(loggedInUserId);
    headerRQType.setUserRole(loggedInUserType);
    headerRQType.setTransactionRequestID(generateTransactionId());
    headerRQType.setSource(SystemsList.PUI);
    headerRQType.setTarget(SystemsList.ORACLE_E_BUSINESS);

    try {
      headerRQType.setTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(
          LocalDate.now().toString()));
    } catch (DatatypeConfigurationException e) {
      log.error("Failed to create DatatypeFactory", e);
      throw new RuntimeException(e);
    }

    return headerRQType;
  }

  protected RecordCount createRecordCount(BigInteger maxRecords) {
    RecordCount recordCount = COMMON_FACTORY.createRecordCount();
    recordCount.setMaxRecordsToFetch(maxRecords);
    recordCount.setRetriveDataOnMaxCount(Boolean.FALSE);

    return recordCount;
  }

  /**
   * @return String
   */
  protected static String generateTransactionId() {

    // K024-677
    // Whilst this should be a UUID and a String type is returned, we can't adopt it!
    // Cap. Gem inform us that their down stream system store persists the ref.
    // as a 30 digit number field. The old timestamp format "yyyyMMddHHmmssSSS"
    // does not have sufficient granularity to support our production needs.
    // added a 5 digit random number (zero packed) to act as noise/millis
    // I found that with just 4 digits I was getting a number of duplicates.
    // this reduced significantly with 5 digit noise.
    return String.format("%s%010d",
        new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()),
        (new Random().nextInt(999999998) + 1));
    //      yyyyMMddHHmmssSSS
    // e.g. 2016051215540200506053
  }
}
