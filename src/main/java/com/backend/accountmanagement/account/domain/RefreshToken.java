package com.backend.accountmanagement.account.domain;

import com.backend.accountmanagement.common.domain.BaseDomain;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshToken extends BaseDomain {

  private long id = 0L;
  private String email;
  private String refreshToken;

  @Builder
  public RefreshToken(long id, String email, String refreshToken) {
    this.id = id;
    this.email = email;
    this.refreshToken = refreshToken;
  }

  public void update(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
