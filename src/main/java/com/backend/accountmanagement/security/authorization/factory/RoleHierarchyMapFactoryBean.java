package com.backend.accountmanagement.security.authorization.factory;

import com.backend.accountmanagement.account.controller.port.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleHierarchyMapFactoryBean implements FactoryBean<RoleHierarchyImpl> {


  private final RoleHierarchyService roleHierarchyService;

  private RoleHierarchyImpl roleHierarchy;

  public void init() {
    String roleHeirarchyStr = roleHierarchyService.findAllHierarchy();
    roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy(roleHeirarchyStr);
  }

  public RoleHierarchyImpl getObject() {
    if (roleHierarchy == null) {
      init();
    }

    return roleHierarchy;
  }

  public void reload(){
    String newRoleHierarchyStr = roleHierarchyService.findAllHierarchy();
    roleHierarchy.setHierarchy(newRoleHierarchyStr);
  }

  public Class<String> getObjectType() {
    return String.class;
  }

  public boolean isSingleton() {
    return true;
  }
}
