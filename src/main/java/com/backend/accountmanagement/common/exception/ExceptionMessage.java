package com.backend.accountmanagement.common.exception;


public class ExceptionMessage {

  // oauth
  public static final String INVALID_OAUTH_PROVIDER = "유효한 인증처가 아닙니다.";

  // jwt
  public static final String REFRESH_TOKEN_NOT_VAILD = "유효한 refresh 토큰이 아닙니다.";
  public static final String ACCESS_TOKEN_EXPIRED = "access token이 만료되었습니다.";
  public static final String REFRESH_TOKEN_EXPIRED = "refresh token이 만료되었습니다.";
  public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh Token이 존재하지 않습니다.";


  // Login
  public static final String LOGIN_INFO_NOT_FOUND = "입력한 이메일이나 비밀번호가 존재하지 않습니다.";
  public static final String ACCOUNT_NOT_FOUND = "해당하는 계정이 존재하지 않습니다.";
  public static final String LOGIN_FAILED = "아이디와 비밀번호가 일치하지 않습니다.";

  // join
  public static final String INVALID_EMAIL = "사용할 수 없는 이메일입니다.";


  // 인증 관련
  public static final String UNAUTHENTICATED = "로그인이 필요합니다.";

  // 권한 관련
  public static final String ROLE_NOT_FOUND = "존재하지 않는 권한이 존재합니다.";

  // 메일 관련
  public static final String MAIL_WRITE_FAILED = "메일 작성에 실패하였습니다. 잠시후 다시 시도해주세요.";
  public static final String MAIL_SEND_FAILED = "메일 전송에 실패하였습니다. 잠시후 다시 시도해주세요.";


  // redis 관련
  public static final String REDIS_SAVE_FAILED = "저장중 오류가 발생했습니다. 잠시후 다시 시도해주세요.";
  public static final String ERROR_EXECUTING_EMBEDDED_REDIS = "EMBEDDED Redis 실행 오류";

  public static final String REDIS_SERVER_EXCUTABLE_NOT_FOUND = "Redis 실행을 찾을 수 없습니다";
  public static final String NOT_FOUND_AVAILABLE_PORT = "사용가능한 포트가 없습니다.";
}

