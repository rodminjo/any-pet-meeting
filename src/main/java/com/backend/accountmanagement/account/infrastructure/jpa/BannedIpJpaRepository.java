package com.backend.accountmanagement.account.infrastructure.jpa;

import com.backend.accountmanagement.account.infrastructure.entity.BannedIpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannedIpJpaRepository extends JpaRepository<BannedIpEntity, Long> {


}
