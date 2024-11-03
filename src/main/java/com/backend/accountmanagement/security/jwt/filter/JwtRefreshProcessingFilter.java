package com.backend.accountmanagement.security.jwt.filter;


import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.security.jwt.token.JwtRefreshToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;


/**
 * JWT 리프레시 인증 필터
 * URL : /api/reissue
 */
public class JwtRefreshProcessingFilter extends AbstractAuthenticationProcessingFilter {

  private final JwtManager jwtManager;


  public JwtRefreshProcessingFilter(AuthenticationManager authenticationManager, JwtManager jwtManager) {
    super(new AntPathRequestMatcher("/api/reissue", HttpMethod.POST.name()), authenticationManager);
    this.jwtManager = jwtManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    // Jwt refresh 요청인지 검증후 처리
    String refreshToken = jwtManager.resolveRefreshToken(request);

    if (!StringUtils.hasText(refreshToken)){
      throw new UsernameNotFoundException(ExceptionMessage.REFRESH_TOKEN_NOT_FOUND);
    }

    JwtRefreshToken jwtRefreshToken = new JwtRefreshToken(refreshToken, "", List.of());

    return getAuthenticationManager().authenticate(jwtRefreshToken);
  }

}
