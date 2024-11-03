package com.backend.accountmanagement.account.infrastructure.entity;

import com.backend.accountmanagement.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "account_role")
public class AccountRoleEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private long id = 0L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private AccountEntity accountEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private RoleEntity roleEntity;


  @Builder
  public AccountRoleEntity(long id, AccountEntity accountEntity, RoleEntity roleEntity) {
    this.id = id;
    this.accountEntity = accountEntity;
    this.roleEntity = roleEntity;
  }

}
