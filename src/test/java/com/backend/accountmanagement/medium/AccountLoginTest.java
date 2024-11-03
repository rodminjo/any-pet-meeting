package com.backend.accountmanagement.medium;

import static com.backend.accountmanagement.utils.ApiDocumentUtils.getDocumentRequest;
import static com.backend.accountmanagement.utils.ApiDocumentUtils.getDocumentResponse;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.accountmanagement.acceptance.AcceptanceTest;
import com.backend.accountmanagement.account.controller.dto.AccountRequestDto.AccountCreateRequestDto;
import com.backend.accountmanagement.common.controller.dto.ValidationMessage;
import com.backend.accountmanagement.mock.data.TestDataInitializer;
import com.backend.accountmanagement.utils.MapperUtils;
import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;


public class AccountLoginTest extends AcceptanceTest {

  @Autowired
  TestDataInitializer testDataInitializer;

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "1q2w3e!!"))
      .withPerMethodLifecycle(false);


  @BeforeEach
  void setup() {
    testDataInitializer.initUser();
  }


  @Test
  @DisplayName("유효하지 않은 필드가 입력되었을 경우 오류를 발생시킨다")
  void test_join_case1() throws Exception {
    //given
    String name = "testNew";
    String email = "testNew@example.com";
    String password = "wrongPassword";
    String phoneNumber = "01012341234";
    LocalDate birthDate = LocalDate.of(2000, 1, 1);
    String gender = "MALE";
    String address = "서울특별시 양천구 목동";
    String addressDetail = "강서고등학교";

    AccountCreateRequestDto request = AccountCreateRequestDto.builder()
        .name(name)
        .email(email)
        .password(password)
        .phoneNumber(phoneNumber)
        .birthDate(birthDate)
        .gender(gender)
        .address(address)
        .addressDetail(addressDetail)
        .build();
    String json = MapperUtils.getMapper().writeValueAsString(request);

    //when
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/join")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(ValidationMessage.PASSWORD_PATTERN)));

  }

  @Test
  @DisplayName("필수 입력값들만 입력해도 회원가입된다")
  void test_join_case2() throws Exception {
    //given
    String name = "testNew";
    String email = "testNew@example.com";
    String password = "testPassword@1";

    AccountCreateRequestDto request = AccountCreateRequestDto.builder()
        .name(name)
        .email(email)
        .password(password)
        .build();
    String json = MapperUtils.getMapper().writeValueAsString(request);

    //when
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/join")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(name)))
        .andExpect(content().string(containsString(email)));
  }


  @Test
  @DisplayName("모든 데이터가 정상적으로 입력되면 회원가입한다")
  void test_join_case3() throws Exception {
    //given
    String name = "testNew";
    String email = "testNew@example.com";
    String password = "testPassword@1";
    String phoneNumber = "01012341234";
    LocalDate birthDate = LocalDate.of(2000, 1, 1);
    String gender = "MALE";
    String address = "서울특별시 양천구 목동";
    String addressDetail = "강서고등학교";

    AccountCreateRequestDto request = AccountCreateRequestDto.builder()
        .name(name)
        .email(email)
        .password(password)
        .phoneNumber(phoneNumber)
        .birthDate(birthDate)
        .gender(gender)
        .address(address)
        .addressDetail(addressDetail)
        .build();
    String json = MapperUtils.getMapper().writeValueAsString(request);

    //when
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/join")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(name)))
        .andExpect(content().string(containsString(email)))
        .andDo(print())
        .andDo(document("join",
                getDocumentRequest(),
                getDocumentResponse(),
                resource(ResourceSnippetParameters.builder()
                    .tag("회원 API")
                    .summary("회원가입 API")
                    .requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일(아이디): " + ValidationMessage.EMAIL_FORMAT),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름: " + ValidationMessage.NAME_PATTERN),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호: " + ValidationMessage.PASSWORD_FORMAT),
                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호: " + ValidationMessage.PHONE_NUMBER_PATTERN),
                        fieldWithPath("birthDate").type(JsonFieldType.STRING).description("생년월일: " + ValidationMessage.BIRTH_DATE_FORMAT),
                        fieldWithPath("gender").type(JsonFieldType.STRING).description("성별: " + ValidationMessage.GENDER_FORMAT),
                        fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                        fieldWithPath("addressDetail").type(JsonFieldType.STRING).description("상세주소")
                    )
                    .responseFields(
                        new FieldDescriptors(successResponseFields).and(
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("회원번호"),
                            fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일(아이디)"),
                            fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                            fieldWithPath("data.userRoles").type(JsonFieldType.STRING).description("권한목록")
                        ).getFieldDescriptors()
                    )
                    .requestSchema(Schema.schema("AccountCreateRequestDto"))
                    .responseSchema(Schema.schema("CommonApiResponse.join"))
                    .build()
                )
            )
        );
  }

  @Test
  @DisplayName("사용자는 인증 이메일을 발송할 수 있다")
  void test_send_case1() throws Exception {
    //given
    String json = MapperUtils.getMapper()
        .writeValueAsString(Map.of("email", "test@gmail.com"));

    //when
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/email")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("sendEmail",
                getDocumentRequest(),
                getDocumentResponse(),
                resource(ResourceSnippetParameters.builder()
                    .tag("회원 API")
                    .summary("인증 이메일 발송 API")
                    .requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일(아이디): " + ValidationMessage.EMAIL_FORMAT)
                    )
                    .responseFields(
                        new FieldDescriptors(successResponseFields)
                    )
                    .requestSchema(Schema.schema("AccountSendRequestDto"))
                    .responseSchema(Schema.schema("CommonApiResponse.sendEmail"))
                    .build()
                )
            )
        );

    greenMail.waitForIncomingEmail(1);
    MimeMessage result = greenMail.getReceivedMessages()[0];
    assertThat(result.getSubject()).isEqualTo("[Petmeeting] 회원가입을 위한 이메일 인증코드 입니다");
    assertThat(result.getRecipients(RecipientType.TO)[0].toString()).isEqualTo("test@gmail.com");

  }

  @Test
  @DisplayName("잘못된 인증번호를 받으면 false를 반환한다")
  void test_verify_case1() throws Exception {
    //given
    testDataInitializer.initEmailTestData();
    String json = MapperUtils.getMapper()
        .writeValueAsString(Map.of("email", "test1@gmail.com", "code", "wrongCo"));

    //when
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/email/verification")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("false")));

  }

  @Test
  @DisplayName("만료된 인증번호를 받으면 expired 를 반환한다")
  void test_verify_case2() throws Exception {
    //given
    testDataInitializer.initEmailTestData();
    String json = MapperUtils.getMapper()
        .writeValueAsString(Map.of("email", "test2@gmail.com", "code", "rightCo"));

    //when
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/email/verification")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("expired")));

  }

  @Test
  @DisplayName("정상적인된 인증번호를 받으면 true 를 반환한다")
  void test_verify_case3() throws Exception {
    //given
    testDataInitializer.initEmailTestData();
    String json = MapperUtils.getMapper()
        .writeValueAsString(Map.of("email", "test1@gmail.com", "code", "rightCo"));

    //when
    //then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/email/verification")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("true")))
        .andDo(print())
        .andDo(document("verify Email",
                getDocumentRequest(),
                getDocumentResponse(),
                resource(ResourceSnippetParameters.builder()
                    .tag("회원 API")
                    .summary("인증 이메일 검증 API")
                    .requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일(아이디): " + ValidationMessage.EMAIL_FORMAT),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("인증번호")
                    )
                    .responseFields(
                        new FieldDescriptors(successResponseFields).and(
                            fieldWithPath("data").type(JsonFieldType.STRING).description("검증결과 : 'true', 'false', 'expired'")
                        )
                    )
                    .requestSchema(Schema.schema("AccountVerifyRequestDto"))
                    .responseSchema(Schema.schema("CommonApiResponse.verifyEmail"))
                    .build()
                )
            )
        );

  }


}
