package com.backend.accountmanagement.security.jwt.provider;

import com.backend.accountmanagement.account.controller.port.RefreshTokenService;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.security.jwt.token.JwtRefreshToken;
import com.backend.accountmanagement.account.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtRefreshProvider implements AuthenticationProvider {

  private final RefreshTokenService refreshTokenService;
  private final JwtManager jwtManager;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    // 계정 정보 찾기
    String refreshToken = (String) authentication.getPrincipal();
    boolean isVaild = jwtManager.validateToken(refreshToken);
    if (!isVaild){
      throw new CredentialsExpiredException(ExceptionMessage.REFRESH_TOKEN_EXPIRED);
    }

    // db에서 refresh token을 가져오고 문제가 없다면 저장
    RefreshToken findToken = refreshTokenService
        .checkRefreshToken((String) jwtManager.getAuthentication(refreshToken).getPrincipal(), refreshToken);

    return new UsernamePasswordAuthenticationToken(
        findToken.getEmail(),
        findToken,
        authentication.getAuthorities()
    );
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(JwtRefreshToken.class);
  }


}
