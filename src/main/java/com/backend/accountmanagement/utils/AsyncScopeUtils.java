package com.backend.accountmanagement.utils;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;

public class AsyncScopeUtils {

  private static final Map<String, Authentication> authenticationMap = new HashMap<>();

  public static Authentication getAuthentication() {
    return authenticationMap.get(Long.toString(Thread.currentThread().getId()));
  }

  public static void setAuthentication(Authentication authentication) {
    authenticationMap.put(Long.toString(Thread.currentThread().getId()), authentication);
  }

  public static void removeAuthentication() {
    authenticationMap.remove(Long.toString(Thread.currentThread().getId()));
  }
}
