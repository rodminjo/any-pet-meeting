package com.backend.accountmanagement.security.jwt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class JwtUserDetailServiceTest {


  @Test
  void 인증정보에_해당하는_인증이_없다면_오류가_발생한다(){
    // given
    SecurityTestContainer testContainer = new SecurityTestContainer(new MockClockHolder(new Date()), 1L);
    JwtUserDetailService jwtUserDetailService =
        new JwtUserDetailService(testContainer.accountRepository);

    // when
    // then
    assertThatThrownBy(() -> jwtUserDetailService.loadUserByUsername(testContainer.email))
        .isExactlyInstanceOf(UsernameNotFoundException.class)
        .isInstanceOf(AuthenticationException.class)
        .hasMessage(ExceptionMessage.ACCOUNT_NOT_FOUND);

  }

  @Test
  void 인증정보에_해당하는_계정이_있다면_인증객체를_반환한다(){
    // given
    SecurityTestContainer testContainer = new SecurityTestContainer(new MockClockHolder(new Date()), 1L);
    testContainer.accountRepository.save(testContainer.account);
    JwtUserDetailService jwtUserDetailService =
        new JwtUserDetailService(testContainer.accountRepository);

    // when
    UserDetails result = jwtUserDetailService.loadUserByUsername(testContainer.email);

    // then
    assertThat(result).isExactlyInstanceOf(AccountContext.class);
    assertThat(((AccountContext) result).getAccount().getName()).isEqualTo(testContainer.account.getName());
    assertThat(((AccountContext) result).getAccount().getEmail()).isEqualTo(testContainer.account.getEmail());
    assertThat(result.getUsername()).isEqualTo(testContainer.email);
    assertThat(testContainer.passwordEncoder.matches(testContainer.password, result.getPassword())).isTrue();
    assertThat(result.getAuthorities().size()).isEqualTo(testContainer.authorities.size());
    assertThat(result.getAuthorities().stream().findFirst().orElse(null).getAuthority())
        .isEqualTo(testContainer.account.getRoleSet().stream().findFirst().orElse(null).getRoleName());

  }

}