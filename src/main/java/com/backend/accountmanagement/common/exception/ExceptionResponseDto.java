package com.backend.accountmanagement.common.exception;

import com.backend.accountmanagement.common.controller.dto.BaseDto;
import lombok.Getter;

@Getter
public class ExceptionResponseDto extends BaseDto {

  private final int errorId;
  private final String errorDesc;

  public ExceptionResponseDto(int errorId, String errorDesc) {
    this.errorId = errorId;
    this.errorDesc = errorDesc;
  }
}
