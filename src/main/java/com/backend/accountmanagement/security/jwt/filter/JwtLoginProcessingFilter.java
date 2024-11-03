package com.backend.accountmanagement.security.jwt.filter;

import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.security.jwt.dto.AccountLoginRequestDto;
import com.backend.accountmanagement.security.jwt.token.JwtLoginToken;
import com.backend.accountmanagement.utils.MapperUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

/**
 * JWT 로그인 인증 필터
 * URL : /api/login
 */
public class JwtLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

  public JwtLoginProcessingFilter(AuthenticationManager authenticationManager) {
    super(new AntPathRequestMatcher("/api/login", HttpMethod.POST.name()), authenticationManager);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

    // 로그인 요청
    AccountLoginRequestDto loginInfo;
    try {
      loginInfo = MapperUtils.getMapper().readValue(request.getInputStream(), AccountLoginRequestDto.class);

    } catch (Exception e){
      loginInfo = new AccountLoginRequestDto();

    }

    if (!StringUtils.hasText(loginInfo.getEmail()) || !StringUtils.hasText(loginInfo.getPassword())) {
      throw new UsernameNotFoundException(ExceptionMessage.LOGIN_INFO_NOT_FOUND);
    }

    // 토큰 생성하여 인증 요청을 처리할 매니저에게 전달
    JwtLoginToken jwtLoginToken =
        new JwtLoginToken(loginInfo.getEmail(), loginInfo.getPassword());

    return getAuthenticationManager().authenticate(jwtLoginToken);

  }
}
