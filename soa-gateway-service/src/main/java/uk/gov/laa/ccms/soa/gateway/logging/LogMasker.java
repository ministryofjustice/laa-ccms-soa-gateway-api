package uk.gov.laa.ccms.soa.gateway.logging;

/** Describes failures for the log without carrying the personal data their messages can echo. */
public final class LogMasker {

  private static final int MAX_CAUSES = 5;

  private LogMasker() {}

  /**
   * Describes a failure as its class and the classes of its causes, never its message. Messages
   * from upstream services and parsers can echo names, addresses and identifiers that no pattern
   * reliably catches.
   *
   * @param throwable the failure
   * @return the class name, followed by the cause class names
   */
  public static String describe(Throwable throwable) {
    StringBuilder description = new StringBuilder(throwable.getClass().getSimpleName());
    Throwable cause = throwable.getCause();
    for (int depth = 0; cause != null && cause != throwable && depth < MAX_CAUSES; depth++) {
      description.append(" caused by ").append(cause.getClass().getSimpleName());
      cause = cause.getCause();
    }
    return description.toString();
  }
}
