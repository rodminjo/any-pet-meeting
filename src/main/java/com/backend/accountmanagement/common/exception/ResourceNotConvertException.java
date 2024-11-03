package com.backend.accountmanagement.common.exception;

public class ResourceNotConvertException extends RuntimeException{

  public ResourceNotConvertException(String message){
    super(message);
  }

  public ResourceNotConvertException(String datasource, long id) {
    super(datasource + "에서 ID " + id + "를 변환하는 작업을 실패하였습니다");
  }
}
