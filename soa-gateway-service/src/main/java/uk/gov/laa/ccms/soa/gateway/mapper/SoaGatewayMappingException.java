package uk.gov.laa.ccms.soa.gateway.mapper;

/**
 * An exception raised when a problem occurs during mapping to and from SOA data transfer objects.
 */
public class SoaGatewayMappingException extends RuntimeException {

  /**
   * Construct a new {@code SoaGatewayMappingException} with a message.
   *
   * @param message the message describing the problem.
   */
  public SoaGatewayMappingException(String message) {
    super(message);
  }

  /**
   * Construct a new {@code SoaGatewayMappingException} with a message and cause.
   *
   * @param message the message describing the problem.
   * @param cause the {@code Throwable} which was the cause of this exception.
   */
  public SoaGatewayMappingException(String message, Throwable cause) {
    super(message, cause);
  }
}
