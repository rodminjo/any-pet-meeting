package com.backend.accountmanagement.account.controller.dto;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.common.controller.dto.BaseDto;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AccountResponseDto extends BaseDto {

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class AccountBaseResponseDto {

    private long id;

    private String name;

    private String email;

    private String userRoles;

    public static AccountBaseResponseDto from(Account account) {
      String roleNameListStr = account.getRoleSet()
          .stream()
          .map(Role::getRoleName)
          .collect(Collectors.joining(","));

      return AccountBaseResponseDto.builder()
          .id(account.getId())
          .name(account.getName())
          .email(account.getEmail())
          .userRoles(roleNameListStr)
          .build();
    }

  }
}
