package com.backend.accountmanagement.security.jwt.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.security.jwt.token.JwtLoginToken;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class JwtLoginProcessingFilterTest {

  @Test
  void request에_로그인_정보가_존재하지_않으면_오류가_발생한다(){
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

    JwtLoginProcessingFilter jwtLoginProcessingFilter =
        new JwtLoginProcessingFilter(authenticationManager);

    // when

    // then
    assertThatThrownBy(() -> jwtLoginProcessingFilter.attemptAuthentication(request, response))
        .isExactlyInstanceOf(UsernameNotFoundException.class)
        .isInstanceOf(RuntimeException.class)
        .hasMessage(ExceptionMessage.LOGIN_INFO_NOT_FOUND);
  }

  @Test
  void request에_로그인_정보가_존재하면_토큰을_생성하고_provider에게_넘겨준다() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    String email = "test@test.com";
    String password = "testPassword";

    String jsonContent = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}";
    request.setContent(jsonContent.getBytes(StandardCharsets.UTF_8));

    JwtLoginProcessingFilter jwtLoginProcessingFilter =
        new JwtLoginProcessingFilter(authenticationManager);

    // when
    jwtLoginProcessingFilter.attemptAuthentication(request, response);

    // then
    ArgumentCaptor<Authentication> argumentCaptor = ArgumentCaptor.forClass(Authentication.class);
    verify(authenticationManager, times(1)).authenticate(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).isExactlyInstanceOf(JwtLoginToken.class);
    assertThat(argumentCaptor.getValue().getPrincipal()).isEqualTo(email);
    assertThat(argumentCaptor.getValue().getCredentials()).isEqualTo(password);

  }


}