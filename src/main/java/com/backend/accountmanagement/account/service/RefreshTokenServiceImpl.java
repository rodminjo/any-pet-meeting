package com.backend.accountmanagement.account.service;

import com.backend.accountmanagement.account.controller.port.RefreshTokenService;
import com.backend.accountmanagement.account.domain.RefreshToken;
import com.backend.accountmanagement.account.service.port.RefreshTokenRepository;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  @Transactional(readOnly = true)
  public RefreshToken checkRefreshToken(String email, String refreshToken) {
    RefreshToken findRefreshToken = refreshTokenRepository.findByEmail(email)
        .filter(token-> token.getRefreshToken().equals(refreshToken))
        .orElseThrow(() -> new BadCredentialsException(ExceptionMessage.REFRESH_TOKEN_NOT_VAILD));

    return findRefreshToken;
  }


  @Override
  @Transactional
  public void merge(String email, String refreshToken) {
    RefreshToken findToken = refreshTokenRepository.findByEmail(email).orElse(null);

    if (ObjectUtils.isEmpty(findToken)) {
      findToken = RefreshToken.builder()
          .email(email)
          .refreshToken(refreshToken)
          .build();

    } else {
      findToken.update(refreshToken);

    }

    refreshTokenRepository.save(findToken);
  }
}
