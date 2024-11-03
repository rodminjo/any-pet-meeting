package com.backend.accountmanagement.account.service.port;

import com.backend.accountmanagement.account.domain.Role;
import java.util.Optional;

public interface RoleRepository {
  public Optional<Role> findByRoleName(String roleName);

  public Role save(Role role);
}
