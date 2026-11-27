package uk.gov.laa.ccms.soa.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.ProviderDetail;
import uk.gov.laa.ccms.soa.gateway.model.SubmittedApplicationDetails;

class ProviderContextTest {

  @AfterEach
  void tearDown() {
    ProviderContext.clear();
  }

  @Test
  void setAndRequireProviderIdTrimsDigits() {
    ProviderContext.set("  12345  ");

    assertThat(ProviderContext.get()).isEqualTo("12345");
    assertThat(ProviderContext.requireProviderId()).isEqualTo("12345");
  }

  @Test
  void requireProviderIdThrowsBadRequestWhenUnset() {
    assertThatThrownBy(ProviderContext::requireProviderId)
        .isInstanceOf(ProviderScopeException.class)
        .satisfies(
            ex ->
                assertThat(((ProviderScopeException) ex).getStatus())
                    .isEqualTo(HttpStatus.BAD_REQUEST));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "-1", "+1", "12a3", "12.3", "12 3"})
  void parseAndValidateHeaderRejectsInvalidValues(final String rawHeader) {
    assertThatThrownBy(() -> ProviderContext.parseAndValidateHeader(rawHeader))
        .isInstanceOf(ProviderScopeException.class)
        .satisfies(
            ex ->
                assertThat(((ProviderScopeException) ex).getStatus())
                    .isEqualTo(HttpStatus.BAD_REQUEST));
  }

  @Test
  void requireMatchingProviderIdAcceptsMatchingIntegerAndTrimmedString() {
    ProviderContext.set("12345");

    ProviderContext.requireMatchingProviderId(12345);
    ProviderContext.requireMatchingProviderId(" 12345 ");
  }

  @Test
  void requireMatchingProviderIdRejectsNullOrMismatchedValuesWithForbidden() {
    ProviderContext.set("12345");

    assertThatThrownBy(() -> ProviderContext.requireMatchingProviderId((Integer) null))
        .isInstanceOf(ProviderScopeException.class)
        .satisfies(
            ex ->
                assertThat(((ProviderScopeException) ex).getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN));

    assertThatThrownBy(() -> ProviderContext.requireMatchingProviderId("   "))
        .isInstanceOf(ProviderScopeException.class)
        .satisfies(
            ex ->
                assertThat(((ProviderScopeException) ex).getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN));

    assertThatThrownBy(() -> ProviderContext.requireMatchingProviderId("99999"))
        .isInstanceOf(ProviderScopeException.class)
        .satisfies(
            ex ->
                assertThat(((ProviderScopeException) ex).getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN));
  }

  @Test
  void requireMatchingCaseProviderChecksAllNestingLevelsSafely() {
    ProviderContext.set("26517");

    assertThatThrownBy(() -> ProviderContext.requireMatchingCaseProvider(null))
        .isInstanceOf(ProviderScopeException.class)
        .satisfies(
            ex ->
                assertThat(((ProviderScopeException) ex).getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN));

    assertThatThrownBy(() -> ProviderContext.requireMatchingCaseProvider(new CaseDetail()))
        .isInstanceOf(ProviderScopeException.class)
        .satisfies(
            ex ->
                assertThat(((ProviderScopeException) ex).getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN));

    assertThatThrownBy(
            () ->
                ProviderContext.requireMatchingCaseProvider(
                    new CaseDetail().applicationDetails(new SubmittedApplicationDetails())))
        .isInstanceOf(ProviderScopeException.class)
        .satisfies(
            ex ->
                assertThat(((ProviderScopeException) ex).getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN));

    assertThatThrownBy(
            () ->
                ProviderContext.requireMatchingCaseProvider(
                    new CaseDetail()
                        .applicationDetails(
                            new SubmittedApplicationDetails()
                                .providerDetails(new ProviderDetail()))))
        .isInstanceOf(ProviderScopeException.class)
        .satisfies(
            ex ->
                assertThat(((ProviderScopeException) ex).getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN));

    ProviderContext.requireMatchingCaseProvider(
        new CaseDetail()
            .applicationDetails(
                new SubmittedApplicationDetails()
                    .providerDetails(new ProviderDetail().providerFirmId("26517"))));
  }
}
