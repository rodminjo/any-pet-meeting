package com.backend.accountmanagement.common.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@Builder
public class CommonApiResponse<T> {

  private final String message;

  private final T data;

  public ResponseEntity<CommonApiResponse<T>> toResponse(final HttpStatus httpStatus){
    return ResponseEntity
        .status(httpStatus)
        .body(this);
  }

  // 일반 응답
  public static <T> ResponseEntity<CommonApiResponse<T>> createResponse(HttpStatus httpStatus, String message, T data) {
    return CommonApiResponse.<T>builder()
        .message(message)
        .data(data)
        .build()
        .toResponse(httpStatus);
  }

  // Error 응답
  public static ResponseEntity<CommonApiResponse<ErrorResponse>> createErrorResponse(HttpStatus httpStatus, String errorId, String errorDescription) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .errorId(errorId)
        .errorDescription(errorDescription)
        .build();

    return CommonApiResponse.<ErrorResponse>builder()
        .message(CommonResponseMessage.ERROR)
        .data(errorResponse)
        .build()
        .toResponse(httpStatus);

  }

  @Getter
  @Builder
  @RequiredArgsConstructor
  public static class ErrorResponse {
    private final String errorId;
    private final String errorDescription;
  }
}



