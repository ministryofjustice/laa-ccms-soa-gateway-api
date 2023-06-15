package uk.gov.laa.ccms.soa.gateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;

@SpringBootApplication
public class SoaGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(SoaGatewayApplication.class, args);
	}


	@Bean
	CommandLineRunner lookup(NotificationClient quoteClient) {
		return args -> {

			NotificationCntInqRS response = quoteClient.getNotificationCount();
			System.out.println(response.getNotificationCntLists().getNotificationsCnt().get(0).getNotificationCount());
		};

	}
}
