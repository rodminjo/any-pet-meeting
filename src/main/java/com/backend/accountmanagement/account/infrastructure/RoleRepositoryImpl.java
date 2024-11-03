package com.backend.accountmanagement.account.infrastructure;

import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.account.infrastructure.entity.RoleEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleJpaRepository;
import com.backend.accountmanagement.account.service.port.RoleRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RoleRepositoryImpl implements RoleRepository {

  private final RoleJpaRepository roleJpaRepository;

  @Override
  public Optional<Role> findByRoleName(String roleName) {
    return roleJpaRepository.findByRoleName(roleName).map(RoleEntity::toDomain);
  }

  @Override
  public Role save(Role role) {
    RoleEntity savedRole = roleJpaRepository.save(RoleEntity.from(role));
    return savedRole.toDomain();
  }
}
