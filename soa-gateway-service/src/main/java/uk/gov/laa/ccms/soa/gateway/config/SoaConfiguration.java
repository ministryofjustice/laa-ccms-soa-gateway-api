package uk.gov.laa.ccms.soa.gateway.config;

import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

/**
 * Configuration class responsible for setting up the Service-Oriented Architecture (SOA)
 * communication with the necessary security and marshalling configurations.
 *
 * <p>This configuration includes setting up the marshaller, web service template, and security
 * interceptors with the provided username and password for authentication.</p>
 */
@Configuration
public class SoaConfiguration {
  private final String username;
  private final String password;

  /**
   * Constructs a new SOA configuration with the provided authentication credentials.
   *
   * @param username the username for SOA gateway authentication
   * @param password the password for SOA gateway authentication
   */
  public SoaConfiguration(@Value("${laa.ccms.soa-gateway.username}")String username,
                          @Value("${laa.ccms.soa-gateway.password}")String password) {
    this.username = username;
    this.password = password;
  }

  /**
   * Configures the JAXB 2 marshaller for the SOA gateway communication.
   * Sets up the required packages for scanning.
   *
   * @return a configured {@link Jaxb2Marshaller} instance
   */
  @Bean
  public Jaxb2Marshaller marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setPackagesToScan("uk.gov.legalservices", "uk.gov.gsi.legalaid");
    return marshaller;
  }

  /**
   * Configures the web service template for SOA communication.
   * Sets up the marshaller/unmarshaller and security interceptors.
   *
   * @param marshaller the configured JAXB 2 marshaller
   * @return a configured {@link WebServiceTemplate} instance
   */
  @Bean
  public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
    WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
    webServiceTemplate.setMarshaller(marshaller);
    webServiceTemplate.setUnmarshaller(marshaller);
    webServiceTemplate.setInterceptors(new ClientInterceptor[]{ securityInterceptor() });
    return webServiceTemplate;
  }

  /**
   * Configures the WSS4J security interceptor for SOA communication.
   * Sets up the security actions and authentication credentials.
   *
   * @return a configured {@link Wss4jSecurityInterceptor} instance
   */
  @Bean
  public Wss4jSecurityInterceptor securityInterceptor() {
    Wss4jSecurityInterceptor security = new Wss4jSecurityInterceptor();
    security.setSecurementActions(WSHandlerConstants.TIMESTAMP + " "
            + WSHandlerConstants.USERNAME_TOKEN);
    security.setSecurementPasswordType(WSConstants.PW_TEXT);
    security.setSecurementUsername(this.username);
    security.setSecurementPassword(this.password);
    security.setSecurementMustUnderstand(false);
    return security;
  }
}
