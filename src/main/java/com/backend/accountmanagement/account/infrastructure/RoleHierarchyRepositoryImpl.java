package com.backend.accountmanagement.account.infrastructure;

import com.backend.accountmanagement.account.domain.RoleHierarchy;
import com.backend.accountmanagement.account.infrastructure.entity.RoleHierarchyEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleHierarchyJpaRepository;
import com.backend.accountmanagement.account.service.port.RoleHierarchyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RoleHierarchyRepositoryImpl implements RoleHierarchyRepository {

  private final RoleHierarchyJpaRepository roleHierarchyJpaRepository;

  @Override
  public List<RoleHierarchy> findAll() {
    List<RoleHierarchyEntity> all = roleHierarchyJpaRepository.findAll();
    return all.stream().map(RoleHierarchyEntity::toDomain).toList();

  }
}
