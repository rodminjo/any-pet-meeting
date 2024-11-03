package com.backend.accountmanagement.account.infrastructure.entity;

import com.backend.accountmanagement.account.domain.BannedIp;
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
@Table(name = "banned_ip")
public class BannedIpEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private long id;

  @Column
  private String ipAddress;


  public BannedIp toDomain(){
    BannedIp bannedIp = BannedIp.builder()
        .id(id)
        .ipAddress(ipAddress)
        .build();

    return bannedIp;
  }

  public static BannedIpEntity from(BannedIp bannedIp){
    BannedIpEntity bannedIpEntity = new BannedIpEntity();
    bannedIpEntity.id = bannedIp.getId();
    bannedIpEntity.ipAddress = bannedIp.getIpAddress();

    return bannedIpEntity;
  }
}
