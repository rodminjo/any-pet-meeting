package com.backend.accountmanagement.medium;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.backend.accountmanagement.acceptance.AcceptanceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;


public class SecurityContextAsyncTest extends AcceptanceTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void 메인쓰레드가_아닌_자식쓰레드에서도_계정정보를_불러올_수_있다() throws Exception {
    mockMvc.perform(get("/test/async/"))
        .andExpect(content().string("Success"))
        .andDo(print());

  }

}
