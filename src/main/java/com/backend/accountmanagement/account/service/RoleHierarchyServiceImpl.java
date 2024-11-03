package com.backend.accountmanagement.account.service;

import com.backend.accountmanagement.account.controller.port.RoleHierarchyService;
import com.backend.accountmanagement.account.domain.RoleHierarchy;
import com.backend.accountmanagement.account.service.port.RoleHierarchyRepository;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

  private final RoleHierarchyRepository roleHierarchyRepository;

  @Override
  public String findAllHierarchy() {
    List<RoleHierarchy> all = roleHierarchyRepository.findAll();
    Iterator<RoleHierarchy> itr = all.iterator();
    StringBuffer concatRoles = new StringBuffer();

    while (itr.hasNext()) {
      RoleHierarchy model = itr.next();
      if (model.getParentName() != null) {
        concatRoles.append(model.getParentName().getChildName());
        concatRoles.append(" > ");
        concatRoles.append(model.getChildName());
        concatRoles.append("\n");
      }
    }

    return concatRoles.toString();
  }
}
