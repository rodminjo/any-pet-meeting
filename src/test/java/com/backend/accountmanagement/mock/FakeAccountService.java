package com.backend.accountmanagement.mock;

import com.backend.accountmanagement.account.controller.port.AccountService;
import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.AccountCreate;
import com.backend.accountmanagement.account.domain.AccountVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class FakeAccountService implements AccountService {

  private final PasswordEncoder passwordEncoder;
  private final String verificationCode;

  @Override
  public Account doLogin(String email, String password) {
    return Account.builder()
        .email(email)
        .password(passwordEncoder.encode(password))
        .build();

  }

  @Override
  public Account doJoin(AccountCreate accountCreate) {
    return accountCreate.of(passwordEncoder);

  }

  @Override
  public String sendVerfiedEmail(String email) {
    return "prefix: "+email;
  }

  @Override
  public String verifiedEmail(AccountVerification accountVerification) {
    return String.valueOf(accountVerification.getVerificationCode().equals(verificationCode));

  }
}
