package uk.gov.laa.ccms.soa.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the SOA Gateway Application. */
@SpringBootApplication
public class SoaGatewayApplication {
  public static void main(String[] args) {
    SpringApplication.run(SoaGatewayApplication.class, args);
  }
}
