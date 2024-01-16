package uk.gov.laa.ccms.soa.gateway.client;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Provides a mock client interface for returning static responses to Contract Details
 * requests.
 */
@Component
@Profile({"dev", "local"})
public class MockContractDetailsClientImpl extends ContractDetailsClientImpl {

  private final WireMockServer wireMockServer;

  /**
   * Constructs a new {@link ContractDetailsClientImpl} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName        The name of the contract details service.
   * @param serviceUrl         The URL endpoint for the contract details service.
   */
  public MockContractDetailsClientImpl(
      final WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.contract-details.service-name}") final String serviceName,
      @Value("${laa.ccms.soa-gateway.contract-details.service-port}") final Integer servicePort,
      @Value("${laa.ccms.soa-gateway.contract-details.service-url}") final String serviceUrl) {
    super(webServiceTemplate, serviceName, serviceUrl);

    this.wireMockServer = new WireMockServer(options()
        .port(servicePort)
        .usingFilesUnderClasspath("wiremock"));
    wireMockServer.start();
  }
}
