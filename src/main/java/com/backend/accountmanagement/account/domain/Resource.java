package com.backend.accountmanagement.account.domain;

import com.backend.accountmanagement.common.domain.BaseDomain;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Resource extends BaseDomain {

  private long id = 0L;

  private String resourceName;

  private String httpMethod;

  private int orderNum;

  private String resourceType;

  private Set<Role> roleSet = new HashSet<>();
  @Builder
  public Resource(long id, String resourceName, String httpMethod, int orderNum,
      String resourceType) {
    this.id = id;
    this.resourceName = resourceName;
    this.httpMethod = httpMethod;
    this.orderNum = orderNum;
    this.resourceType = resourceType;
  }
}
