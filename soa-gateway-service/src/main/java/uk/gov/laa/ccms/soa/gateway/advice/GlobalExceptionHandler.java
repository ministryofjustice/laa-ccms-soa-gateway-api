package uk.gov.laa.ccms.soa.gateway.advice;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.laa.ccms.soa.gateway.SoaGatewaySortingException;
import uk.gov.laa.ccms.soa.gateway.security.ProviderScopeException;

/**
 * Controller advice class responsible for handling exceptions globally and providing appropriate
 * error responses.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles the {@link SoaGatewaySortingException} by logging the error and returning an
   * appropriate error response.
   *
   * @param e the SoaGatewaySortingException
   * @return the response entity with the status code
   */
  @ExceptionHandler(value = {SoaGatewaySortingException.class})
  public ResponseEntity<Object> handleSoaGatewaySortingException(
      final SoaGatewaySortingException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getLocalizedMessage());
  }

  /**
   * Handles {@link ProviderScopeException} with a non-sensitive JSON error response.
   *
   * @param e the ProviderScopeException
   * @return the JSON error response with {@code 400} or {@code 403} status
   */
  @ExceptionHandler(value = {ProviderScopeException.class})
  public ResponseEntity<Map<String, Object>> handleProviderScopeException(
      final ProviderScopeException e) {
    final HttpStatus status = e.getStatus();
    log.warn("Provider scope check failed ({}): {}", status.value(), e.getMessage());
    final Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("error_message", e.getMessage());
    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
  }
}
