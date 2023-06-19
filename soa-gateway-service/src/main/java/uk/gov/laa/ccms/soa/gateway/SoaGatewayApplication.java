package uk.gov.laa.ccms.soa.gateway;

import java.math.BigInteger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.notificationservice.NotificationCntInqRS;

@SpringBootApplication
public class SoaGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(SoaGatewayApplication.class, args);
	}


	@Bean
	CommandLineRunner lookup(NotificationClient quoteClient) {
		return args -> {

			NotificationCntInqRS response = quoteClient.getNotificationCount(
					"MARILYN@DESORANDCO.CO.UK",
					"EXTERNAL",
					"MARILYN@DESORANDCO.CO.UK",
					new BigInteger("100"));

			System.out.println(response.getNotificationCntLists().getNotificationsCnt().get(0).getNotificationCount());
		};

	}
}
