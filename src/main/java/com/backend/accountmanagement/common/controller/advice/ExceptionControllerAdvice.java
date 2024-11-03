package com.backend.accountmanagement.common.controller.advice;

import com.backend.accountmanagement.common.controller.response.CommonApiResponse;
import com.backend.accountmanagement.common.controller.response.CommonApiResponse.ErrorResponse;
import com.backend.accountmanagement.common.exception.ResourceNotConvertException;
import com.backend.accountmanagement.common.exception.ResourceNotFoundException;
import com.backend.accountmanagement.utils.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Component
@RestControllerAdvice
public class ExceptionControllerAdvice {

  public Logger logger = LoggerFactory.getLogger( this.getClass().getSimpleName() );

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<CommonApiResponse<ErrorResponse>> illegalArgumentExceptionHandler(IllegalArgumentException e){
    return CommonApiResponse.createErrorResponse(HttpStatus.BAD_REQUEST, "40001", e.getMessage());

  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<CommonApiResponse<ErrorResponse>> illegalStateExceptionHandler(IllegalStateException e){
    return CommonApiResponse.createErrorResponse(HttpStatus.BAD_REQUEST, "40001", e.getMessage());

  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CommonApiResponse<ErrorResponse>> resourceNotFoundExceptionHandler(ResourceNotFoundException e){
    return CommonApiResponse.createErrorResponse(HttpStatus.BAD_REQUEST, "40001", e.getMessage());

  }

  @ExceptionHandler(ResourceNotConvertException.class)
  public ResponseEntity<CommonApiResponse<ErrorResponse>> resourceNotConvertExceptionHandler(ResourceNotConvertException e){
    return CommonApiResponse.createErrorResponse(HttpStatus.BAD_REQUEST, "40002", e.getMessage());

  }


  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<CommonApiResponse<ErrorResponse>> noResourceFoundExceptionHandler(NoResourceFoundException e){
    return CommonApiResponse.createErrorResponse(HttpStatus.NOT_FOUND, "40401", "해당하는 요청이 존재하지 않습니다.");

  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CommonApiResponse<ErrorResponse>> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e)
      throws JsonProcessingException {
      Map<String, String> validationErrors = new HashMap<>();
      List<ObjectError> validationErrorList = e.getBindingResult().getAllErrors();

      validationErrorList.forEach((error) -> {
        String fieldName = ((FieldError) error).getField();
        String validationMsg = error.getDefaultMessage();
        validationErrors.put(fieldName, validationMsg);
      });

    String errorListStr = MapperUtils.getMapper().writeValueAsString(validationErrors);

    return CommonApiResponse.createErrorResponse(HttpStatus.BAD_REQUEST, "40003", errorListStr);

  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<CommonApiResponse<ErrorResponse>> exceptionHandler(Exception e){
    logger.error(e.getMessage());
    return CommonApiResponse.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "50001", "죄송합니다. 잠시후 다시 시도해주세요.");

  }
}
