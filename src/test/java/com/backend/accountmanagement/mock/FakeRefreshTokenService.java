package com.backend.accountmanagement.mock;

import com.backend.accountmanagement.account.controller.port.RefreshTokenService;
import com.backend.accountmanagement.account.domain.RefreshToken;

public class FakeRefreshTokenService implements RefreshTokenService {

  public int mergeCount = 0;
  @Override
  public RefreshToken checkRefreshToken(String email, String refreshToken) {
    return RefreshToken.builder()
        .id(0L)
        .email(email)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public void merge(String email, String refreshToken) {
    mergeCount++;
  }
}
