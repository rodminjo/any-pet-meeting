package com.backend.accountmanagement.account.domain;


import com.backend.accountmanagement.common.domain.BaseDomain;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BannedIp extends BaseDomain {

  private long id = 0L;
  private String ipAddress;


  @Builder
  public BannedIp(long id, String ipAddress) {
    this.id = id;
    this.ipAddress = ipAddress;
  }
}
