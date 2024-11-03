package com.backend.accountmanagement.account.infrastructure.entity;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.AccountStatus;
import com.backend.accountmanagement.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "account")
public class AccountEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id = 0L;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column
  private String password;

  @Column(nullable = false)
  private String nickname;

  @Column
  private String phoneNumber;

  @Column
  private LocalDate birthDate;

  @Column
  private String gender;

  @Column
  private String address;

  @Column
  private String addressDetail;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AccountStatus status;

  @Column
  private String profileImageUrl;



  public Account toDomain(){
    Account account = Account.builder()
        .id(id)
        .name(name)
        .email(email)
        .password(password)
        .nickname(nickname)
        .phoneNumber(phoneNumber)
        .birthDate(birthDate)
        .gender(gender)
        .address(address)
        .addressDetail(addressDetail)
        .status(status)
        .profileImageUrl(profileImageUrl)
        .build();

    return account;
  }

  public static AccountEntity from(Account account){
    AccountEntity accountEntity = new AccountEntity();
    accountEntity.id = account.getId();
    accountEntity.name = account.getName();
    accountEntity.email = account.getEmail();
    accountEntity.password = account.getPassword();
    accountEntity.nickname = account.getNickname();
    accountEntity.phoneNumber = account.getPhoneNumber();
    accountEntity.birthDate = account.getBirthDate();
    accountEntity.gender = account.getGender();
    accountEntity.address = account.getAddress();
    accountEntity.addressDetail = account.getAddressDetail();
    accountEntity.status = account.getStatus();
    accountEntity.profileImageUrl = account.getProfileImageUrl();

    return accountEntity;
  }
}
