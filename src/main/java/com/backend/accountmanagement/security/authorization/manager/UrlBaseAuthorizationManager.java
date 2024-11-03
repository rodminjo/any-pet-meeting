package com.backend.accountmanagement.security.authorization.manager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthoritiesAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher.MatchResult;
import org.springframework.util.StringUtils;

public class UrlBaseAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

  private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap;
  private List<String> bannedIpList;

  // 권한계층 체크를 위해 delegate 추가
  private final AuthoritiesAuthorizationManager delegate = new AuthoritiesAuthorizationManager();

  public UrlBaseAuthorizationManager(
      LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap,
      List<String> bannedIpList,
      RoleHierarchy roleHierarchy
  ) {
    this.requestMap = requestMap;
    this.bannedIpList = bannedIpList;
    this.delegate.setRoleHierarchy(roleHierarchy);
  }

  @Override
  public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    AuthorizationManager.super.verify(authentication, object);
  }

  @Override
  public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {

    if (checkBannedIp(object)){
      return new AuthorizationDecision(false);
    }

    // url 검사
    // 이후 해당 Url에 따른 authorization 검사
    Set<String> authorities = null;
    List<ConfigAttribute> neededRole = null;

    for (var requestEntry : requestMap.entrySet()){
      MatchResult matchResult = requestEntry.getKey().matcher(object.getRequest());

      // url 목록에 존재한다면
      if(matchResult.isMatch()){
        // 필요한 Role을 가져옴
        neededRole = requestEntry.getValue();

        if (neededRole != null && !neededRole.isEmpty()){
          authorities = neededRole.stream().map(ConfigAttribute::getAttribute).collect(Collectors.toSet());
          // 필요한 Role이 존재하면 권한계층 또한 체크
          return this.delegate.check(authentication, authorities);

        } else {
          // 권한목록에 존재하지 않으면 authenticated 되어있는지만 확인
          return AuthenticatedAuthorizationManager.authenticated().check(authentication, authorities);

        }
      }
    }

    // Resource 목록에 해당 Url이 없다면 전부 허용
    return new AuthorizationDecision(true);

  }

  public boolean checkBannedIp(RequestAuthorizationContext object) {
    // ip 허용부터 검사
    String address = object.getRequest().getRemoteAddr();

    if (!StringUtils.hasText(address)) {
      return true;
    }

    for (var i : bannedIpList){
      if (address.equals(i)){
        return true;
      }
    }

    return false;
  }
}
