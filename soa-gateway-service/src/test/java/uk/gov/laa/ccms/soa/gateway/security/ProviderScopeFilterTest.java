package uk.gov.laa.ccms.soa.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.servlet.ServletException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class ProviderScopeFilterTest {

  @AfterEach
  void tearDown() {
    ProviderContext.clear();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/actuator",
        "/actuator/health",
        "/actuator/health/readiness",
        "/swagger-ui.html",
        "/swagger-ui",
        "/swagger-ui/index.html",
        "/v3/api-docs",
        "/v3/api-docs/swagger-config",
        "/api-docs",
        "/api-docs/swagger-config",
        "/open-api-specification.yml",
        "/favicon.ico",
        "/error",
        "/error/404"
      })
  void exemptRoutesBypassHeaderRequirement(final String uri) throws Exception {
    ProviderScopeFilter filter = new ProviderScopeFilter();
    MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
    MockHttpServletResponse response = new MockHttpServletResponse();
    AtomicBoolean chainCalled = new AtomicBoolean(false);

    filter.doFilter(request, response, (req, res) -> chainCalled.set(true));

    assertThat(chainCalled.get()).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(ProviderContext.get()).isNull();
  }

  @Test
  void contextPathPrefixIsStrippedWhenMatchingExemptRoutes() throws Exception {
    ProviderScopeFilter filter = new ProviderScopeFilter();
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/soa/actuator/health");
    request.setContextPath("/soa");
    MockHttpServletResponse response = new MockHttpServletResponse();
    AtomicBoolean chainCalled = new AtomicBoolean(false);

    filter.doFilter(request, response, (req, res) -> chainCalled.set(true));

    assertThat(chainCalled.get()).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"/actuator/../cases", "/swagger-ui/../invoices", ""})
  void traversalAndBlankPathsAreNotExempt(final String path) {
    assertThat(ProviderScopeFilter.isExemptPath(path)).isFalse();
  }

  @Test
  void validHeaderPopulatesContextDuringChainAndClearsInFinallyEvenOnException() {
    ProviderScopeFilter filter = new ProviderScopeFilter();
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/cases");
    request.addHeader(ProviderContext.HEADER_NAME, " 26517 ");
    MockHttpServletResponse response = new MockHttpServletResponse();
    AtomicReference<String> seenInChain = new AtomicReference<>();

    assertThatThrownBy(
            () ->
                filter.doFilter(
                    request,
                    response,
                    (req, res) -> {
                      seenInChain.set(ProviderContext.get());
                      throw new ServletException("downstream failure");
                    }))
        .isInstanceOf(ServletException.class);

    assertThat(seenInChain.get()).isEqualTo("26517");
    assertThat(ProviderContext.get()).isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "abc", "-5", "12.34"})
  void invalidHeaderReturnsJsonBadRequestAndLogsInputRejection(final String invalidValue)
      throws Exception {
    SecurityEventLogger eventLogger = mock(SecurityEventLogger.class);
    ProviderScopeFilter filter = new ProviderScopeFilter(eventLogger);
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/contract-details");
    request.addHeader(ProviderContext.HEADER_NAME, invalidValue);
    MockHttpServletResponse response = new MockHttpServletResponse();
    AtomicBoolean chainCalled = new AtomicBoolean(false);

    filter.doFilter(request, response, (req, res) -> chainCalled.set(true));

    assertThat(chainCalled.get()).isFalse();
    assertThat(response.getStatus()).isEqualTo(400);
    assertThat(response.getContentType()).startsWith("application/json");
    assertThat(response.getContentAsString())
        .contains("\"status\":400")
        .doesNotContain(invalidValue.trim().isEmpty() ? "SENTINEL" : invalidValue);
    verify(eventLogger)
        .inputRejected(eq("GET"), eq("/contract-details"), any(ProviderScopeException.class));
  }

  @Test
  void missingOrDuplicateHeadersReturnBadRequest() throws Exception {
    ProviderScopeFilter filter = new ProviderScopeFilter();

    MockHttpServletRequest missingRequest = new MockHttpServletRequest("POST", "/clients");
    MockHttpServletResponse missingResponse = new MockHttpServletResponse();
    filter.doFilter(missingRequest, missingResponse, (req, res) -> {});
    assertThat(missingResponse.getStatus()).isEqualTo(400);

    MockHttpServletRequest duplicateRequest = new MockHttpServletRequest("POST", "/clients");
    duplicateRequest.addHeader(ProviderContext.HEADER_NAME, "100");
    duplicateRequest.addHeader(ProviderContext.HEADER_NAME, "200");
    MockHttpServletResponse duplicateResponse = new MockHttpServletResponse();
    filter.doFilter(duplicateRequest, duplicateResponse, (req, res) -> {});
    assertThat(duplicateResponse.getStatus()).isEqualTo(400);
  }

  @Test
  void fallsBackToServletPathWhenRequestUriIsEmpty() throws Exception {
    final ProviderScopeFilter filter = new ProviderScopeFilter();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("");
    request.setServletPath("/organisations");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, (req, res) -> {});

    assertThat(response.getStatus()).isEqualTo(400);
  }
}
