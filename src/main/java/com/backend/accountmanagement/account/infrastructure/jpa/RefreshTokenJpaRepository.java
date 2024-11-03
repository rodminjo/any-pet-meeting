package com.backend.accountmanagement.account.infrastructure.jpa;

import com.backend.accountmanagement.account.infrastructure.entity.RefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

  Optional<RefreshTokenEntity> findByEmail(String email);

  Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);


}
