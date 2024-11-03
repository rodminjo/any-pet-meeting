package com.backend.accountmanagement.account.service.port;

import com.backend.accountmanagement.account.domain.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository {

  public RefreshToken getByEmail(String email);

  public Optional<RefreshToken> findByEmail(String email);

  public Optional<RefreshToken> findByRefreshToken(String refreshToken);

  public RefreshToken save(RefreshToken refreshToken);

}
