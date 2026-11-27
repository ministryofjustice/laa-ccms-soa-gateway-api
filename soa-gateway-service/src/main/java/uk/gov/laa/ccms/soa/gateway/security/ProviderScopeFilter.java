package uk.gov.laa.ccms.soa.gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Enforces presence and numeric format of {@code Caab-Provider-Id} on SOA API routes and clears
 * {@link ProviderContext} after every request.
 */
@Component
public class ProviderScopeFilter extends OncePerRequestFilter {

  private static final String BAD_REQUEST_JSON =
      "{\"status\":400,\"error\":\"Bad Request\",\"error_message\":\""
          + ProviderContext.MISSING_OR_INVALID_HEADER_MESSAGE
          + "\"}";

  private final SecurityEventLogger securityEventLogger;

  /** Constructs a filter without security event logging for standalone controller test setups. */
  public ProviderScopeFilter() {
    this(null);
  }

  /**
   * Constructs a filter backed by {@link SecurityEventLogger}.
   *
   * @param securityEventLogger the security event logger
   */
  @Autowired
  public ProviderScopeFilter(final SecurityEventLogger securityEventLogger) {
    this.securityEventLogger = securityEventLogger;
  }

  @Override
  protected boolean shouldNotFilter(final HttpServletRequest request) {
    return isExemptPath(resolveApplicationPath(request));
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    final String validatedProviderId;
    try {
      validatedProviderId = extractProviderId(request);
    } catch (final ProviderScopeException ex) {
      if (securityEventLogger != null) {
        securityEventLogger.inputRejected(request.getMethod(), resolveApplicationPath(request), ex);
      }
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      response.getWriter().write(BAD_REQUEST_JSON);
      return;
    }

    ProviderContext.set(validatedProviderId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      ProviderContext.clear();
    }
  }

  private static String extractProviderId(final HttpServletRequest request) {
    final Enumeration<String> headerValues = request.getHeaders(ProviderContext.HEADER_NAME);
    final List<String> values = headerValues == null ? List.of() : Collections.list(headerValues);
    if (values.size() != 1) {
      throw ProviderScopeException.badRequest(ProviderContext.MISSING_OR_INVALID_HEADER_MESSAGE);
    }
    return ProviderContext.parseAndValidateHeader(values.getFirst());
  }

  private static String resolveApplicationPath(final HttpServletRequest request) {
    String path = request.getRequestURI();
    final String contextPath = request.getContextPath();
    if (StringUtils.hasText(contextPath) && path != null && path.startsWith(contextPath)) {
      path = path.substring(contextPath.length());
    }
    if (!StringUtils.hasText(path)) {
      path = request.getServletPath();
    }
    return path == null ? "" : path;
  }

  static boolean isExemptPath(final String path) {
    if (!StringUtils.hasText(path) || path.contains("..")) {
      return false;
    }
    return path.equals("/actuator")
        || path.startsWith("/actuator/")
        || path.equals("/swagger-ui.html")
        || path.equals("/swagger-ui")
        || path.startsWith("/swagger-ui/")
        || path.equals("/v3/api-docs")
        || path.startsWith("/v3/api-docs/")
        || path.equals("/api-docs")
        || path.startsWith("/api-docs/")
        || path.equals("/open-api-specification.yml")
        || path.equals("/favicon.ico")
        || path.equals("/error")
        || path.startsWith("/error/");
  }
}
