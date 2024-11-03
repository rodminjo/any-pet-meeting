package com.backend.accountmanagement.security.jwt.dto;


import com.backend.accountmanagement.common.controller.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountLoginRequestDto extends BaseDto {

  private String email;
  private String password;

}
