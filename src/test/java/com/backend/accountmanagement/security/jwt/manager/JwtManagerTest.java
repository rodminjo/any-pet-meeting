package com.backend.accountmanagement.security.jwt.manager;


import com.backend.accountmanagement.common.service.port.ClockHolder;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.utils.DateUtils;
import jakarta.servlet.http.Cookie;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtManagerTest {


  @Test
  void authentication으로_jwt토큰을_생성한다() {
    // given
    long expiredTime = 123412341234L;
    ClockHolder clockHolder = new MockClockHolder(DateUtils.getDate(2024, 1, 1));
    SecurityTestContainer testContainer = SecurityTestContainer.builder()
        .clockHolder(clockHolder)
        .expiredTime(expiredTime)
        .build();
    JwtManager jwtManager = testContainer.jwtManager;

    // when
    String accessToken = jwtManager.generateToken(testContainer.token, false);
    Authentication accessAuthentication = jwtManager.getAuthentication(accessToken);

    String refreshToken = jwtManager.generateToken(testContainer.token, true);
    Authentication refreshAuthentication = jwtManager.getAuthentication(refreshToken);

    // then
    assertThat(StringUtils.hasText(accessToken)).isTrue();
    assertThat(accessAuthentication.getPrincipal()).isEqualTo(testContainer.email);
    assertThat(accessAuthentication.getAuthorities().size()).isEqualTo(testContainer.authorities.size());
    assertThat(accessAuthentication.getAuthorities().stream().findFirst().orElse(null))
        .isEqualTo(testContainer.authorities.get(0));
    assertThat(((Date) accessAuthentication.getCredentials()).after(clockHolder.getNow()))
        .isTrue();

    assertThat(StringUtils.hasText(refreshToken)).isTrue();
    assertThat(refreshAuthentication.getPrincipal()).isEqualTo(testContainer.email);
    assertThat(refreshAuthentication.getAuthorities().size()).isEqualTo(testContainer.authorities.size());
    assertThat(refreshAuthentication.getAuthorities().stream().findFirst().orElse(null))
        .isEqualTo(testContainer.authorities.get(0));
    assertThat(((Date) refreshAuthentication.getCredentials()).after(clockHolder.getNow()))
        .isTrue();
  }

  @Test
  void token이_유효하지_않은_문자열일_경우_유효하지_않다(){
    // given
    String nullToken = null;
    String emptyToken = "";
    String invalidToken = "1293ilkasdfn;";

    long expiredTime = 123412341234L;
    ClockHolder clockHolder = new MockClockHolder(DateUtils.getDate(2024, 1, 1));
    SecurityTestContainer testContainer = SecurityTestContainer.builder()
        .clockHolder(clockHolder)
        .expiredTime(expiredTime)
        .build();
    JwtManager jwtManager = testContainer.jwtManager;

    // when
    boolean nullValid = jwtManager.validateToken(nullToken);
    boolean emptyValid = jwtManager.validateToken(emptyToken);
    boolean invalid = jwtManager.validateToken(invalidToken);

    // then
    assertThat(nullValid).isFalse();
    assertThat(emptyValid).isFalse();
    assertThat(invalid).isFalse();
  }


  @Test
  void token이_기간이_지났다면_유효하지_않다(){
    // given
    long pastTime = 100L;
    long expiredTime = 1L;
    ClockHolder clockHolder = new MockClockHolder(new Date(new Date().getTime() - pastTime));
    SecurityTestContainer testContainer = SecurityTestContainer.builder()
        .clockHolder(clockHolder)
        .expiredTime(expiredTime)
        .build();
    JwtManager jwtManager = testContainer.jwtManager;

    String accessToken = jwtManager.generateToken(testContainer.token, false);

    // when
    boolean validated = jwtManager.validateToken(accessToken);

    // then
    assertThat(validated).isFalse();

  }

  @Test
  void token이_기간이_지나지_않았다면_유효하다(){
    // given
    long expiredTime =  3 * 24 * 3600 * 1000L;
    ClockHolder clockHolder = new MockClockHolder(new Date());
    SecurityTestContainer testContainer = SecurityTestContainer.builder()
        .clockHolder(clockHolder)
        .expiredTime(expiredTime)
        .build();
    JwtManager jwtManager = testContainer.jwtManager;
    String accessToken = jwtManager.generateToken(testContainer.token, false);

    // when
    boolean validated = jwtManager.validateToken(accessToken);

    // then
    assertThat(validated).isTrue();

  }

  @Test
  void request에서_refresh토큰을_꺼내온다(){
    //given
    String refreshToken = "refresh_token";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(new Cookie(JwtManager.REFRESH_TOKEN, refreshToken));
    SecurityTestContainer testContainer = SecurityTestContainer.builder()
        .clockHolder(new MockClockHolder(new Date()))
        .expiredTime(1L)
        .build();

    // when
    String getRefreshToken = testContainer.jwtManager.resolveRefreshToken(request);

    // then
    assertThat(getRefreshToken).isEqualTo(refreshToken);
  }

  @Test
  void request에서_access토큰을_꺼내온다(){
    //given
    String accessToken = "access_token";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization",JwtManager.BEARER + " " + accessToken);
    SecurityTestContainer testContainer = SecurityTestContainer.builder()
        .clockHolder(new MockClockHolder(new Date()))
        .expiredTime(1L)
        .build();

    // when
    String getAccessToken = testContainer.jwtManager.resolveToken(request);

    // then
    assertThat(getAccessToken).isEqualTo(accessToken);
  }




}