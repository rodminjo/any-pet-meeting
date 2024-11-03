package com.backend.accountmanagement.security.configs;

import com.backend.accountmanagement.security.authorization.factory.BannedIpMapFactoryBean;
import com.backend.accountmanagement.security.authorization.factory.RoleHierarchyMapFactoryBean;
import com.backend.accountmanagement.security.authorization.factory.UrlResourceMapFactoryBean;
import com.backend.accountmanagement.security.authorization.manager.UrlBaseAuthorizationManager;
import com.backend.accountmanagement.security.configs.dsl.JwtLoginDsl;
import com.backend.accountmanagement.security.configs.dsl.JwtRefreshDsl;
import com.backend.accountmanagement.security.jwt.filter.JwtAuthenticationFilter;
import com.backend.accountmanagement.security.jwt.handler.JwtAccessDeniedHandler;
import com.backend.accountmanagement.security.jwt.handler.JwtAuthenticationEntryPoint;
import com.backend.accountmanagement.security.jwt.handler.JwtAuthenticationFailureHandler;
import com.backend.accountmanagement.security.jwt.handler.JwtAuthenticationSuccessHandler;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.security.jwt.provider.JwtAuthenticationProvider;
import com.backend.accountmanagement.security.jwt.provider.JwtRefreshProvider;
import com.backend.accountmanagement.security.oauth.handler.OAuthFailureHandler;
import com.backend.accountmanagement.security.oauth.handler.OAuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationProvider jwtAuthenticationProvider;
  private final JwtRefreshProvider jwtRefreshProvider;

  private final JwtManager jwtManager;

  private final JwtAuthenticationFailureHandler jwtAuthenticationFailuerHandler;
  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  private final OAuthSuccessHandler oAuthSuccessHandler;
  private final OAuthFailureHandler oAuthFailureHandler;

  private final UrlResourceMapFactoryBean urlResourceMapFactoryBean;
  private final RoleHierarchyMapFactoryBean roleHierarchyMapFactoryBean;
  private final BannedIpMapFactoryBean bannedIpMapFactoryBean;


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests((auth) -> auth
            // resources 전부 허용
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers(
                "/",
                "/error",
                "/view/**",
                "/api/login",
                "/api/join",
                "/api/reissue"
            ).permitAll()
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().access(
                new UrlBaseAuthorizationManager(
                    urlResourceMapFactoryBean.getObject(),
                    bannedIpMapFactoryBean.getObject(),
                    roleHierarchyMapFactoryBean.getObject()
                )
            )
        )
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .exceptionHandling((exception) -> exception
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
        )
        .oauth2Login(oauth -> oauth
            .userInfoEndpoint(Customizer.withDefaults())
            .successHandler(oAuthSuccessHandler)
            .failureHandler(oAuthFailureHandler)
        )
        .authenticationProvider(jwtAuthenticationProvider)
        .authenticationProvider(jwtRefreshProvider)
        .with(new JwtRefreshDsl(jwtAuthenticationSuccessHandler, jwtAuthenticationFailuerHandler, jwtManager), jwtRefreshDsl -> {})
        .with(new JwtLoginDsl(jwtAuthenticationSuccessHandler, jwtAuthenticationFailuerHandler), jwtLoginDsl -> {})
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    ;

    return http.build();
  }
}
