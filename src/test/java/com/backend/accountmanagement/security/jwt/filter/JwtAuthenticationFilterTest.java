package com.backend.accountmanagement.security.jwt.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


class JwtAuthenticationFilterTest {

  @Test
  void access_토큰이_존재하고_유효할_경우_SecurityContext에_담는다() throws ServletException, IOException {
    // given
    SecurityTestContainer testContainer =
        new SecurityTestContainer(new MockClockHolder(new Date()), 60*60*1000*24L);
    String accessToken =
        testContainer.jwtManager.generateToken(testContainer.token, false);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(JwtManager.AUTHORIZATION, JwtManager.BEARER + " " + accessToken);
    MockFilterChain filterChain = mock(MockFilterChain.class);

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(testContainer.jwtManager);

    // when
    jwtAuthenticationFilter.doFilter(request, response, filterChain);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


    // then
    assertThat(authentication.getPrincipal()).isEqualTo(testContainer.email);
    assertThat(authentication.getAuthorities().size()).isEqualTo(testContainer.authorities.size());
    assertThat(authentication.getAuthorities().stream().findFirst().orElse(null))
        .isEqualTo(testContainer.authorities.get(0));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void access_토큰이_유효히지_않을_경우_인증없이_다음필터로_이동한다() throws ServletException, IOException {
    // given
    SecurityTestContainer testContainer = new SecurityTestContainer(new MockClockHolder(new Date()), 60*60*1000*24L);
    String accessToken = "access_token";

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(JwtManager.AUTHORIZATION, JwtManager.BEARER + " " + accessToken);
    MockFilterChain filterChain = mock(MockFilterChain.class);

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(testContainer.jwtManager);

    // when
    jwtAuthenticationFilter.doFilter(request, response, filterChain);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


    // then
    assertThat(authentication).isNull();
    verify(filterChain, times(1)).doFilter(request, response);
  }

}