package com.backend.accountmanagement.account.infrastructure.entity;

import com.backend.accountmanagement.account.domain.RefreshToken;
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
@Table(name = "account_refresh_token")
public class RefreshTokenEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private long id;

  @Column
  private String email;

  @Column
  private String refreshToken;

  public RefreshToken toDomain(){
    RefreshToken token = RefreshToken.builder()
        .id(id)
        .email(email)
        .refreshToken(refreshToken)
        .build();

    return token;
  }

  public static RefreshTokenEntity from(RefreshToken refreshToken){
    RefreshTokenEntity accountEntity = new RefreshTokenEntity();
    accountEntity.id = refreshToken.getId();
    accountEntity.email = refreshToken.getEmail();
    accountEntity.refreshToken = refreshToken.getRefreshToken();

    return accountEntity;
  }
}
