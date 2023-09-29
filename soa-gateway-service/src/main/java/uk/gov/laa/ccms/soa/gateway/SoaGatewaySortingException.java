package uk.gov.laa.ccms.soa.gateway;

/**
 * Exception thrown in the SOA Gateway on invalid {@link org.springframework.data.domain.Sort}.
 */
public class SoaGatewaySortingException extends RuntimeException {

  /**
   * Constructs a new {@link SoaGatewaySortingException} with the specified detail message. The
   * cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *                {@link #getMessage()} method.
   */
  public SoaGatewaySortingException(String message) {
    super(message);
  }

}
