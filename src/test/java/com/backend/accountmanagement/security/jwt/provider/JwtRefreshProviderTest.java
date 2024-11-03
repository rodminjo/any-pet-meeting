package com.backend.accountmanagement.security.jwt.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.security.jwt.token.JwtLoginToken;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

class JwtRefreshProviderTest {

  @Test
  void refresh토큰이_유효하지_않으면_오류가_발생된다(){
    // given
    SecurityTestContainer testContainer =
        new SecurityTestContainer(new MockClockHolder(new Date()), 1L);
    JwtRefreshProvider jwtRefreshProvider =
        new JwtRefreshProvider(testContainer.refreshTokenService, testContainer.jwtManager);

    // when

    // then
    assertThatThrownBy(() -> jwtRefreshProvider.authenticate(testContainer.token))
        .isExactlyInstanceOf(CredentialsExpiredException.class)
        .isInstanceOf(AuthenticationException.class)
        .hasMessage(ExceptionMessage.REFRESH_TOKEN_EXPIRED);

  }

  @Test
  void 인증정보를_반환한다(){
    // given
    SecurityTestContainer testContainer =
        new SecurityTestContainer(new MockClockHolder(new Date()), 86400000L);
    JwtRefreshProvider jwtRefreshProvider =
        new JwtRefreshProvider(testContainer.refreshTokenService, testContainer.jwtManager);
    String testToken = testContainer.jwtManager.generateToken(testContainer.token, true);

    // when
    Authentication authenticate = jwtRefreshProvider
        .authenticate(new JwtLoginToken(testToken, "", testContainer.authorities));

    // then
    assertThat(authenticate).isExactlyInstanceOf(UsernamePasswordAuthenticationToken.class);
    assertThat(authenticate.getPrincipal()).isEqualTo(testContainer.email);
    assertThat(authenticate.getAuthorities().size()).isEqualTo(testContainer.authorities.size());
    assertThat(authenticate.getAuthorities().stream().findFirst().orElse(null))
        .isEqualTo(testContainer.authorities.get(0));
  }

}