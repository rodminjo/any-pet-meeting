package com.backend.accountmanagement.account.controller.dto;

import com.backend.accountmanagement.account.domain.AccountCreate;
import com.backend.accountmanagement.account.domain.AccountVerification;
import com.backend.accountmanagement.common.controller.dto.BaseDto;
import com.backend.accountmanagement.common.controller.dto.ValidationMessage;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AccountRequestDto {

  @NoArgsConstructor
  @Getter
  @Setter
  public static class AccountCreateRequestDto extends BaseDto {

    // 필수 입력값
    @NotBlank(message = ValidationMessage.NAME_REQUIRED)
    @Pattern(regexp = ValidationMessage.NAME_PATTERN_REGEX, message = ValidationMessage.NAME_PATTERN)
    private String name;

    @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
    @Email(message = ValidationMessage.EMAIL_INVALID)
    private String email;

    @NotBlank(message = ValidationMessage.PASSWORD_REQUIRED)
    @Pattern(regexp = ValidationMessage.PASSWORD_PATTERN_REGEX, message = ValidationMessage.PASSWORD_PATTERN)
    private String password;


    // 선택 입력값
    @Pattern(regexp = ValidationMessage.PHONE_NUMBER_PATTERN_REGEX, message = ValidationMessage.PHONE_NUMBER_PATTERN)
    private String phoneNumber;

    @Past(message = ValidationMessage.BIRTH_DATE_PAST)
    private LocalDate birthDate;

    private AccountGender gender;
    private String address;
    private String addressDetail;

    private enum AccountGender {
      MALE, FEMALE
    }

    @Builder
    public AccountCreateRequestDto(String name, String email, String password, String phoneNumber,
        LocalDate birthDate, String gender, String address, String addressDetail) {
      this.name = name;
      this.email = email;
      this.password = password;
      this.phoneNumber = phoneNumber;
      this.birthDate = birthDate;
      this.gender = StringUtils.isNotEmpty(gender) ? AccountGender.valueOf(gender.toUpperCase()) : null;
      this.address = address;
      this.addressDetail = addressDetail;
    }


    public AccountCreate ofCreate() {
      AccountCreate create = AccountCreate.builder()
          .name(name)
          .email(email)
          .password(password)
          .phoneNumber(phoneNumber)
          .birthDate(birthDate)
          .gender(gender != null ? gender.name() : null)
          .address(address)
          .addressDetail(addressDetail)
          .build();

      return create;
    }
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class AccountSendRequestDto extends BaseDto {

    @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
    @Email(message = ValidationMessage.EMAIL_INVALID)
    private String email;

    @Builder
    public AccountSendRequestDto(String email) {
      this.email = email;
    }
  }


  @NoArgsConstructor
  @Getter
  @Setter
  public static class AccountVerifyRequestDto extends BaseDto {

    @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
    @Email(message = ValidationMessage.EMAIL_INVALID)
    private String email;

    @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
    private String code;

    @Builder
    public AccountVerifyRequestDto(String email, String code) {
      this.email = email;
      this.code = code;
    }

    public AccountVerification ofVerification(){
      AccountVerification verification = AccountVerification.builder()
         .email(email)
         .verificationCode(code)
         .build();

      return verification;
    }
  }
}
