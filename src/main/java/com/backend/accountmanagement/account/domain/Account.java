package com.backend.accountmanagement.account.domain;

import com.backend.accountmanagement.common.domain.BaseDomain;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Account extends BaseDomain {

  private long id = 0L;
  private String name;
  private String email;
  private String password;
  private String nickname;
  private String phoneNumber;
  private LocalDate birthDate;
  private String gender;
  private String address;
  private String addressDetail;
  private AccountStatus status;
  private String profileImageUrl;

  private Set<Role> roleSet = new HashSet<>();


  @Builder
  public Account(long id, String name, String email, String password, String nickname,
      String phoneNumber, LocalDate birthDate, String gender, String address, String addressDetail,
      AccountStatus status, String profileImageUrl) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
    this.birthDate = birthDate;
    this.gender = gender;
    this.address = address;
    this.addressDetail = addressDetail;
    this.status = status;
    this.profileImageUrl = profileImageUrl;

  }
}
