package com.backend.accountmanagement.account.service;

import com.backend.accountmanagement.account.controller.port.AccountService;
import com.backend.accountmanagement.account.controller.port.EmailService;
import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.AccountCreate;
import com.backend.accountmanagement.account.domain.AccountVerification;
import com.backend.accountmanagement.account.service.port.AccountRepository;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.redis.service.port.RedisRepository;
import com.backend.accountmanagement.web.configs.properties.MailProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
  public static final String VERIFIED_EMAIL_PREFIX = "VERIFIED_EMAIL:";

  private final MailProperties mailProperties;
  private final PasswordEncoder passwordEncoder;

  private final EmailService emailService;
  private final AccountRepository accountRepository;
  private final RedisRepository redisRepository;



  @Override
  public Account doLogin(String email, String password) {
    Account findAccount = accountRepository.findByEmail(email)
        .filter(account -> passwordEncoder.matches(password, account.getPassword()))
        .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.LOGIN_FAILED));

    return findAccount;
  }

  @Transactional
  @Override
  public Account doJoin(AccountCreate accountCreate) {
    Account joinAccount = accountCreate.of(passwordEncoder);

    if (accountRepository.existByEmail(joinAccount.getEmail())){
      throw new IllegalStateException(ExceptionMessage.INVALID_EMAIL);
    }

    Account savedAccount = accountRepository.save(joinAccount);
    return savedAccount;

  }

  @Override
  public String sendVerfiedEmail(String email) {
    String certificationCode = emailService.sendSimpleMessage(email);
    AccountVerification verification = AccountVerification
        .createVerification(email, certificationCode, mailProperties.getAuthCodeExpirationMin());

    try {
      String redisKey = VERIFIED_EMAIL_PREFIX + verification.getEmail();
      long durationMillis = mailProperties.getAuthCodeExpirationMin() * 60 * 1000L;
      redisRepository.setDataWithExpired(redisKey, verification, durationMillis);

      return redisKey;

    } catch (JsonProcessingException e){
      log.error("redis 에러 : {}", e.getMessage());
      throw new IllegalStateException(ExceptionMessage.REDIS_SAVE_FAILED);

    }
  }

  @Override
  public String verifiedEmail(AccountVerification accountVerification) {
    String redisKey = VERIFIED_EMAIL_PREFIX + accountVerification.getEmail();
    String code = accountVerification.getVerificationCode();
    try {
      AccountVerification data = redisRepository.getData(redisKey, AccountVerification.class);
      if (data == null || data.expired()) {
        return "expired";

      }

      return data.verify(code) ? "true" : "false";

    } catch (JsonProcessingException e){
      return "false";

    }
  }
}
