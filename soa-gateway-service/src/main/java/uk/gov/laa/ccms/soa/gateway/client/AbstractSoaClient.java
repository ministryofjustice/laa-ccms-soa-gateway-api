package uk.gov.laa.ccms.soa.gateway.client;

import static uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType.SUCCESS;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordCount;
import uk.gov.legalservices.enterprise.common._1_0.common.SystemsList;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRQType;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.ObjectFactory;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;

/**
 * Provides an abstract base for SOA (Service-Oriented Architecture) client implementations.
 *
 * <p>The `AbstractSoaClient` class serves as a foundational layer for interacting with
 * SOA-based services. It provides utility methods for header creation, transaction ID
 * generation, and SOA call success validation. Derived classes can extend this to
 * implement specific service calls.</p>
 *
 * <p>Transaction ID generation adopts a specific format and constraints, as explained
 * in the `generateTransactionId` method documentation.</p>
 *
 * @author [Your Name]  // Optional, if you want to include the author's name
 */
@Slf4j
public abstract class AbstractSoaClient {

  protected String serviceName;

  protected String serviceUrl;

  private static final ObjectFactory COMMON_HEADER_FACTORY = new ObjectFactory();

  private static final uk.gov.legalservices.enterprise.common._1_0.common.ObjectFactory
          COMMON_FACTORY = new uk.gov.legalservices.enterprise.common._1_0.common.ObjectFactory();

  protected WebServiceTemplate webServiceTemplate;

  /**
   * Get the WebServiceTemplate for this SoaClient.
   *
   * @return WebServiceTemplate
   */
  protected WebServiceTemplate getWebServiceTemplate() {
    return webServiceTemplate;
  }

  /**
   * Create the headers for this SoaClient.
   *
   * @param loggedInUserId The logged in user id
   * @param loggedInUserType The type of the logged in user
   * @return HeaderRQType
   */
  protected HeaderRQType createHeaderRq(
          final String loggedInUserId, final String loggedInUserType) {
    HeaderRQType headerRqType = COMMON_HEADER_FACTORY.createHeaderRQType();
    headerRqType.setLanguage("ENG");
    headerRqType.setUserLoginID(loggedInUserId);
    headerRqType.setUserRole(loggedInUserType);
    headerRqType.setTransactionRequestID(generateTransactionId());
    headerRqType.setSource(SystemsList.PUI);
    headerRqType.setTarget(SystemsList.ORACLE_E_BUSINESS);

    try {
      headerRqType.setTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(
          LocalDate.now().toString()));
    } catch (DatatypeConfigurationException e) {
      log.error("Failed to create DatatypeFactory", e);
      throw new RuntimeException(e);
    }

    return headerRqType;
  }

  protected RecordCount createRecordCount(Integer maxRecords) {
    RecordCount recordCount = COMMON_FACTORY.createRecordCount();
    recordCount.setMaxRecordsToFetch(BigInteger.valueOf(maxRecords));
    recordCount.setRetriveDataOnMaxCount(Boolean.FALSE);

    return recordCount;
  }

  /**
   * Generate a transaction id for a SOA call.
   * K024-677
   * Whilst this should be a UUID and a String type is returned, we can't adopt it!
   * Cap. Gem inform us that their down stream system store persists the ref.
   * as a 30 digit number field. The old timestamp format "yyyyMMddHHmmssSSS"
   * does not have sufficient granularity to support our production needs.
   * added a 5 digit random number (zero packed) to act as noise/millis
   * I found that with just 4 digits I was getting a number of duplicates.
   * this reduced significantly with 5 digit noise.
   *
   * @return String
   */
  protected static String generateTransactionId() {
    return String.format("%s%010d",
        new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()),
        (new Random().nextInt(999999998) + 1));
    //      yyyyMMddHHmmssSSS
    // e.g. 2016051215540200506053
  }

  protected void checkSoaCallSuccess(String serviceName, HeaderRSType headerRsType) {
    Optional.ofNullable(headerRsType)
        .map(HeaderRSType::getStatus)
        .filter(status -> Optional.ofNullable(status.getStatus())
            .orElse(StatusTextType.ERROR) != SUCCESS)
        .ifPresent(status -> {
          final String errorMsg = String.format(
                  "Failure in SOA call %s. Status Code: %s. Status Text: %s",
              serviceName,
              Optional.ofNullable(status.getStatus())
                  .orElse(StatusTextType.ERROR).value(),
              status.getStatusFreeText());
          log.error(errorMsg);
          throw new RuntimeException(errorMsg);
        });
  }
}
