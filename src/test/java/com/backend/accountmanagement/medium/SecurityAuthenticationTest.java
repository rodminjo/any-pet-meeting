package com.backend.accountmanagement.medium;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.accountmanagement.acceptance.AcceptanceTest;
import com.backend.accountmanagement.mock.data.TestDataInitializer;
import com.backend.accountmanagement.security.jwt.dto.AccountLoginRequestDto;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.security.jwt.manager.JwtManager.JwtStatus;
import com.backend.accountmanagement.utils.MapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class SecurityAuthenticationTest extends AcceptanceTest {

  @Autowired
  private TestDataInitializer testDataInitializer;

  @SpyBean
  private JwtManager jwtManager;


  private String accessToken = "";


  @BeforeEach
  void setup() throws Exception {
    testDataInitializer.initUser();

    MvcResult mvcResult = testLogin();
    accessToken = jwtManager.resolveToken(mvcResult.getResponse().getHeader(JwtManager.AUTHORIZATION));
  }



  @Test
  void 권한이_필요하지_않은_페이지는_access_토큰이_없어도_200을_내려준다() throws Exception {
    // given
    // when
    // then
    mockMvc.perform(get("/test/all/"))
        .andExpect(status().isOk())
        .andExpect(content().string("success"));
  }

  @Test
  void 권한이_필요한_페이지에서_access_토큰이_없을_경우_401_에러를_내려준다() throws Exception {
    // given
    // when
    // then
    mockMvc.perform(
        get("/test/auth/")
    ).andExpect(status().isUnauthorized());
  }

  @Test
  void 권한이_필요한_페이지에서_access_토큰이_있으면_200를_내려준다() throws Exception {
    // given
    // when
    // then
    mockMvc.perform(
        get("/test/auth/")
            .header(JwtManager.AUTHORIZATION,JwtManager.BEARER + " " + accessToken)
        )
        .andExpect(status().isOk());
  }

  @Test
  void access_토큰이_유효하지_않을_경우_401_에러를_내려준다() throws Exception {
    // given
    doReturn(false).when(jwtManager).validateToken(anyString());
    doReturn(JwtStatus.EXPIRED).when(jwtManager).statusToken(anyString());

    // when, then
    mockMvc.perform(
        get("/test/auth/").header(JwtManager.AUTHORIZATION, JwtManager.BEARER + " " + accessToken)
    ).andExpect(status().isUnauthorized());
  }



  private MvcResult testLogin() throws Exception {
    String email = "authTest@naver.com";
    String password = "password";

    AccountLoginRequestDto dto = AccountLoginRequestDto.builder()
        .email(email)
        .password(password)
        .build();
    String json = MapperUtils.getMapper().writeValueAsString(dto);

    MvcResult mvcResult = mockMvc.perform(post("/api/login")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(json)
    ).andReturn();
    return mvcResult;
  }


}
