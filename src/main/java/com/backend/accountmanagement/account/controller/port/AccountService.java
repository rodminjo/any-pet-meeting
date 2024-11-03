package com.backend.accountmanagement.account.controller.port;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.AccountCreate;
import com.backend.accountmanagement.account.domain.AccountVerification;

public interface AccountService {

  public Account doLogin(String email, String password);

  public Account doJoin(AccountCreate accountCreate);

  String sendVerfiedEmail(String email);

  String verifiedEmail(AccountVerification accountVerification);
}
