package com.backend.accountmanagement.account.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.accountmanagement.account.controller.dto.AccountRequestDto.AccountCreateRequestDto;
import com.backend.accountmanagement.account.controller.dto.AccountRequestDto.AccountSendRequestDto;
import com.backend.accountmanagement.account.controller.dto.AccountRequestDto.AccountVerifyRequestDto;
import com.backend.accountmanagement.account.controller.dto.AccountResponseDto.AccountBaseResponseDto;
import com.backend.accountmanagement.common.controller.response.CommonApiResponse;
import com.backend.accountmanagement.common.controller.response.CommonResponseMessage;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import java.time.LocalDate;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AccountControllerTest {

  private AccountController accountController;

  @BeforeEach
  void setUp() {
    SecurityTestContainer testContainer = new SecurityTestContainer(new MockClockHolder(new Date()), 1L);
    accountController = new AccountController(testContainer.accountService, testContainer.securityProperties);
  }


  @Test
  @DisplayName("사용자는_회원가입을_할_수_있다")
  void test_join_case1(){
    // given
    SecurityTestContainer testContainer = new SecurityTestContainer(new MockClockHolder(new Date()), 1L);
    String name = "test";
    String email = "test@example.com";
    String password = "testPassword";
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

    // when
    ResponseEntity<CommonApiResponse<AccountBaseResponseDto>> response = accountController.join(request);

    //then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData().getName()).isEqualTo(testContainer.name);
    assertThat(response.getBody().getData().getEmail()).isEqualTo(testContainer.email);
    assertThat(response.getBody().getData().getUserRoles()).isEqualTo(testContainer.authorities.get(0).getAuthority());

  }

  @Test
  @DisplayName("사용자는_이메일을_발송할_수_있다")
  void test_send_case1(){
    // given
    String email = "test@gmail.com";
    AccountSendRequestDto dto = AccountSendRequestDto.builder().email(email).build();

    // when
    ResponseEntity<CommonApiResponse<String>> response = accountController.send(dto);

    //then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getMessage()).isEqualTo(CommonResponseMessage.SUCCESS);
  }

  @Test
  @DisplayName("사용자는 이메일을 검증할 수있다")
  void test_verify_case1(){
    // given
    String email = "test@gmail.com";
    String rightCode = "1q2w3e";
    String wrongCode = "123456";

    AccountVerifyRequestDto rightDto = AccountVerifyRequestDto.builder()
        .email(email)
        .code(rightCode)
        .build();

    AccountVerifyRequestDto wrongDto = AccountVerifyRequestDto.builder()
        .email(email)
        .code(wrongCode)
        .build();


    // when
    ResponseEntity<CommonApiResponse<String>> rightResponse = accountController.verify(rightDto);
    ResponseEntity<CommonApiResponse<String>> wrongResponse = accountController.verify(wrongDto);

    //then
    assertThat(rightResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(rightResponse.getBody()).isNotNull();
    assertThat(rightResponse.getBody().getMessage()).isEqualTo(CommonResponseMessage.SUCCESS);
    assertThat(rightResponse.getBody().getData()).isEqualTo("true");

    assertThat(wrongResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(wrongResponse.getBody()).isNotNull();
    assertThat(wrongResponse.getBody().getMessage()).isEqualTo(CommonResponseMessage.SUCCESS);
    assertThat(wrongResponse.getBody().getData()).isEqualTo("false");
  }

}