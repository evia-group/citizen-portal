package com.evia.portal.userportal;

import com.evia.portal.userportal.core.util.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class UserPortalApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserPortalApplication.class, args);
  }
}
