package com.backend.accountmanagement.security.jwt.dto;


import com.backend.accountmanagement.common.controller.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TokenResponseDto extends BaseDto {

  private String tokenType;
  private String accessToken;
  private String refreshToken;

}
