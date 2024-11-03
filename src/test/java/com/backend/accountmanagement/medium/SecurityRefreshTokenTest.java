package com.backend.accountmanagement.medium;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.accountmanagement.acceptance.AcceptanceTest;
import com.backend.accountmanagement.mock.data.TestDataInitializer;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.security.jwt.dto.AccountLoginRequestDto;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.utils.MapperUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;


public class SecurityRefreshTokenTest extends AcceptanceTest {

  @Autowired
  TestDataInitializer testDataInitializer;

  @Autowired
  private JwtManager jwtManager;

  private String accessToken = "";
  private String refreshToken = "";


  @BeforeEach
  void setup() throws Exception {
    testDataInitializer.initUser();
    MvcResult loginResult = loginBeforeTest();
    refreshToken = loginResult.getResponse().getCookie(JwtManager.REFRESH_TOKEN).getValue();
    accessToken = jwtManager.resolveToken(loginResult.getResponse().getHeader(JwtManager.AUTHORIZATION));
  }

  private MvcResult loginBeforeTest() throws Exception {
    AccountLoginRequestDto dto = AccountLoginRequestDto.builder()
        .email("authTest@naver.com")
        .password("password")
        .build();
    String json = MapperUtils.getMapper().writeValueAsString(dto);

    MvcResult mvcResult = mockMvc.perform(post("/api/login")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(json)
    ).andReturn();

    return mvcResult;
  }

  @Test
  void refreshToken이_없다면_401에러를_반환한다() throws Exception {
    Cookie cookie = new Cookie(JwtManager.REFRESH_TOKEN, refreshToken);

    mockMvc.perform(post("/api/reissue"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(ExceptionMessage.REFRESH_TOKEN_NOT_FOUND)));
  }

  @Test
  void refreshToken이_유효하지_않다면_401에러를_반환한다() throws Exception {
    Cookie cookie = new Cookie(JwtManager.REFRESH_TOKEN, "invalid_token");

    mockMvc.perform(post("/api/reissue").cookie(cookie))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(ExceptionMessage.REFRESH_TOKEN_EXPIRED)));
  }

  @Test
  void refreshToken이_유효하지만_DB와_다르다면_401에러를_반환한다() throws Exception {
    Cookie cookie = new Cookie(JwtManager.REFRESH_TOKEN, accessToken);

    mockMvc.perform(post("/api/reissue").cookie(cookie))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(ExceptionMessage.REFRESH_TOKEN_NOT_VAILD)));
  }

  @Test
  void refreshToken이_유효하다면_200과_토큰을_반환한다() throws Exception {
    Cookie cookie = new Cookie(JwtManager.REFRESH_TOKEN, refreshToken);

    mockMvc.perform(post("/api/reissue").cookie(cookie))
        .andExpect(status().isOk())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("refreshToken")))
        .andExpect(content().string(containsString("accessToken")))
        .andExpect(content().string(containsString("tokenType")))
        .andExpect(header().exists(JwtManager.AUTHORIZATION))
        .andExpect(header().exists(HttpHeaders.SET_COOKIE))
        .andDo(print());
  }


}
