package com.backend.accountmanagement.account.infrastructure;

import com.backend.accountmanagement.account.domain.BannedIp;
import com.backend.accountmanagement.account.infrastructure.entity.BannedIpEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.BannedIpJpaRepository;
import com.backend.accountmanagement.account.service.port.BannedIpRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BannedIpRepositoryImpl implements BannedIpRepository {

  private final BannedIpJpaRepository bannedIpJpaRepository;

  @Override
  public List<BannedIp> findAll() {
    return bannedIpJpaRepository.findAll()
        .stream()
        .map(BannedIpEntity::toDomain)
        .toList();

  }

  @Override
  public BannedIp save(BannedIp bannedIp) {
    BannedIpEntity savedIp = bannedIpJpaRepository.save(BannedIpEntity.from(bannedIp));
    return savedIp.toDomain();

  }
}
