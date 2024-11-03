package com.backend.accountmanagement.account.domain;

import com.backend.accountmanagement.common.domain.BaseDomain;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountVerification extends BaseDomain {

  private String email;
  private String verificationCode;
  private LocalDateTime expirationDate;


  @Builder
  public AccountVerification(String email, String verificationCode, LocalDateTime expirationDate) {
    this.email = email;
    this.verificationCode = verificationCode;
    this.expirationDate = expirationDate;
  }

  public static AccountVerification createVerification(String email, String verificationCode, int expirationInMinutes) {
    LocalDateTime now = LocalDateTime.now();
    return AccountVerification.builder()
        .email(email)
        .verificationCode(verificationCode)
        .expirationDate(now.plusMinutes(expirationInMinutes))
        .build();
  }

  public boolean verify(String code) {
    return this.verificationCode.equals(code);

  }

  public boolean expired(){
    return LocalDateTime.now().isAfter(this.expirationDate);

  }
}
