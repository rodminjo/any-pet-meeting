package com.backend.accountmanagement.web.configs;

import com.backend.accountmanagement.utils.MapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class JacksonConfig {

  @Bean
  @Primary
  public ObjectMapper serializingObjectMapper() {
    return MapperUtils.getMapper();
  }

}
