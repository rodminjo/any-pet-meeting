package com.backend.accountmanagement.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backend.accountmanagement.account.controller.port.EmailService;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.mock.utils.MockCodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith({MockitoExtension.class})
class EmailServiceTest {
  private EmailService emailService;

  @Mock
  private JavaMailSender javaMailSender;
  @Mock
  private MimeMessage mineMessage;

  @BeforeEach
  void setup(){
    emailService = new EmailServiceImpl(javaMailSender, new MockCodeGenerator("1q2w3e"));
    when(javaMailSender.createMimeMessage()).thenReturn(mineMessage);
  }

  @Test
  @DisplayName("사용자에게 보낼 메일을 생성한다")
  void test_createMessage_case1() throws MessagingException, IOException {
    // given
    String to = "testerEmail@gmail.com";
    String code = "1q2w3e";

    // when
    MimeMessage result = emailService.creatMessage(to, code);

    // then
    assertThat(result).isNotNull();
    verify(result).addRecipients(MimeMessage.RecipientType.TO, to);
    verify(result).setSubject("[Petmeeting] 회원가입을 위한 이메일 인증코드 입니다");
    verify(result).setText(contains(code), eq("utf-8"), eq("html"));
    verify(result).setFrom(new InternetAddress("Petmeeting@gmail.com", "Petmeeting_Admin"));
  }

  @Test
  @DisplayName("메일 생성중 오류가 발생하면 예외를 반환한다")
  void test_sendSimpleMessage_case1() throws Exception {
    // given
    String to = "testerEmail@gmail.com";
    doThrow(new MessagingException()).when(mineMessage).setSubject(anyString());

    // when, then
    assertThatThrownBy(() -> emailService.sendSimpleMessage(to))
        .isExactlyInstanceOf(IllegalStateException.class)
        .hasMessage(ExceptionMessage.MAIL_WRITE_FAILED);
  }

  @Test
  @DisplayName("메일을 발송중 오류가 발생하면 예외를 반환한다")
  void test_sendSimpleMessage_case2() throws Exception {
    // given
    String to = "testerEmail@gmail.com";
    doThrow(new MailSendException("")).when(javaMailSender).send(mineMessage);

    // when, then
    assertThatThrownBy(() -> emailService.sendSimpleMessage(to))
        .isExactlyInstanceOf(IllegalStateException.class)
        .hasMessage(ExceptionMessage.MAIL_SEND_FAILED);
  }

  @Test
  @DisplayName("메일을 발송하고 인증코드를 반환한다")
  void test_sendSimpleMessage_case3() throws Exception {
    // given
    String to = "testerEmail@gmail.com";

    // when
    String code = emailService.sendSimpleMessage(to);

    //then
    assertThat(code).isEqualTo("1q2w3e");
  }

}