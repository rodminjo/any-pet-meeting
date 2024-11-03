package com.backend.accountmanagement.account.infrastructure.jpa;

import com.backend.accountmanagement.account.infrastructure.entity.RoleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {

  Optional<RoleEntity> findByRoleName(String roleName);

  List<RoleEntity> findAllByRoleNameIn(List<String> roleNames);
}
