package com.backend.accountmanagement.web.configs.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

  private final String host;
  private final Integer port;
  private final String username;
  private final String password;
  private final Integer authCodeExpirationMin;

}
