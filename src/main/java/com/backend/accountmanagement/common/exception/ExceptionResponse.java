package com.backend.accountmanagement.common.exception;

public class ExceptionResponse {

  public static final ExceptionResponseDto JWT_TOKEN_EXPIRED =
      new ExceptionResponseDto(40101, ExceptionMessage.ACCESS_TOKEN_EXPIRED);

  public static final ExceptionResponseDto ACCESS_WITHOUT_JWT_TOKEN =
      new ExceptionResponseDto(40102, ExceptionMessage.UNAUTHENTICATED);

  public static final ExceptionResponseDto JWT_REFRESH_TOKEN_EXPIRED =
      new ExceptionResponseDto(40102, ExceptionMessage.UNAUTHENTICATED);



}




