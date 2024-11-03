package com.backend.accountmanagement.account.infrastructure.jpa;

import com.backend.accountmanagement.account.infrastructure.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceJpaRepository extends JpaRepository<ResourceEntity, Long> {
}
