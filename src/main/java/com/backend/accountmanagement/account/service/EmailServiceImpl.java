package com.backend.accountmanagement.account.service;

import com.backend.accountmanagement.account.controller.port.EmailService;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.common.service.port.CodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender javaMailSender;
  private final CodeGenerator codeGenerator;



  @Override
  public MimeMessage creatMessage(String to, String certificationCode) throws MessagingException, UnsupportedEncodingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    message.addRecipients(RecipientType.TO, to); // 메일 받을 사용자
    message.setSubject("[Petmeeting] 회원가입을 위한 이메일 인증코드 입니다"); // 이메일 제목
    String msgg = "";
    // msgg += "<img src=../resources/static/image/emailheader.jpg />"; // header image
    msgg += "<h1>안녕하세요</h1>";
    msgg += "<h1>강아지 소셜 플랫폼 Petmeeting 입니다</h1>";
    msgg += "<br>";
    msgg += "<p>아래 인증코드를 이메일 인증 페이지에 입력해주세요</p>";
    msgg += "<br>";
    msgg += "<br>";
    msgg += "<div align='center' style='border:1px solid black'>";
    msgg += "<h3 style='color:blue'>인증코드 입니다</h3>";
    msgg += "<div style='font-size:130%'>";
    msgg += "<strong>" + certificationCode + "</strong></div><br/>" ; // 메일에 인증번호 넣기
    msgg += "</div>";
    // msgg += "<img src=../resources/static/image/emailfooter.jpg />"; // footer image

    // 메일 내용, charset타입, subtype
    message.setText(msgg, "utf-8", "html");

    // 보내는 사람의 이메일 주소, 보내는 사람 이름
    message.setFrom(new InternetAddress("Petmeeting@gmail.com", "Petmeeting_Admin"));

    return message;
  }


  @Override
  public String sendSimpleMessage(String to) {
    String certificationCode = codeGenerator.generateCode();

    try{
      MimeMessage mimeMessage = creatMessage(to, certificationCode);
      javaMailSender.send(mimeMessage);

    } catch (MessagingException | UnsupportedEncodingException e){
      log.error("email 작성 오류 : {}", e.getMessage());
      throw new IllegalStateException(ExceptionMessage.MAIL_WRITE_FAILED);

    } catch (MailException e) {
      log.error("email 발송 오류 : {}", e.getMessage());
      throw new IllegalStateException(ExceptionMessage.MAIL_SEND_FAILED);

    }

    return certificationCode;

  }
}
