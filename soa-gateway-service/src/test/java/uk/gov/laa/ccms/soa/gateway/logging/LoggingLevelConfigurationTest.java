package uk.gov.laa.ccms.soa.gateway.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

class LoggingLevelConfigurationTest {

  @Test
  void springsRejectedValueWarningsAreSwitchedOff() {
    var yaml = new YamlPropertiesFactoryBean();
    yaml.setResources(new ClassPathResource("application.yml"));

    assertThat(
            yaml.getObject()
                .getProperty(
                    "logging.level.org.springframework.web.servlet.mvc.support"
                        + ".DefaultHandlerExceptionResolver"))
        .isEqualTo("ERROR");
  }
}
