package uk.gov.laa.ccms.soa.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.client.RequestMatchers.anything;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;
import uk.gov.laa.springboot.oauth2.testsupport.StubJwtDecoder;
import uk.gov.laa.springboot.oauth2.testsupport.StubJwtToken;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "laa.springboot.starter.oauth2.unprotected-uris="
          + "/actuator/health/**,/swagger-ui.html,/swagger-ui/**,/v3/api-docs/**"
    })
@Import(ProviderScopeIntegrationTest.StubAuthConfig.class)
class ProviderScopeIntegrationTest {

  private static final String TOKEN = "integration-test-bearer-token";
  private static final String USER_LOGIN = "user@firm.co.uk";
  private static final String USER_ROLE = "EXTERNAL";

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @LocalServerPort private int port;

  @Autowired private WebServiceTemplate webServiceTemplate;

  @Value("classpath:/payload/ContractDetailsInqRS_valid.xml")
  private Resource contractDetailsValidXml;

  @Value("classpath:/payload/UpdateUserRS_valid.xml")
  private Resource updateUserValidXml;

  @Value("classpath:/payload/InvoiceAddRS_valid.xml")
  private Resource invoiceAddValidXml;

  private MockWebServiceServer mockSoapServer;

  @BeforeEach
  void setUp() {
    mockSoapServer = MockWebServiceServer.createServer(webServiceTemplate);
  }

  @Test
  void actuatorHealthRemainsExemptWithoutProviderHeader() throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/actuator/health"))
            .GET()
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    mockSoapServer.verify();
  }

  static Stream<Arguments> soaEndpoints() {
    return Stream.of(
        Arguments.of("GET", "/contract-details?providerFirmId=123&officeId=456", null),
        Arguments.of("POST", "/cases", "{}"),
        Arguments.of("PUT", "/cases?case-update-type=ProviderDetails", "{}"),
        Arguments.of("POST", "/invoices", "{}"),
        Arguments.of("GET", "/invoices/999", null),
        Arguments.of("POST", "/clients", "{}"),
        Arguments.of("GET", "/clients/12345", null),
        Arguments.of("PUT", "/clients/12345", "{}"),
        Arguments.of("POST", "/documents", "{}"),
        Arguments.of("GET", "/documents/doc1", null),
        Arguments.of("PUT", "/documents/doc1", "{}"),
        Arguments.of("GET", "/documents/doc1/cover-sheet", null),
        Arguments.of("PUT", "/notifications/n1", "{\"user_id\":\"u1\",\"action\":\"act\"}"),
        Arguments.of("GET", "/organisations", null),
        Arguments.of("GET", "/organisations/org1", null),
        Arguments.of("POST", "/provider-requests", "{}"),
        Arguments.of(
            "PUT", "/users/options", "{\"user_login_id\":\"u1\",\"provider_firm_id\":\"200\"}"));
  }

  @ParameterizedTest
  @MethodSource("soaEndpoints")
  void allSoaEndpointsReturnBadRequestWhenProviderHeaderMissing(
      final String method, final String path, final String body) throws Exception {
    HttpResponse<String> response = sendAuthenticated(method, path, null, body);

    assertThat(response.statusCode()).isEqualTo(400);
    mockSoapServer.verify();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "not-numeric", "-42", "12.34"})
  void invalidProviderHeaderValuesReturnBadRequestWithoutSoapInvocation(final String headerValue)
      throws Exception {
    HttpResponse<String> response =
        sendAuthenticated(
            "GET", "/contract-details?providerFirmId=123&officeId=456", headerValue, null);

    assertThat(response.statusCode()).isEqualTo(400);
    mockSoapServer.verify();
  }

  @Test
  void contractDetailsReturnsForbiddenOnProviderMismatchWithZeroSoapCalls() throws Exception {
    HttpResponse<String> response =
        sendAuthenticated("GET", "/contract-details?providerFirmId=123&officeId=456", "999", null);

    assertThat(response.statusCode()).isEqualTo(403);
    mockSoapServer.verify();
  }

  @Test
  void contractDetailsSucceedsWhenProviderHeaderMatches() throws Exception {
    mockSoapServer.expect(anything()).andRespond(withPayload(contractDetailsValidXml));

    HttpResponse<String> response =
        sendAuthenticated("GET", "/contract-details?providerFirmId=123&officeId=456", "123", null);

    assertThat(response.statusCode()).isEqualTo(200);
    mockSoapServer.verify();
  }

  @Test
  void createAndUpdateCaseReturnForbiddenOnProviderMismatchWithZeroSoapCalls() throws Exception {
    String casePayload =
        "{\"application_details\":{\"provider_details\":{\"provider_firm_id\":\"26517\"}}}";

    HttpResponse<String> postResponse = sendAuthenticated("POST", "/cases", "99999", casePayload);
    assertThat(postResponse.statusCode()).isEqualTo(403);

    HttpResponse<String> putResponse =
        sendAuthenticated("PUT", "/cases?case-update-type=ProviderDetails", "99999", casePayload);
    assertThat(putResponse.statusCode()).isEqualTo(403);

    HttpResponse<String> missingBodyFirmResponse =
        sendAuthenticated("POST", "/cases", "26517", "{}");
    assertThat(missingBodyFirmResponse.statusCode()).isEqualTo(403);

    mockSoapServer.verify();
  }

  @Test
  void createCaseSucceedsWhenProviderMatches() throws Exception {
    String caseAddRsXml =
        "<ns2:CaseAddRS xmlns:ns2=\"http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIM\">"
            + "<ns2:TransactionID>case-tx-777</ns2:TransactionID>"
            + "</ns2:CaseAddRS>";
    mockSoapServer.expect(anything()).andRespond(withPayload(new StringSource(caseAddRsXml)));
    String casePayload =
        "{\"application_details\":{\"provider_details\":{\"provider_firm_id\":\"26517\"}}}";

    HttpResponse<String> response = sendAuthenticated("POST", "/cases", "26517", casePayload);

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("case-tx-777");
    mockSoapServer.verify();
  }

  @Test
  void createInvoiceReturnsForbiddenOnBillAndPoaMismatchWithZeroSoapCalls() throws Exception {
    String billPayload =
        "{\"bill\":{\"case_reference_number\":\"300000123456\",\"provider_firm_id\":\"200\"}}";
    String poaPayload =
        "{\"poa\":{\"case_reference_number\":\"300000123456\",\"provider_id\":\"200\"}}";

    HttpResponse<String> billMismatch = sendAuthenticated("POST", "/invoices", "100", billPayload);
    assertThat(billMismatch.statusCode()).isEqualTo(403);

    HttpResponse<String> poaMismatch = sendAuthenticated("POST", "/invoices", "100", poaPayload);
    assertThat(poaMismatch.statusCode()).isEqualTo(403);

    mockSoapServer.verify();
  }

  @Test
  void createInvoiceSucceedsWhenProviderMatches() throws Exception {
    mockSoapServer.expect(anything()).andRespond(withPayload(invoiceAddValidXml));
    String poaPayload =
        "{\"poa\":{\"case_reference_number\":\"300000123456\",\"provider_id\":\"12345\"}}";

    HttpResponse<String> response = sendAuthenticated("POST", "/invoices", "12345", poaPayload);

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("invoice123");
    mockSoapServer.verify();
  }

  @Test
  void updateUserOptionsAllowsCrossFirmSwitchWhenHeaderCarriesCurrentFirm() throws Exception {
    mockSoapServer.expect(anything()).andRespond(withPayload(updateUserValidXml));
    String body = "{\"user_login_id\":\"testLogin\",\"provider_firm_id\":\"200\"}";

    HttpResponse<String> response = sendAuthenticated("PUT", "/users/options", "100", body);

    assertThat(response.statusCode()).isEqualTo(200);
    mockSoapServer.verify();
  }

  private HttpResponse<String> sendAuthenticated(
      final String method, final String path, final String caabProviderId, final String jsonBody)
      throws Exception {
    HttpRequest.Builder builder =
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Authorization", "Bearer " + TOKEN)
            .header("SoaGateway-User-Login-Id", USER_LOGIN)
            .header("SoaGateway-User-Role", USER_ROLE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE);

    if (caabProviderId != null) {
      builder.header(ProviderContext.HEADER_NAME, caabProviderId);
    }

    if (jsonBody != null) {
      builder
          .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
          .method(method, HttpRequest.BodyPublishers.ofString(jsonBody));
    } else {
      builder.method(method, HttpRequest.BodyPublishers.noBody());
    }

    return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
  }

  @TestConfiguration(proxyBeanMethods = false)
  static class StubAuthConfig {
    @Bean
    JwtDecoder jwtDecoder() {
      return StubJwtDecoder.of(
          new StubJwtToken(TOKEN, "integration-test", new String[] {"ALL"}, null, Map.of()));
    }
  }
}
