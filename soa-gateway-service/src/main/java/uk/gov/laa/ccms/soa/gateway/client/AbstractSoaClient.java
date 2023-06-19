package uk.gov.laa.ccms.soa.gateway.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public abstract class AbstractSoaClient extends WebServiceGatewaySupport {

  /**
   * @return String
   */
  public static String generateTransactionId() {

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
