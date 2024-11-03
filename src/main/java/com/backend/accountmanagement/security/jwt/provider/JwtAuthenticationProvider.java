package com.backend.accountmanagement.security.jwt.provider;

import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.security.jwt.service.AccountContext;
import com.backend.accountmanagement.security.jwt.token.JwtLoginToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final UserDetailsService userDetailService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String requestId = (String) authentication.getPrincipal();
    String requestPassword = (String) authentication.getCredentials();

    // 계정 정보 찾기
    AccountContext accountContext = (AccountContext) userDetailService.loadUserByUsername(requestId);
    if (!passwordEncoder.matches(requestPassword, accountContext.getPassword())) {
      throw new BadCredentialsException(ExceptionMessage.LOGIN_FAILED);
    }

    return new UsernamePasswordAuthenticationToken(
        accountContext.getAccount().getEmail(),
        accountContext.getAccount(),
        accountContext.getAuthorities()
    );
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(JwtLoginToken.class);
  }


}
