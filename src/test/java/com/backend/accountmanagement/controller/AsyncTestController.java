package com.backend.accountmanagement.controller;

import com.backend.accountmanagement.service.AsyncTestService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AsyncTestController {

  private final AsyncTestService asyncTestService;


  @GetMapping("/test/async/")
  public String async() throws ExecutionException, InterruptedException, TimeoutException {
    return asyncTestService.asyncTestLogic().get(10, TimeUnit.SECONDS);

  }

}
