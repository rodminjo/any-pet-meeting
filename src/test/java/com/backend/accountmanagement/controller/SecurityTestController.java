package com.backend.accountmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SecurityTestController {


  @GetMapping("/test/auth/")
  public String checkAuth() {
    return "success";

  }

  @GetMapping("/test/all/")
  public String permitAll() {
    return "success";

  }

  @GetMapping("/test/auth/user")
  public String checkAuthUser() {
    return "user";

  }

  @GetMapping("/test/auth/user/low")
  public String checkAuthUserLow() {
    return "userLow";

  }

  @GetMapping("/test/auth/anonymous")
  public String checkAuthAnonymous() {
    return "anonymous";

  }


}
