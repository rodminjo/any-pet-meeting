package com.backend.accountmanagement.security.configs.dsl;

import com.backend.accountmanagement.security.jwt.filter.JwtAuthenticationFilter;
import com.backend.accountmanagement.security.jwt.filter.JwtLoginProcessingFilter;
import com.backend.accountmanagement.security.jwt.handler.JwtAuthenticationFailureHandler;
import com.backend.accountmanagement.security.jwt.handler.JwtAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
public class JwtLoginDsl extends AbstractHttpConfigurer<JwtLoginDsl, HttpSecurity> {

  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
  private final JwtAuthenticationFailureHandler jwtAuthenticationFailuerHandler;
  @Override
  public void configure(HttpSecurity http) throws Exception {
    AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
    JwtLoginProcessingFilter jwtLoginProcessingFilter = new JwtLoginProcessingFilter(authenticationManager);
    jwtLoginProcessingFilter.setAuthenticationFailureHandler(jwtAuthenticationFailuerHandler);
    jwtLoginProcessingFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
    http.addFilterBefore(jwtLoginProcessingFilter, JwtAuthenticationFilter.class);
  }
}