package uk.gov.laa.ccms.soa.gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@ExtendWith(MockitoExtension.class)
public class SoaConfigurationTest {

  @InjectMocks private SoaConfiguration soaConfiguration;

  @Mock private Jaxb2Marshaller marshaller;

  @Test
  public void testWebServiceTemplate() {
    // Create mock values for the properties
    String username = "testuser";
    String password = "testpassword";

    // Create a new instance of SoaConfiguration
    SoaConfiguration soaConfiguration = new SoaConfiguration(username, password);

    // Call the webServiceTemplate method
    WebServiceTemplate result = soaConfiguration.webServiceTemplate(marshaller);

    // Verify that the marshaller and unmarshaller are set correctly
    assertEquals(marshaller, result.getMarshaller());
    assertEquals(marshaller, result.getUnmarshaller());
  }

  @Test
  public void testMarshaller() {
    // Create a new instance of SoaConfiguration
    SoaConfiguration soaConfiguration = new SoaConfiguration("testuser", "testpassword");

    // Call the marshaller() method
    Jaxb2Marshaller result = soaConfiguration.marshaller();

    // Verify that the packages to scan are set correctly
    String[] packagesToScan = result.getPackagesToScan();
    assertEquals(2, packagesToScan.length);
    assertEquals("uk.gov.legalservices", packagesToScan[0]);
    assertEquals("uk.gov.gsi.legalaid", packagesToScan[1]);
  }
}
