package com.backend.accountmanagement.account.infrastructure.entity;

import com.backend.accountmanagement.account.domain.Resource;
import com.backend.accountmanagement.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "account_resource")
public class ResourceEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private long id = 0L;

  private String resourceName;

  private String httpMethod;

  private int orderNum;

  private String resourceType;


  public Resource toDomain() {
    return Resource.builder()
        .id(id)
        .resourceName(this.resourceName)
        .httpMethod(this.httpMethod)
        .orderNum(this.orderNum)
        .resourceType(this.resourceType)
        .build();

  }

  static public ResourceEntity from(Resource resource) {
    ResourceEntity resourceEntity = new ResourceEntity();
    resourceEntity.id = resource.getId();
    resourceEntity.resourceName = resource.getResourceName();
    resourceEntity.httpMethod = resource.getHttpMethod();
    resourceEntity.orderNum = resource.getOrderNum();
    resourceEntity.resourceType = resource.getResourceType();

    return resourceEntity;
  }
}
