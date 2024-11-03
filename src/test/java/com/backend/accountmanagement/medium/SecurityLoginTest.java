package com.backend.accountmanagement.medium;

import static com.backend.accountmanagement.utils.ApiDocumentUtils.getDocumentRequest;
import static com.backend.accountmanagement.utils.ApiDocumentUtils.getDocumentResponse;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.accountmanagement.acceptance.AcceptanceTest;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.mock.data.TestDataInitializer;
import com.backend.accountmanagement.security.jwt.dto.AccountLoginRequestDto;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.utils.MapperUtils;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

@AutoConfigureRestDocs
public class SecurityLoginTest extends AcceptanceTest {

  @Autowired
  TestDataInitializer testDataInitializer;

  @BeforeEach
  void setup() {
    testDataInitializer.initUser();
  }


  /**
   * https://github.com/spring-projects/spring-security/issues/14418
   * 현재 @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) 를 사용할 경우 MockMvc오류 발생
   * 때문에 현재 RandomPort 사용 안함
   * @throws Exception
   */
  @Test
  void 메인페이지_접속을_수행한다() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(get("/"))
        .andExpect(status().isOk());

  }

  @Test
  void 잘못된_방식으로_로그인_시도할_경우_404_에러를_내려준다() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(get("/api/login"))
        .andExpect(status().is4xxClientError())
        .andExpect(status().isNotFound())
        .andDo(print());

  }


  @Test
  void 로그인_시도할_경우_내용이_유효하지_않으면_401_에러를_내려준다() throws Exception {
    //given
    String email = "test@naver.com";
    String password = "";
    String json = getAccountLoginRequestDtoJson(email, password);

    //when
    //then
    mockMvc.perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().is4xxClientError())
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(ExceptionMessage.LOGIN_INFO_NOT_FOUND)))
        .andDo(print());

  }

  @Test
  void 로그인_시도할_경우_회원이_존재하지_않으면_401_에러를_내려준다() throws Exception {
    //given
    String email = "invalid_test@naver.com";
    String password = "password";
    String json = getAccountLoginRequestDtoJson(email, password);

    //when
    //then
    mockMvc.perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().is4xxClientError())
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(ExceptionMessage.ACCOUNT_NOT_FOUND)))
        .andDo(print());

  }

  @Test
  void 로그인_시도할_경우_비밀번호가_맞지_않으면_401_에러를_내려준다() throws Exception {
    // given
    String email = "test@naver.com";
    String password = "invalid_password";
    String json = getAccountLoginRequestDtoJson(email, password);

    //when
    //then
    mockMvc.perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().is4xxClientError())
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(ExceptionMessage.LOGIN_FAILED)))
        .andDo(print());

  }

  @Test
  void 정상적인_로그인을_시도할_경우_200과_토큰을_내려준다() throws Exception {
    //given
    String email = "test@naver.com";
    String password = "password";
    String json = getAccountLoginRequestDtoJson(email, password);

    //when
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("refreshToken")))
        .andExpect(content().string(containsString("accessToken")))
        .andExpect(content().string(containsString("tokenType")))
        .andExpect(header().exists(JwtManager.AUTHORIZATION))
        .andExpect(header().exists(HttpHeaders.SET_COOKIE))
        .andDo(print())
        .andDo(document("login",
            getDocumentRequest(),
            getDocumentResponse(),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("회원 API")
                    .summary("로그인 API")
                    .requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("아이디"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                    )
                    .responseFields(
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("갱신토큰"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("접근토큰"),
                                fieldWithPath("tokenType").type(JsonFieldType.STRING).description("토큰타입")
                    )
                    .responseHeaders(
                        headerWithName(JwtManager.AUTHORIZATION).defaultValue(JwtManager.BEARER + " ").description("accessToken"),
                        headerWithName(HttpHeaders.SET_COOKIE).description("set cookie for refreshToken"))
                    .requestSchema(Schema.schema("AccountLoginRequestDto"))
                    .responseSchema(Schema.schema("CommonApiResponse.login"))
                    .build()
            )
        ));

  }

  @Test
  @DisplayName("구글로그인 주소로 접속하면 로그인 페이지로 리다이렉트 된다")
  void test_google_login_case1() throws Exception {
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.get("/oauth2/authorization/google"))
        .andExpect(status().is3xxRedirection())
        .andDo(print())
        .andDo(document("naver login",
                getDocumentRequest(),
                getDocumentResponse(),
                resource(ResourceSnippetParameters.builder()
                    .tag("회원 API")
                    .summary("네이버로그인 API")
                    .build()
                )
            )
        );

  }

  @Test
  @DisplayName("네이버로그인 주소로 접속하면 로그인 페이지로 리다이렉트 된다")
  void test_naver_login_case1() throws Exception {
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.get("/oauth2/authorization/naver"))
        .andExpect(status().is3xxRedirection())
        .andDo(print())
        .andDo(document("google login",
                getDocumentRequest(),
                getDocumentResponse(),
                resource(ResourceSnippetParameters.builder()
                    .tag("회원 API")
                    .summary("구글로그인 API")
                    .build()
                )
            )
        );
  }

  private String getAccountLoginRequestDtoJson(String email, String password)
      throws JsonProcessingException {
    AccountLoginRequestDto dto = AccountLoginRequestDto.builder()
        .email(email)
        .password(password)
        .build();
    String json = MapperUtils.getMapper().writeValueAsString(dto);

    return json;
  }





}
