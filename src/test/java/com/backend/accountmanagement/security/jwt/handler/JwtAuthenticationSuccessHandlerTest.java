package com.backend.accountmanagement.security.jwt.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.security.jwt.dto.TokenResponseDto;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.utils.MapperUtils;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

class JwtAuthenticationSuccessHandlerTest {

  static SecurityTestContainer testContainer =
      new SecurityTestContainer(new MockClockHolder(new Date()), 8640000L);
  static JwtAuthenticationSuccessHandler handler =
      new JwtAuthenticationSuccessHandler(testContainer.jwtManager, testContainer.refreshTokenService);
  static MockHttpServletRequest request = new MockHttpServletRequest();
  static MockHttpServletResponse response = new MockHttpServletResponse();

  @BeforeAll
  static void setup() throws Exception {
    handler.onAuthenticationSuccess(request, response, testContainer.token);
  }

  @Test
  void response_헤더에_accessToken을_담는다() throws ServletException, IOException {
    String header = response.getHeader(JwtManager.AUTHORIZATION);
    String accessToken = testContainer.jwtManager.resolveToken(header);
    Authentication authentication = testContainer.jwtManager.getAuthentication(accessToken);

    assertThat(header).isNotBlank();
    assertThat(authentication.getPrincipal()).isEqualTo(testContainer.email);

  }

  @Test
  void response_헤더에_refreshToken_set_cookie를_담는다() throws ServletException, IOException {
    String header = response.getHeader(HttpHeaders.SET_COOKIE);
    assertThat(header).isNotBlank();

  }

  @Test
  void DB에_refreshToken을_저장한다(){
    assertThat(testContainer.refreshTokenService.mergeCount).isEqualTo(1);

  }

  @Test
  void response에_상태코드와_contentType을_저장한다(){
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

  }

  @Test
  void response_body에_tokenDto를_저장한다(){
    assertThatCode(() -> MapperUtils.getMapper()
        .readValue(response.getContentAsByteArray(), TokenResponseDto.class));

  }

}