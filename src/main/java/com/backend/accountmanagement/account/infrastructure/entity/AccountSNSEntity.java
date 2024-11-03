package com.backend.accountmanagement.account.infrastructure.entity;

import com.backend.accountmanagement.account.domain.AccountSNS;
import com.backend.accountmanagement.account.domain.SNSProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "account_sns")
public class AccountSNSEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id = 0L;

  @Column(nullable = false)
  private String email;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SNSProvider provider;

  @Column(nullable = false)
  private String providerAccountId;

  public AccountSNS toDomain() {
    return AccountSNS.builder()
        .id(id)
        .email(email)
        .provider(provider)
        .providerAccountId(providerAccountId)
        .build();

  }

  public static AccountSNSEntity from(AccountSNS accountSNS){
    AccountSNSEntity entity = new AccountSNSEntity();
    entity.id = accountSNS.getId();
    entity.email = accountSNS.getEmail();
    entity.provider = accountSNS.getProvider();
    entity.providerAccountId = accountSNS.getProviderAccountId();

    return entity;

  }

}