package com.backend.accountmanagement.account.controller.port;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public interface EmailService {
  MimeMessage creatMessage(String to, String code) throws MessagingException, UnsupportedEncodingException;

  String sendSimpleMessage(String to);

}
