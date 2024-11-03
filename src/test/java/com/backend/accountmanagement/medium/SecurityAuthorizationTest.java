package com.backend.accountmanagement.medium;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.accountmanagement.acceptance.AcceptanceTest;
import com.backend.accountmanagement.mock.data.TestDataInitializer;
import com.backend.accountmanagement.security.jwt.dto.AccountLoginRequestDto;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.utils.MapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class SecurityAuthorizationTest extends AcceptanceTest {

  @Autowired
  private TestDataInitializer testDataInitializer;

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

  @Test
  void 리소스_목록에_없다면_전부허용된다() throws Exception {
    mockMvc.perform(get("/test/all/"))
        .andExpect(status().isOk())
        .andExpect(content().string("success"));
  }

  @Test
  void 리소스_목록에_있지만_Role이_기재되어있지_않다면_로그인된_사용자만_허용된다() throws Exception {
    // 로그인 안됨
    mockMvc.perform(
            get("/test/auth/")
        )
        .andExpect(status().isUnauthorized());

    // 로그인 완료
    mockMvc.perform(
        get("/test/auth/")
            .header(JwtManager.AUTHORIZATION,JwtManager.BEARER + " " + accessToken)
        )
        .andExpect(status().isOk())
        .andExpect(content().string("success"));
  }

  @Test
  void 리소스_목록에_있고_허용된_Role만_접근이_허가된다() throws Exception {
    // 허용된 리소스
    mockMvc.perform(
            get("/test/auth/user")
                .header(JwtManager.AUTHORIZATION, JwtManager.BEARER + " " + accessToken)
        )
        .andExpect(status().isOk())
        .andExpect(content().string("user"));

    // 허용되지 않은 리소스
    mockMvc.perform(
            get("/test/auth/anonymous")
                .header(JwtManager.AUTHORIZATION,JwtManager.BEARER + " " + accessToken)
        )
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString("errorId")))
        .andExpect(content().string(containsString("40301")));
  }

  @Test
  void 리소스_목록에_있고_하위계층_Role은_상위계층_ROLE이_접근할_수_있다() throws Exception {
    // 하위 계층에만 허용한 리소스
    mockMvc.perform(
            get("/test/auth/user/low")
                .header(JwtManager.AUTHORIZATION, JwtManager.BEARER + " " + accessToken)
        )
        .andExpect(status().isOk());
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


}
