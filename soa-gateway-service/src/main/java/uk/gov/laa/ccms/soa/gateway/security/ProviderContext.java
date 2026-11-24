package uk.gov.laa.ccms.soa.gateway.security;

import java.util.Optional;
import java.util.regex.Pattern;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.ProviderDetail;
import uk.gov.laa.ccms.soa.gateway.model.SubmittedApplicationDetails;

/**
 * Request-scoped holder and validator for the caller's {@code Caab-Provider-Id} firm identifier.
 */
public final class ProviderContext {

  public static final String HEADER_NAME = "Caab-Provider-Id";
  public static final String MISSING_OR_INVALID_HEADER_MESSAGE =
      "Missing or invalid Caab-Provider-Id header";
  public static final String PROVIDER_MISMATCH_MESSAGE = "Provider scope mismatch";

  private static final Pattern NUMERIC_ID = Pattern.compile("^\\d+$");
  private static final ThreadLocal<String> CURRENT_PROVIDER_ID = new ThreadLocal<>();

  private ProviderContext() {}

  /**
   * Validates and stores the current request's provider firm identifier.
   *
   * @param providerId the raw or trimmed numeric provider firm identifier
   */
  public static void set(final String providerId) {
    CURRENT_PROVIDER_ID.set(parseAndValidateHeader(providerId));
  }

  /**
   * Returns the current request's validated provider firm identifier, or {@code null} if unset.
   *
   * @return the current provider firm identifier or {@code null}
   */
  public static String get() {
    return CURRENT_PROVIDER_ID.get();
  }

  /** Clears the thread-local provider firm identifier for the current request thread. */
  public static void clear() {
    CURRENT_PROVIDER_ID.remove();
  }

  /**
   * Trims and validates a raw {@code Caab-Provider-Id} header value.
   *
   * @param rawHeader the raw header value
   * @return the trimmed numeric provider firm identifier
   */
  public static String parseAndValidateHeader(final String rawHeader) {
    if (rawHeader == null) {
      throw ProviderScopeException.badRequest(MISSING_OR_INVALID_HEADER_MESSAGE);
    }
    final String trimmed = rawHeader.trim();
    if (trimmed.isEmpty() || !NUMERIC_ID.matcher(trimmed).matches()) {
      throw ProviderScopeException.badRequest(MISSING_OR_INVALID_HEADER_MESSAGE);
    }
    return trimmed;
  }

  /**
   * Requires a valid {@code Caab-Provider-Id} to be present in the current request context.
   *
   * @return the validated provider firm identifier
   */
  public static String requireProviderId() {
    final String providerId = CURRENT_PROVIDER_ID.get();
    if (providerId == null) {
      throw ProviderScopeException.badRequest(MISSING_OR_INVALID_HEADER_MESSAGE);
    }
    return providerId;
  }

  /**
   * Verifies that an integer target provider firm ID matches the caller's {@code Caab-Provider-Id}.
   *
   * @param targetProviderId the target provider firm ID from the request
   */
  public static void requireMatchingProviderId(final Integer targetProviderId) {
    requireMatchingProviderId(targetProviderId == null ? null : String.valueOf(targetProviderId));
  }

  /**
   * Verifies that a string target provider firm ID is present and matches {@code Caab-Provider-Id}.
   *
   * @param targetProviderId the target provider firm ID from the request
   */
  public static void requireMatchingProviderId(final String targetProviderId) {
    final String callerProviderId = requireProviderId();
    if (targetProviderId == null) {
      throw ProviderScopeException.forbidden(PROVIDER_MISMATCH_MESSAGE);
    }
    final String trimmedTarget = targetProviderId.trim();
    if (trimmedTarget.isEmpty() || !callerProviderId.equals(trimmedTarget)) {
      throw ProviderScopeException.forbidden(PROVIDER_MISMATCH_MESSAGE);
    }
  }

  /**
   * Verifies that a {@link CaseDetail} carries a {@code providerFirmId} matching the caller's
   * {@code Caab-Provider-Id}.
   *
   * @param caseDetail the case payload being created or amended
   */
  public static void requireMatchingCaseProvider(final CaseDetail caseDetail) {
    final String caseProviderFirmId =
        Optional.ofNullable(caseDetail)
            .map(CaseDetail::getApplicationDetails)
            .map(SubmittedApplicationDetails::getProviderDetails)
            .map(ProviderDetail::getProviderFirmId)
            .orElse(null);
    requireMatchingProviderId(caseProviderFirmId);
  }
}
