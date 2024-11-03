package com.backend.accountmanagement.account.infrastructure.jpa;

import com.backend.accountmanagement.account.infrastructure.entity.RoleHierarchyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleHierarchyJpaRepository extends JpaRepository<RoleHierarchyEntity, Long> {
}
