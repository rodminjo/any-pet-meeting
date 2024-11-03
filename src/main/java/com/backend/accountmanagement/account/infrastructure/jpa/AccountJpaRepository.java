package com.backend.accountmanagement.account.infrastructure.jpa;

import com.backend.accountmanagement.account.infrastructure.entity.AccountEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {

  Optional<AccountEntity> findByEmail(String email);

  Optional<AccountEntity> findByEmailAndPassword(String email, String password);

  boolean existsByEmail(String email);

}
