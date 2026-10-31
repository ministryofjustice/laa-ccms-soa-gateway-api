package uk.gov.laa.ccms.soa.gateway.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

class SecurityEventLoggerTest {

  private final SecurityEventLogger securityEventLogger = new SecurityEventLogger();
  private Logger logger;
  private ListAppender<ILoggingEvent> appender;

  @BeforeEach
  void setUp() {
    logger = (Logger) LoggerFactory.getLogger(SecurityEventLogger.class);
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
  }

  @AfterEach
  void tearDown() {
    logger.detachAppender(appender);
  }

  @Test
  void inputRejectedLogsClassMethodAndPathOnly() {
    securityEventLogger.inputRejected(
        "POST", "/cases/search", new MaxUploadSizeExceededException(1L));

    ILoggingEvent event = appender.list.getFirst();
    assertEquals("INPUT_REJECTED", keyValue(event, "event_type"));
    assertEquals("DENIED", keyValue(event, "outcome"));
    assertEquals("MaxUploadSizeExceededException", keyValue(event, "event_detail"));
    assertEquals("POST", keyValue(event, "http_method"));
    assertEquals("/cases/search", keyValue(event, "path"));
    assertEquals(Level.WARN, event.getLevel());
  }

  private static Object keyValue(ILoggingEvent event, String key) {
    return event.getKeyValuePairs().stream()
        .filter(kv -> kv.key.equals(key))
        .findFirst()
        .orElseThrow()
        .value;
  }
}
