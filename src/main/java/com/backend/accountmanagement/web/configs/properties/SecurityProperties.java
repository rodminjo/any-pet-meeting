package com.backend.accountmanagement.web.configs.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "jwt.token")
public class SecurityProperties {

  private String secretKey;
  private Long expireTime;
  private Long refreshExpireTime;

  public SecurityProperties(String secretKey, Long expireTime, Long refreshExpireTime) {
    this.secretKey = secretKey;
    this.expireTime = expireTime;
    this.refreshExpireTime = refreshExpireTime;
  }
}
