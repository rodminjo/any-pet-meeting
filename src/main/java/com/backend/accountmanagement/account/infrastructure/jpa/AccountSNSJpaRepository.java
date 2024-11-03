package com.backend.accountmanagement.account.infrastructure.jpa;

import com.backend.accountmanagement.account.domain.SNSProvider;
import com.backend.accountmanagement.account.infrastructure.entity.AccountSNSEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSNSJpaRepository extends JpaRepository<AccountSNSEntity, Long> {

  Optional<AccountSNSEntity> findByEmailAndProvider(String email, SNSProvider provider);

}
