package com.backend.accountmanagement.mock;

import com.backend.accountmanagement.account.controller.port.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FakeEmailService implements EmailService {

  private final String certificateCode;

  @Override
  public MimeMessage creatMessage(String to, String code)
      throws MessagingException, UnsupportedEncodingException {
    return null;

  }

  @Override
  public String sendSimpleMessage(String to) {
    return certificateCode;

  }
}
