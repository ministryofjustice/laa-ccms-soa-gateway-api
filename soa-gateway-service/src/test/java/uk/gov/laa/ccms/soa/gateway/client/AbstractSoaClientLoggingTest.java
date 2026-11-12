package uk.gov.laa.ccms.soa.gateway.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.Status;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;

class AbstractSoaClientLoggingTest {

  private final AbstractSoaClient client = new AbstractSoaClient() {};

  @Test
  void aFailedCallLogsTheStatusCodeAndNotTheFreeText() {
    var logger = (Logger) LoggerFactory.getLogger(AbstractSoaClient.class);
    var appender = new ListAppender<ILoggingEvent>();
    appender.start();
    logger.addAppender(appender);
    try {
      var status = new Status();
      status.setStatus(StatusTextType.ERROR);
      status.setStatusFreeText("No client Jane Doe at 10 High Street");
      var header = new HeaderRSType();
      header.setStatus(status);

      assertThatThrownBy(() -> client.isSuccessOrThrowException("ClientService", header))
          .isInstanceOf(RuntimeException.class);

      assertThat(appender.list).hasSize(1);
      assertThat(appender.list.getFirst().getFormattedMessage())
          .contains("ClientService", "Error")
          .doesNotContain("Jane", "High Street");
    } finally {
      logger.detachAppender(appender);
    }
  }
}
