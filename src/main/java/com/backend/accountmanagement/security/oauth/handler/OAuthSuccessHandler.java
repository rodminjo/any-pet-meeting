package com.backend.accountmanagement.security.oauth.handler;

import com.backend.accountmanagement.account.controller.port.RefreshTokenService;
import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.security.jwt.dto.TokenResponseDto;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.security.oauth.CustomOAuth2User;
import com.backend.accountmanagement.utils.MapperUtils;
import com.backend.accountmanagement.utils.SecurityScopeUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtManager jwtManager;
  private final RefreshTokenService refreshTokenService;

  /**
   * jwt 인증 필터에게서 받아온 UsernamePasswordAuthenticationToken 을 받아 처리하는 handler
   *
   * @param request        the request which caused the successful authentication
   * @param response       the response
   * @param authentication provider가 생성한 UsernamePasswordAuthenticationToken the authentication
   *                       process.
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    Account account = ((CustomOAuth2User) authentication.getPrincipal()).getAccount();
    // refresh Token 저장 전 Audit 을 위해 principal 변경
    UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
        account.getEmail(),
        account,
        authentication.getAuthorities()
    );
    SecurityScopeUtils.setAuthentication(newAuthentication);

    // jwt token을 생성해서 response 로 반환
    String tokenType = JwtManager.BEARER;
    String jwtToken = jwtManager.generateToken(account.getEmail(), authentication.getAuthorities(),
        false);
    String jwtRefreshToken = jwtManager.generateToken(account.getEmail(),
        authentication.getAuthorities(), true);

    // jwt refreshToken save or update
    refreshTokenService.merge(account.getEmail(), jwtRefreshToken);

    // response setting
    setRefreshToken(response, jwtRefreshToken);
    response.setHeader(JwtManager.AUTHORIZATION, tokenType + " " + jwtToken);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.OK.value());

    // set tokenDto
    setBodyTokenDto(response, tokenType, jwtToken, jwtRefreshToken);
  }


  private void setBodyTokenDto(HttpServletResponse response, String tokenType, String accessToken,
      String refreshToken)
      throws IOException {
    // body -> Token Dto
    TokenResponseDto tokenDto = TokenResponseDto.builder()
        .tokenType(tokenType)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();

    // getWriter 할 경우 더이상 response 변경 안됨. 유의!
    MapperUtils.getMapper().writeValue(response.getWriter(), tokenDto);
  }


  private void setRefreshToken(HttpServletResponse response, String refreshToken) {
    // refresh token cookie
    ResponseCookie refreshTokenCookie = ResponseCookie.from(JwtManager.REFRESH_TOKEN, refreshToken)
        .maxAge(jwtManager.REFRESH_EXPIRE_TIME) // 쿠키의 만료 시간 설정 (초 단위)
        .httpOnly(true) // JavaScript에서 쿠키 접근 불가 설정
        .path("/api/reissue") // 쿠키의 유효 범위 설정
        .build();

    response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
  }
}
