package com.backend.accountmanagement.account.infrastructure;

import com.backend.accountmanagement.account.domain.AccountSNS;
import com.backend.accountmanagement.account.domain.SNSProvider;
import com.backend.accountmanagement.account.infrastructure.entity.AccountSNSEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.AccountSNSJpaRepository;
import com.backend.accountmanagement.account.service.port.AccountSNSRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AccountSNSRepositoryImpl implements AccountSNSRepository {

  private final AccountSNSJpaRepository accountSNSJpaRepository;
  @Override
  public Optional<AccountSNS> findByEmailAndProvider(String email, SNSProvider provider) {
    return accountSNSJpaRepository.findByEmailAndProvider(email, provider)
        .map(AccountSNSEntity::toDomain);

  }

  @Override
  public AccountSNS save(AccountSNS accountSNS) {
    return accountSNSJpaRepository.save(AccountSNSEntity.from(accountSNS))
        .toDomain();

  }
}
