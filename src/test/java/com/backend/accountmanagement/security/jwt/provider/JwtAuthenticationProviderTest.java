package com.backend.accountmanagement.security.jwt.provider;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.security.jwt.token.JwtLoginToken;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

class JwtAuthenticationProviderTest {

  SecurityTestContainer testContainer = new SecurityTestContainer(new MockClockHolder(new Date()), 1L);
  JwtAuthenticationProvider testProvider =
      new JwtAuthenticationProvider(testContainer.userDetailService, testContainer.passwordEncoder);

  @Test
  void 찾아온_유저와_비밀번호가_맞지_않으면_오류가_발생한다(){
    String id = "test";
    String password = testContainer.password + "etc";

    assertThatThrownBy(() -> testProvider.authenticate(new JwtLoginToken(id, password)))
        .isExactlyInstanceOf(BadCredentialsException.class)
        .isInstanceOf(AuthenticationException.class)
        .hasMessage(ExceptionMessage.LOGIN_FAILED);

  }

  @Test
  void 찾아온_유저와_비밀번호가_일치한다면_인증정보가_들어온다(){
    String id = "test";
    String password = testContainer.password;

    Authentication authenticate = testProvider.authenticate(new JwtLoginToken(id, password));
    assertThat(authenticate.getPrincipal()).isEqualTo(testContainer.email);
    assertThat(authenticate.getCredentials()).isExactlyInstanceOf(Account.class);
    assertThat(authenticate.getAuthorities()).isEqualTo(testContainer.authorities);
  }
}