package uk.gov.laa.ccms.soa.gateway.config;

import org.apache.wss4j.common.ConfigurationConstants;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSConfig;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.config.SoaConfiguration;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SoaConfigurationTest {

    @InjectMocks
    private SoaConfiguration soaConfiguration;

    @Mock
    private Jaxb2Marshaller marshaller;

    @Test
    public void testNotificationClient() {
        // Create mock values for the properties
        String url = "https://example.com/notification";
        String username = "testuser";
        String password = "testpassword";

        // Create a new instance of SoaConfiguration
        SoaConfiguration soaConfiguration = new SoaConfiguration(url, username, password);

        // Call the notificationClient method
        NotificationClient result = soaConfiguration.notificationClient(marshaller);

        // Verify that the default URI is set correctly
        assertEquals(url, result.getDefaultUri());

        // Verify that the marshaller and unmarshaller are set correctly
        assertEquals(marshaller, result.getMarshaller());
        assertEquals(marshaller, result.getUnmarshaller());
    }

    @Test
    public void testMarshaller() {
        // Create a new instance of SoaConfiguration
        SoaConfiguration soaConfiguration = new SoaConfiguration("https://example.com/notification", "testuser", "testpassword");

        // Call the marshaller() method
        Jaxb2Marshaller result = soaConfiguration.marshaller();

        // Verify that the packages to scan are set correctly
        String[] packagesToScan = result.getPackagesToScan();
        assertEquals(1, packagesToScan.length);
        assertEquals("uk.gov.legalservices", packagesToScan[0]);
    }
}
