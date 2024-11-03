package com.backend.accountmanagement.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.accountmanagement.account.controller.port.RefreshTokenService;
import com.backend.accountmanagement.account.domain.RefreshToken;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

class RefreshTokenServiceTest {


  private RefreshTokenService refreshTokenService;

  @BeforeEach
  void setUp() {
    MockClockHolder mockClockHolder = new MockClockHolder(new Date());
    SecurityTestContainer testContainer = new SecurityTestContainer(mockClockHolder, 10000L);
    refreshTokenService = new RefreshTokenServiceImpl(testContainer.refreshTokenRepository);
    RefreshToken testRefreshToken = RefreshToken.builder()
        .email(testContainer.email)
        .refreshToken("testRefreshToken")
        .build();

    testContainer.refreshTokenRepository.save(testRefreshToken);
  }

  @Test
  void 아이디가_발급받은_리프레시토큰이_DB에_존재하지_않으면_오류를_낸다(){
    String wrongEmail = "wrong_email";
    String refreshToken = "testRefreshToken";

    assertThatThrownBy(() -> refreshTokenService.checkRefreshToken(wrongEmail, refreshToken))
        .isInstanceOf(AuthenticationException.class)
        .isExactlyInstanceOf(BadCredentialsException.class)
        .hasMessage(ExceptionMessage.REFRESH_TOKEN_NOT_VAILD);

  }

  @Test
  void 아이디가_발급받은_리프레시토큰이_DB와_다르면_오류를_낸다(){
    String email = "test@example.com";
    String invalidRefreshToken = "invalidTestRefreshToken";

    assertThatThrownBy(() -> refreshTokenService.checkRefreshToken(email, invalidRefreshToken))
        .isInstanceOf(AuthenticationException.class)
        .isExactlyInstanceOf(BadCredentialsException.class)
        .hasMessage(ExceptionMessage.REFRESH_TOKEN_NOT_VAILD);

  }

  @Test
  void 리프레시토큰과_발급받은_아이디를_확인하여_DB에서_가져온다(){
    String email = "test@example.com";
    String refreshToken = "testRefreshToken";

    RefreshToken findToken = refreshTokenService.checkRefreshToken(email, refreshToken);
    assertThat(findToken.getRefreshToken()).isEqualTo(refreshToken);
    assertThat(findToken.getEmail()).isEqualTo(email);

  }

  @Test
  void 아이디가_발급받은_토큰이_없다면_신규로_저장한다(){
    // given
    String email = "testNew@example.com";
    String refreshToken = "testRefreshToken";

    // when
    refreshTokenService.merge(email, refreshToken);

    // then
    RefreshToken findToken = refreshTokenService.checkRefreshToken(email, refreshToken);
    assertThat(findToken.getRefreshToken()).isEqualTo(refreshToken);
    assertThat(findToken.getEmail()).isEqualTo(email);

  }

  @Test
  void 아이디가_발급받은_토큰이_있다면_새로운_토큰으로_업데이트한다(){
    // given
    String email = "test@example.com";
    String oldRefreshToken = "testRefreshToken";
    String newRefreshToken = "testNewRefreshToken";

    // when
    refreshTokenService.merge(email, newRefreshToken);

    // then
    assertThatThrownBy(()->refreshTokenService.checkRefreshToken(email, oldRefreshToken))
        .isInstanceOf(AuthenticationException.class)
        .isExactlyInstanceOf(BadCredentialsException.class)
        .hasMessage(ExceptionMessage.REFRESH_TOKEN_NOT_VAILD);

    RefreshToken findToken = refreshTokenService.checkRefreshToken(email, newRefreshToken);
    assertThat(findToken.getEmail()).isEqualTo(email);
    assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);

  }
}