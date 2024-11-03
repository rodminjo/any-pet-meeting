package com.backend.accountmanagement.account.infrastructure;

import com.backend.accountmanagement.account.domain.RefreshToken;
import com.backend.accountmanagement.account.infrastructure.entity.RefreshTokenEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.RefreshTokenJpaRepository;
import com.backend.accountmanagement.account.service.port.RefreshTokenRepository;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.common.exception.ResourceNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private final RefreshTokenJpaRepository refreshTokenJpaRepository;

  @Override
  public RefreshToken getByEmail(String email) {
    return findByEmail(email)
        .orElseThrow(()-> new ResourceNotFoundException(ExceptionMessage.REFRESH_TOKEN_NOT_FOUND));
  }

  @Override
  public Optional<RefreshToken> findByEmail(String email) {
    return refreshTokenJpaRepository.findByEmail(email)
        .map(RefreshTokenEntity::toDomain);
  }

  @Override
  public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
    return refreshTokenJpaRepository.findByRefreshToken(refreshToken)
        .map(RefreshTokenEntity::toDomain);
  }

  @Override
  public RefreshToken save(RefreshToken refreshToken) {
    RefreshTokenEntity savedEntity = refreshTokenJpaRepository.save(RefreshTokenEntity.from(refreshToken));
    return savedEntity.toDomain();
  }
}
