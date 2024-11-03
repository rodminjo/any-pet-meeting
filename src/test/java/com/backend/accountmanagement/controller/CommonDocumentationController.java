package com.backend.accountmanagement.controller;

import com.backend.accountmanagement.common.controller.response.CommonApiResponse;
import com.backend.accountmanagement.common.controller.response.CommonApiResponse.ErrorResponse;
import com.backend.accountmanagement.common.controller.response.CommonResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommonDocumentationController {

  @GetMapping("/docs")
  public ResponseEntity<CommonApiResponse<String>> commonField(){
    return CommonApiResponse.createResponse(HttpStatus.OK, CommonResponseMessage.SUCCESS, "data");

  }

  @GetMapping("/docs/error")
  public ResponseEntity<CommonApiResponse<ErrorResponse>> commonErrorField(){
    return CommonApiResponse.createErrorResponse(HttpStatus.OK, "40101", "error description");

  }
}
