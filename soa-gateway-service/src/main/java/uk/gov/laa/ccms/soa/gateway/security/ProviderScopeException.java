package uk.gov.laa.ccms.soa.gateway.security;

import org.springframework.http.HttpStatus;

/** Exception thrown when a request violates {@code Caab-Provider-Id} scope requirements. */
public class ProviderScopeException extends RuntimeException {

  private final HttpStatus status;

  /**
   * Constructs a {@link ProviderScopeException} with an HTTP status and safe error message.
   *
   * @param status the HTTP status to return
   * @param message the non-sensitive error message
   */
  public ProviderScopeException(final HttpStatus status, final String message) {
    super(message);
    this.status = status;
  }

  /**
   * Creates a {@code 400 Bad Request} provider scope exception.
   *
   * @param message the non-sensitive error message
   * @return a {@link ProviderScopeException} with status {@link HttpStatus#BAD_REQUEST}
   */
  public static ProviderScopeException badRequest(final String message) {
    return new ProviderScopeException(HttpStatus.BAD_REQUEST, message);
  }

  /**
   * Creates a {@code 403 Forbidden} provider scope exception.
   *
   * @param message the non-sensitive error message
   * @return a {@link ProviderScopeException} with status {@link HttpStatus#FORBIDDEN}
   */
  public static ProviderScopeException forbidden(final String message) {
    return new ProviderScopeException(HttpStatus.FORBIDDEN, message);
  }

  /**
   * Gets the HTTP status associated with this exception.
   *
   * @return the {@link HttpStatus}
   */
  public HttpStatus getStatus() {
    return status;
  }
}
