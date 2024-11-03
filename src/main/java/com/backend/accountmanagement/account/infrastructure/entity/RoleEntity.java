package com.backend.accountmanagement.account.infrastructure.entity;

import com.backend.accountmanagement.account.domain.Role;
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

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "role")
public class RoleEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private long id = 0L;

  private String roleName;

  private String roleDesc;




  public Role toDomain() {
    return Role.builder()
        .id(id)
        .roleName(roleName)
        .roleDesc(roleDesc)
        .build();

  }

  public static RoleEntity from(Role role) {
    RoleEntity roleEntity = new RoleEntity();
    roleEntity.id = role.getId();
    roleEntity.roleName = role.getRoleName();
    roleEntity.roleDesc = role.getRoleDesc();

    return roleEntity;
  }
}
