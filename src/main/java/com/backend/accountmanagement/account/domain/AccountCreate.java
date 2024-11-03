package com.backend.accountmanagement.account.domain;

import com.backend.accountmanagement.utils.RandomUtils;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Getter
public class AccountCreate {

  private final String name;
  private final String email;
  private final String password;
  private final String phoneNumber;
  private final LocalDate birthDate;
  private final String gender;
  private final String address;
  private final String addressDetail;
  private final String profileImageUrl;

  @Builder
  public AccountCreate(
      String name,
      String email,
      String password,
      String phoneNumber,
      LocalDate birthDate,
      String gender,
      String address,
      String addressDetail,
      String profileImageUrl) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.birthDate = birthDate;
    this.gender = gender;
    this.address = address;
    this.addressDetail = addressDetail;
    this.profileImageUrl = profileImageUrl;
  }

  public Account of(PasswordEncoder passwordEncoder) {
    Account account = Account.builder()
        .name(name)
        .email(email)
        .password(StringUtils.hasText(password) ? passwordEncoder.encode(password) : null)
        .nickname(RandomUtils.generateRandomMixNumNStr(15))
        .phoneNumber(phoneNumber)
        .birthDate(birthDate)
        .gender(gender)
        .address(address)
        .addressDetail(addressDetail)
        .status(AccountStatus.ACTIVE)
        .profileImageUrl(profileImageUrl)
        .build();

    Role newRole = Role.builder()
        .id(0L)
        .roleName("ROLE_USER")
        .roleDesc("DEFAULT")
        .build();
    account.getRoleSet().add(newRole);

    return account;
  }
}
