package com.backend.accountmanagement.web.configs.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.mail.properties.mail.smtp")
public class SmtpProperties {

  private final Boolean auth;
  private final Integer connectionTimeout;
  private final Integer timeout;
  private final Integer writeTimeout;
  private final Starttls starttls;


  @Getter
  @RequiredArgsConstructor
  public static class Starttls {

    private final Boolean enable;
    private final Boolean required;

  }
}
