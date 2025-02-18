package com.backend.accountmanagement.web.configs.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

  private final String host;
  private final Integer port;
  private final String maxmemory;

}
