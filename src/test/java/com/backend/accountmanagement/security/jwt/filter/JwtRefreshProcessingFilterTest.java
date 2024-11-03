package com.backend.accountmanagement.security.jwt.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class JwtRefreshProcessingFilterTest {

  @Test
  void 토큰이_존재하지_않는다면_오류를_출력한다() throws ServletException, IOException {
    // given
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    SecurityTestContainer testContainer =
        new SecurityTestContainer(new MockClockHolder(new Date()), 1L);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();

    JwtRefreshProcessingFilter jwtRefreshProcessingFilter =
        new JwtRefreshProcessingFilter(authenticationManager, testContainer.jwtManager);

    // when
    assertThatThrownBy(() -> jwtRefreshProcessingFilter.attemptAuthentication(request, response))
        .isExactlyInstanceOf(UsernameNotFoundException.class)
        .isInstanceOf(RuntimeException.class)
        .hasMessage(ExceptionMessage.REFRESH_TOKEN_NOT_FOUND);

    verify(authenticationManager, times(0)).authenticate(any());
  }

  @Test
  void 토큰이_존재하면_Provider에게_refreshToken을_전달한다() throws ServletException, IOException {
    // given
    String refreshToken = "invalid_token";
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    SecurityTestContainer testContainer =
        new SecurityTestContainer(new MockClockHolder(new Date()), 1L);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(new Cookie(JwtManager.REFRESH_TOKEN, refreshToken));

    JwtRefreshProcessingFilter jwtRefreshProcessingFilter =
        new JwtRefreshProcessingFilter(authenticationManager, testContainer.jwtManager);

    // when
    jwtRefreshProcessingFilter.attemptAuthentication(request, response);

    // then
    ArgumentCaptor<Authentication> argumentCaptor = ArgumentCaptor.forClass(Authentication.class);
    verify(authenticationManager, times(1)).authenticate(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue().getPrincipal()).isEqualTo(refreshToken);
  }

}