package com.backend.accountmanagement.account.domain;

import com.backend.accountmanagement.common.domain.BaseDomain;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountSNS extends BaseDomain {

  private long id = 0L;
  private String email;
  private SNSProvider provider;
  private String providerAccountId;


  @Builder
  public AccountSNS(long id, String email, SNSProvider provider, String providerAccountId) {
    this.id = id;
    this.email = email;
    this.provider = provider;
    this.providerAccountId = providerAccountId;
  }
}
