package com.backend.accountmanagement.jpa.configs;

import com.backend.accountmanagement.utils.SecurityScopeUtils;
import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;

@Configuration
@EnableJpaAuditing
public class AuditConfig implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityScopeUtils.getAuthentication();
    String user = authentication != null ? String.valueOf(authentication.getPrincipal()) : "";
    return Optional.of(user);
  }
}
