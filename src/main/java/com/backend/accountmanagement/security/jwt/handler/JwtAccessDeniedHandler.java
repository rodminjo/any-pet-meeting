package com.backend.accountmanagement.security.jwt.handler;

import com.backend.accountmanagement.common.controller.response.CommonApiResponse;
import com.backend.accountmanagement.common.controller.response.CommonApiResponse.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {

    CommonApiResponse<ErrorResponse> body = CommonApiResponse
        .createErrorResponse(HttpStatus.FORBIDDEN, "40301", accessDeniedException.getMessage())
        .getBody();

    String jsonResponse = objectMapper.writeValueAsString(body);

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(jsonResponse);

  }
}
