package com.backend.accountmanagement.mock.repo;

import com.backend.accountmanagement.account.domain.RoleHierarchy;
import com.backend.accountmanagement.account.service.port.RoleHierarchyRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FakeRoleHierarchyRepository implements RoleHierarchyRepository {

  private final AtomicLong autoGeneratedId = new AtomicLong(0);
  private final List<RoleHierarchy> data = Collections.synchronizedList(new ArrayList<>());


  @Override
  public List<RoleHierarchy> findAll() {
    RoleHierarchy roleAdmin = RoleHierarchy.builder()
        .id(autoGeneratedId.incrementAndGet())
        .childName("ROLE_ADMIN")
        .parentName(null)
        .build();

    RoleHierarchy roleUser = RoleHierarchy.builder()
        .id(autoGeneratedId.incrementAndGet())
        .childName("ROLE_USER")
        .parentName(roleAdmin)
        .build();

    RoleHierarchy roleNotUser = RoleHierarchy.builder()
        .id(autoGeneratedId.incrementAndGet())
        .childName("ROLE_NOT_USER")
        .parentName(roleUser)
        .build();

    RoleHierarchy roleAnonymous = RoleHierarchy.builder()
        .id(autoGeneratedId.incrementAndGet())
        .childName("ROLE_ANONYMOUS")
        .parentName(roleAdmin)
        .build();

    data.clear();
    data.addAll(List.of(roleAdmin, roleUser, roleNotUser, roleAnonymous));
    return data;
  }
}