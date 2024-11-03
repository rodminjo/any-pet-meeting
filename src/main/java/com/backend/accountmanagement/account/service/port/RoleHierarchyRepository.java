package com.backend.accountmanagement.account.service.port;

import com.backend.accountmanagement.account.domain.RoleHierarchy;
import java.util.List;

public interface RoleHierarchyRepository {

  List<RoleHierarchy> findAll();

}
