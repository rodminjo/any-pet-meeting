package com.backend.accountmanagement.account.controller.port;

import com.backend.accountmanagement.account.domain.RefreshToken;

public interface RefreshTokenService {

  RefreshToken checkRefreshToken(String email, String refreshToken);

  void merge(String email, String refreshToken);
}
