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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "role_resource")
public class RoleResourceEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private RoleEntity roleEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private ResourceEntity resourceEntity;


}
