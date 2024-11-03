package com.backend.accountmanagement.medium.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.accountmanagement.acceptance.AcceptanceTest;
import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;


@AutoConfigureRestDocs
public class CommonDocumentationTest extends AcceptanceTest {
  @Test
  public void commons() throws Exception {

    this.mockMvc.perform(
            RestDocumentationRequestBuilders.get("/docs")
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(document("common-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("공통 포멧")
                        .summary("공통 성공 포멧")
                        .responseFields(
                            new FieldDescriptors(successResponseFields).getFieldDescriptors()
                        )
                        .responseSchema(Schema.schema("CommonApiResponse"))
                        .build()
                )
            )
        );

  }

  @Test
  public void errors() throws Exception {

    this.mockMvc.perform(
            RestDocumentationRequestBuilders.get("/docs/error")
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(document("common-error",
            resource(
                ResourceSnippetParameters.builder()
                    .tag("공통 포멧")
                    .summary("공통 에러발생 포멧")
                    .responseFields(
                        new FieldDescriptors(failResponseFields).getFieldDescriptors()
                    )
                    .responseSchema(Schema.schema("CommonApiResponse"))
                    .build()
            )
            )
        );

  }

}
