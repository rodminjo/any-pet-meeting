package com.backend.accountmanagement.security.configs.dsl;

import com.backend.accountmanagement.security.jwt.filter.JwtAuthenticationFilter;
import com.backend.accountmanagement.security.jwt.filter.JwtRefreshProcessingFilter;
import com.backend.accountmanagement.security.jwt.handler.JwtAuthenticationFailureHandler;
import com.backend.accountmanagement.security.jwt.handler.JwtAuthenticationSuccessHandler;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
public class JwtRefreshDsl extends AbstractHttpConfigurer<JwtRefreshDsl, HttpSecurity> {

  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
  private final JwtAuthenticationFailureHandler jwtAuthenticationFailuerHandler;
  private final JwtManager jwtManager;
  @Override
  public void configure(HttpSecurity http) throws Exception {
    AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
    JwtRefreshProcessingFilter jwtRefreshProcessingFilter =
        new JwtRefreshProcessingFilter(authenticationManager, jwtManager);
    jwtRefreshProcessingFilter.setAuthenticationFailureHandler(jwtAuthenticationFailuerHandler);
    jwtRefreshProcessingFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
    http.addFilterBefore(jwtRefreshProcessingFilter, JwtAuthenticationFilter.class);
  }
}