package com.backend.accountmanagement.security.jwt.handler;

import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.security.jwt.manager.JwtManager.JwtStatus;
import com.backend.accountmanagement.utils.MapperUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final JwtManager jwtManager;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    String accessToken = jwtManager.resolveToken(request);
    JwtStatus status = jwtManager.statusToken(accessToken);

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    if (status.equals(JwtStatus.EXPIRED)){
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      MapperUtils.getMapper().writeValue(response.getWriter(), ExceptionMessage.ACCESS_TOKEN_EXPIRED);

    } else {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ExceptionMessage.UNAUTHENTICATED);

    }


  }
}
