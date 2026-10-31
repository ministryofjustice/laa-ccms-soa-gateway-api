package uk.gov.laa.ccms.soa.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Writes security events with the structured fields the SOC use cases match on. */
@Component
@Slf4j
public class SecurityEventLogger {

  static final String EVENT_TYPE = "event_type";
  static final String OUTCOME = "outcome";

  /** The values of the {@code event_type} log field. */
  public enum EventType {
    INPUT_REJECTED
  }

  /** The values of the {@code outcome} log field. */
  public enum Outcome {
    DENIED
  }

  /**
   * Records a request rejected as malformed or not allowed. Only the exception class, method and
   * path are logged, as the request content is user supplied.
   *
   * @param method the HTTP method
   * @param path the request path, without its query string
   * @param exception the exception that described the rejection
   */
  public void inputRejected(String method, String path, Exception exception) {
    log.atWarn()
        .addKeyValue(EVENT_TYPE, EventType.INPUT_REJECTED.name())
        .addKeyValue(OUTCOME, Outcome.DENIED.name())
        .addKeyValue("event_detail", exception.getClass().getSimpleName())
        .addKeyValue("http_method", method)
        .addKeyValue("path", path)
        .log("Request input rejected");
  }
}
