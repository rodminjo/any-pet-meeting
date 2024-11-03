package com.backend.accountmanagement.acceptance;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import com.backend.accountmanagement.config.DatabaseClearExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({DatabaseClearExtension.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@SpringBootTest
public class AcceptanceTest {

  @Autowired
  public MockMvc mockMvc;

  // 요청 성공시 공통 응답 Spec 부분 추출
  public FieldDescriptor[] successResponseFields = {
      subsectionWithPath("data").description("데이터"),
      fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
  };

  // 요청 데이터 검증 실패시 공통 응답 Spec 부분 추출
  public FieldDescriptor[] failResponseFields = {
      fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
      subsectionWithPath("data").type(JsonFieldType.OBJECT).description("오류 필드"),
      fieldWithPath("data.errorId").type(JsonFieldType.STRING).description("에러ID"),
      fieldWithPath("data.errorDescription").type(JsonFieldType.STRING).description("에러 설명")
  };

}
