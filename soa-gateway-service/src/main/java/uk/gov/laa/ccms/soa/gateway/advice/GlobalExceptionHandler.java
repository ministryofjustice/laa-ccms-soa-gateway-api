package uk.gov.laa.ccms.soa.gateway.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.laa.ccms.soa.gateway.SoaGatewaySortingException;

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
}
