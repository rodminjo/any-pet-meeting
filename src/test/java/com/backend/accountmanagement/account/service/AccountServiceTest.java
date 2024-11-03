package com.backend.accountmanagement.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.accountmanagement.account.controller.port.AccountService;
import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.AccountCreate;
import com.backend.accountmanagement.account.domain.AccountVerification;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.redis.service.port.RedisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountServiceTest {

  private final int authCodeExpirationMin = 5;
  private AccountService accountService;
  private RedisRepository redisRepository;

  @BeforeEach
  void setUp() {
    SecurityTestContainer testContainer =
        new SecurityTestContainer(new MockClockHolder(new Date()), 0L);
    accountService = new AccountServiceImpl(
        testContainer.mailProperties,
        testContainer.passwordEncoder,
        testContainer.emailService,
        testContainer.accountRepository,
        testContainer.redisRepository
    );

    testContainer.accountRepository.save(testContainer.account);
    this.redisRepository = testContainer.redisRepository;
  }

  @Test
  @DisplayName("로그인중_정보와_일치하는_회원이_없다면_오류를_낸다")
  void test_doLogin_case1(){
    // given
    String wrongEmail = "invalid_email";
    String password = "testPassword";

    // when, then
    assertThatThrownBy(() -> accountService.doLogin(wrongEmail, password))
        .isInstanceOf(RuntimeException.class)
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage(ExceptionMessage.LOGIN_FAILED);
  }

  @Test
  @DisplayName("로그인중_아이디는_일치하지만_비밀번호가_다르다면_오류를_낸다")
  void test_doLogin_case2(){
    // given
    String email = "test@example.com";
    String wrongPassword = "wrongPassword";

    // when, then
    assertThatThrownBy(() -> accountService.doLogin(email, wrongPassword))
        .isInstanceOf(RuntimeException.class)
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage(ExceptionMessage.LOGIN_FAILED);
  }

  @Test
  @DisplayName("아이디와_비밀번호가_유효하다면_계정을_반환한다")
  void test_doLogin_case3(){
    // given
    String name = "test";
    String email = "test@example.com";
    String password = "testPassword";
    String roleName = "ROLE_USER";
    String roleDesc = "DEFAULT";

    // when
    Account account = accountService.doLogin(email, password);
    Role role = account.getRoleSet().stream().findFirst().orElse(null);

    // then
    assertThat(account.getName()).isEqualTo(name);
    assertThat(account.getEmail()).isEqualTo(email);
    assertThat(role).isNotNull();
    assertThat(role.getRoleName()).isEqualTo(roleName);
    assertThat(role.getRoleDesc()).isEqualTo(roleDesc);
  }


  @Test
  @DisplayName("AccountCreate로_회원가입을_할때_같은_이메일이_존재한다면_예외를_낸다")
  void test_doJoin_case1(){
    // given
    String name = "test";
    String email = "test@example.com";
    String password = "testPassword";
    String phoneNumber = "01012341234";
    LocalDate birthDate = LocalDate.of(2000, 1, 1);
    String gender = "MALE";
    String address = "서울특별시 양천구 목동";
    String addressDetail = "강서고등학교";

    AccountCreate create = AccountCreate.builder()
        .name(name)
        .email(email)
        .password(password)
        .phoneNumber(phoneNumber)
        .birthDate(birthDate)
        .gender(gender)
        .address(address)
        .addressDetail(addressDetail)
        .build();

    // when, then
    assertThatThrownBy(() -> accountService.doJoin(create))
        .isInstanceOf(RuntimeException.class)
        .isExactlyInstanceOf(IllegalStateException.class)
        .hasMessage(ExceptionMessage.INVALID_EMAIL);
  }

  @Test
  @DisplayName("AccountCreateRequestDto로_회원가입을_할_수_있다")
  void test_doJoin_case2(){
    // given
    String name = "testNew";
    String email = "testNew@example.com";
    String password = "testPassword";
    String phoneNumber = "01012341234";
    LocalDate birthDate = LocalDate.of(2000, 1, 1);
    String gender = "MALE";
    String address = "서울특별시 양천구 목동";
    String addressDetail = "강서고등학교";
    String roleName = "ROLE_USER";
    String roleDesc = "DEFAULT";

    AccountCreate create = AccountCreate.builder()
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
    Account account = accountService.doJoin(create);
    Role role = account.getRoleSet().stream().findFirst().orElse(null);

    // then
    assertThat(account.getName()).isEqualTo(name);
    assertThat(account.getEmail()).isEqualTo(email);
    assertThat(role).isNotNull();
    assertThat(role.getRoleName()).isEqualTo(roleName);
    assertThat(role.getRoleDesc()).isEqualTo(roleDesc);
  }

  @Test
  @DisplayName("이메일을 전송하고 인증코드를 저장한다")
  void test_sendEmail_case1() throws JsonProcessingException {
    // given
    String testEmail = "test@gmail.com";

    // when
    String redisKey = accountService.sendVerfiedEmail(testEmail);

    // then
    AccountVerification saved = redisRepository.getData(redisKey, AccountVerification.class);
    assertThat(redisKey).contains(testEmail);
    assertThat(saved).isNotNull();
    assertThat(saved.getEmail()).isEqualTo(testEmail);
    assertThat(saved.verify("1q2w3e")).isTrue();

  }

  @Test
  @DisplayName("이메일을 가져와서 검증하고 틀리면 Failed를 반환한다")
  void test_verifiedEmail_case1() throws JsonProcessingException {
    // given
    String testEmail = "test@gmail.com";
    String rightCode = "1q2w3e";
    String wrongCode = "123456";
    AccountVerification verify = AccountVerification.createVerification(testEmail, rightCode, 5);
    AccountVerification wrongVerify = AccountVerification.createVerification(testEmail, wrongCode, 0);
    String key = AccountServiceImpl.VERIFIED_EMAIL_PREFIX + testEmail;
    redisRepository.setDataWithExpired(key, verify, 5);

    // when
    String result = accountService.verifiedEmail(wrongVerify);

    // then
    assertThat(result).isEqualTo("false");

  }

  @Test
  @DisplayName("이메일을 가져와서 유효기간이 지나면 expired를 반환한다")
  void test_verifiedEmail_case2() throws JsonProcessingException {
    // given
    String testEmail = "test@gmail.com";
    String rightCode = "1q2w3e";
    String wrongCode = "123456";
    AccountVerification verify = AccountVerification.createVerification(testEmail, rightCode, 0);
    AccountVerification wrongVerify = AccountVerification.createVerification(testEmail, wrongCode, 0);
    String key = AccountServiceImpl.VERIFIED_EMAIL_PREFIX + testEmail;
    redisRepository.setDataWithExpired(key, verify, 0);

    // when
    String result = accountService.verifiedEmail(wrongVerify);

    // then
    assertThat(result).isEqualTo("expired");

  }

  @Test
  @DisplayName("이메일을 가져와서 유효하다면 true를 반환한다")
  void test_verifiedEmail_case3() throws JsonProcessingException {
    // given
    String testEmail = "test@gmail.com";
    String rightCode = "1q2w3e";
    AccountVerification verify = AccountVerification.createVerification(testEmail, rightCode, authCodeExpirationMin);
    String key = AccountServiceImpl.VERIFIED_EMAIL_PREFIX + testEmail;
    redisRepository.setDataWithExpired(key, verify, authCodeExpirationMin * 1000);

    // when
    String result = accountService.verifiedEmail(verify);

    // then
    assertThat(result).isEqualTo("true");

  }



}