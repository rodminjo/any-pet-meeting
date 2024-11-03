package com.backend.accountmanagement.account.domain;

import com.backend.accountmanagement.common.domain.BaseDomain;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Role extends BaseDomain {

  private long id = 0L;
  private String roleName;
  private String roleDesc;

  @Builder
  public Role(long id, String roleName, String roleDesc) {
    this.id = id;
    this.roleName = roleName;
    this.roleDesc = roleDesc;
  }
}
