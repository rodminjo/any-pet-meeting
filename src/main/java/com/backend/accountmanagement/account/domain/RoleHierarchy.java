package com.backend.accountmanagement.account.domain;

import com.backend.accountmanagement.common.domain.BaseDomain;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RoleHierarchy extends BaseDomain {

  private long id;
  private String childName;
  private RoleHierarchy parentName;

  @Builder
  public RoleHierarchy(long id, String childName, RoleHierarchy parentName) {
    this.id = id;
    this.childName = childName;
    this.parentName = parentName;
  }
}
