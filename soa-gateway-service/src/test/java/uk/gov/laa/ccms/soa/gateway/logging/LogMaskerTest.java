package uk.gov.laa.ccms.soa.gateway.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LogMaskerTest {

  @Test
  void masksPersonalDataPatterns() {
    String masked =
        LogMasker.mask(
            "jane.doe@example.com AB 12 34 56 C born 1980-02-03 and 03/02/1980 "
                + "phone 07700 900123 ref 300001");

    assertThat(masked)
        .doesNotContain("jane.doe", "AB 12 34 56 C", "1980", "07700 900123")
        .contains("ref 300001");
  }

  @Test
  void truncatesLongText() {
    assertThat(LogMasker.mask("x".repeat(LogMasker.MAX_LENGTH + 50)))
        .hasSize(LogMasker.MAX_LENGTH + "...[truncated]".length());
  }

  @Test
  void describeGivesClassAndMaskedMessageOnly() {
    assertThat(LogMasker.describe(new IllegalStateException("bad AB123456C")))
        .isEqualTo("IllegalStateException: bad ***");
    assertThat(LogMasker.describe(new RuntimeException())).isEqualTo("RuntimeException: ");
  }
}
