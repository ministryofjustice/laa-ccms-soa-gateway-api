package uk.gov.laa.ccms.soa.gateway.config;

import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

@Configuration
public class SoaConfiguration {
    private final String username;
    private final String password;
    private final String url;

    public SoaConfiguration(@Value("${laa.ccms.soa-gateway.notification.url}")String url,
                                     @Value("${laa.ccms.soa-gateway.username}")String username,
                                     @Value("${laa.ccms.soa-gateway.password}")String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("uk.gov.legalservices");
        return marshaller;
    }

    @Bean
    public NotificationClient notificationClient(Jaxb2Marshaller marshaller) {
        NotificationClient client = new NotificationClient();
        client.setDefaultUri(this.url);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        client.setInterceptors(new ClientInterceptor[]{ securityInterceptor() });
        return client;
    }

    @Bean
    public Wss4jSecurityInterceptor securityInterceptor() {
        Wss4jSecurityInterceptor security = new Wss4jSecurityInterceptor();
        security.setSecurementActions(WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.USERNAME_TOKEN);
        security.setSecurementPasswordType(WSConstants.PW_TEXT);
        security.setSecurementUsername(this.username);
        security.setSecurementPassword(this.password);
        return security;
    }

}
