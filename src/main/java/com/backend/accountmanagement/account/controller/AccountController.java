package com.backend.accountmanagement.account.controller;

import com.backend.accountmanagement.account.controller.dto.AccountRequestDto;
import com.backend.accountmanagement.account.controller.dto.AccountRequestDto.AccountCreateRequestDto;
import com.backend.accountmanagement.account.controller.dto.AccountRequestDto.AccountVerifyRequestDto;
import com.backend.accountmanagement.account.controller.dto.AccountResponseDto.AccountBaseResponseDto;
import com.backend.accountmanagement.account.controller.port.AccountService;
import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.common.controller.response.CommonApiResponse;
import com.backend.accountmanagement.common.controller.response.CommonResponseMessage;
import com.backend.accountmanagement.web.configs.properties.SecurityProperties;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Validated
@RequiredArgsConstructor
@Controller
public class AccountController {

  private final AccountService accountService;
  private final SecurityProperties securityProperties;

  @GetMapping("/")
  public String loginPage(){
    return "/view/index";

  }

  @PostMapping("/api/join")
  @ResponseBody
  public ResponseEntity<CommonApiResponse<AccountBaseResponseDto>> join(@Valid @RequestBody AccountCreateRequestDto accountCreateRequestDto){
    Account savedAccount = accountService.doJoin(accountCreateRequestDto.ofCreate());
    AccountBaseResponseDto result = AccountBaseResponseDto.from(savedAccount);

    return CommonApiResponse.createResponse(HttpStatus.OK, CommonResponseMessage.SUCCESS, result);
    
  }

  @PostMapping("/api/email")
  @ResponseBody
  public ResponseEntity<CommonApiResponse<String>> send(@Valid @RequestBody AccountRequestDto.AccountSendRequestDto dto){
    accountService.sendVerfiedEmail(dto.getEmail());

    return CommonApiResponse.createResponse(HttpStatus.OK, CommonResponseMessage.SUCCESS, "success");

  }

  @PostMapping("/api/email/verification")
  @ResponseBody
  public ResponseEntity<CommonApiResponse<String>> verify(@Valid @RequestBody AccountVerifyRequestDto dto){
    String result = accountService.verifiedEmail(dto.ofVerification());

    return CommonApiResponse.createResponse(HttpStatus.OK, CommonResponseMessage.SUCCESS, result);

  }

  @PostMapping("/api/health-check")
  @ResponseBody
  public ResponseEntity<CommonApiResponse<SecurityProperties>> healthCheck(){
    return CommonApiResponse.createResponse(HttpStatus.OK, CommonResponseMessage.SUCCESS, securityProperties);

  }



}
