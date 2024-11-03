package com.backend.accountmanagement.security.oauth.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.security.jwt.dto.TokenResponseDto;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.security.oauth.CustomOAuth2User;
import com.backend.accountmanagement.utils.MapperUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

class OAuthSuccessHandlerTest {

  static SecurityTestContainer testContainer =
      new SecurityTestContainer(new MockClockHolder(new Date()), 8640000L);
  static OAuthSuccessHandler handler =
      new OAuthSuccessHandler(testContainer.jwtManager, testContainer.refreshTokenService);
  static MockHttpServletRequest request = new MockHttpServletRequest();
  static MockHttpServletResponse response = new MockHttpServletResponse();

  @BeforeAll
  static void setup() throws Exception {
    String content = "{\n"
        + "  \"sub\": \"103058387739722400130\",\n"
        + "  \"name\": \"구글\",\n"
        + "  \"given_name\": \"글\",\n"
        + "  \"family_name\": \"구\",\n"
        + "  \"picture\": \"google-picture.com\",\n"
        + "  \"email\": \"google@gmail.com\",\n"
        + "  \"email_verified\": true,\n"
        + "  \"locale\": \"ko\"\n"
        + "}";
    TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {};
    Map<String, Object> attribute = MapperUtils.getMapper().readValue(content, type);

    Account account = Account.builder()
        .name((String) attribute.get("name"))
        .email((String) attribute.get("email"))
        .profileImageUrl((String) attribute.get("picture"))
        .build();
    Role role = Role.builder()
        .roleName("ROLE_USER")
        .roleDesc("DEFAULT")
        .build();
    account.getRoleSet().add(role);
    ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>(List.of(new SimpleGrantedAuthority("ROLE_USER")));

    OAuth2User user = new CustomOAuth2User(account, authorities, attribute, "sub");
    OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(user, testContainer.authorities, "google");
    handler.onAuthenticationSuccess(request, response, token);
  }



  @Test
  void response_헤더에_accessToken을_담는다() {
    String header = response.getHeader(JwtManager.AUTHORIZATION);
    String accessToken = testContainer.jwtManager.resolveToken(header);
    Authentication authentication = testContainer.jwtManager.getAuthentication(accessToken);

    assertThat(header).isNotBlank();
    assertThat(authentication.getPrincipal()).isEqualTo("google@gmail.com");

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