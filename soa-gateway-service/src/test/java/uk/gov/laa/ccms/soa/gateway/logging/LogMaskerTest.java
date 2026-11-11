package uk.gov.laa.ccms.soa.gateway.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LogMaskerTest {

  @Test
  void describeGivesTheClassAndNeverTheMessage() {
    assertThat(LogMasker.describe(new IllegalStateException("bad AB123456C Jane Doe")))
        .isEqualTo("IllegalStateException");
    assertThat(LogMasker.describe(new RuntimeException())).isEqualTo("RuntimeException");
  }

  @Test
  void describeListsTheCauseClassesButNotTheirMessages() {
    var failure =
        new RuntimeException(
            "wrapper for 10 High Street",
            new IllegalArgumentException("Jane Doe", new java.io.IOException("jane@example.com")));

    assertThat(LogMasker.describe(failure))
        .isEqualTo("RuntimeException caused by IllegalArgumentException caused by IOException");
  }

  @Test
  void describeStopsAtAFixedDepth() {
    Throwable failure = new RuntimeException("end");
    for (int i = 0; i < 20; i++) {
      failure = new RuntimeException("level " + i, failure);
    }

    assertThat(LogMasker.describe(failure).split(" caused by ")).hasSize(6);
  }
}
