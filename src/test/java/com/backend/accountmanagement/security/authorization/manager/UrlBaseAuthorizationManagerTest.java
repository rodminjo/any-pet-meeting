package com.backend.accountmanagement.security.authorization.manager;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.accountmanagement.security.jwt.token.JwtLoginToken;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

class UrlBaseAuthorizationManagerTest {

  private static UrlBaseAuthorizationManager urlBaseAuthorizationManager;

  @BeforeAll
  static void setUp() {
    SecurityConfig roleAdmin = new SecurityConfig("ROLE_ADMIN");
    SecurityConfig roleUser = new SecurityConfig("ROLE_USER");
    SecurityConfig roleSemi = new SecurityConfig("ROLE_SEMI");

    LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resources = new LinkedHashMap<>();

    AntPathRequestMatcher ant1 = new AntPathRequestMatcher("/test1");
    AntPathRequestMatcher ant2 = new AntPathRequestMatcher("/test2");
    AntPathRequestMatcher ant3 = new AntPathRequestMatcher("/test3");
    AntPathRequestMatcher ant4 = new AntPathRequestMatcher("/test4");
    AntPathRequestMatcher ant5 = new AntPathRequestMatcher("/test5");

    List<ConfigAttribute> configList1 = List.of();
    List<ConfigAttribute> configList2 = List.of(roleAdmin);
    List<ConfigAttribute> configList3 = List.of(roleUser);
    List<ConfigAttribute> configList4 = List.of(roleAdmin, roleSemi);
    List<ConfigAttribute> configList5 = List.of(roleAdmin, roleSemi, roleUser);

    resources.put(ant1, configList1);
    resources.put(ant2, configList2);
    resources.put(ant3, configList3);
    resources.put(ant4, configList4);
    resources.put(ant5, configList5);

    String roleHierarchyStr = "ROLE_ADMIN > ROLE_USER";
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy(roleHierarchyStr);

    List<String> ipList = new ArrayList<>(List.of("122.0.0.1"));

    urlBaseAuthorizationManager = new UrlBaseAuthorizationManager(resources, ipList, roleHierarchy);
  }

  @Test
  void 금지된_IP가_접근하면_요청을_거부한다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/none");
    request.setRemoteAddr("122.0.0.1");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getAnonymousAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isFalse();
  }


  @Test
  void 익명사용자가_접속한_URL이_Resource_목록에_없다면_전부_허용한다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/none");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getAnonymousAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isTrue();
  }

  @Test
  void 익명사용자가_접속한_URL이_Resource_목록에_있다면_거부된다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/test1");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getAnonymousAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isFalse();
  }

  @Test
  void 로그인된_사용자가_접속한_URL이_Resource_목록에_없다면_허용된다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/none");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isTrue();
  }


  @Test
  void 로그인된_사용자가_접속한_URL이_Resource_목록에_있고_Resource에_필요한_Role이_없다면_허용된다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/test1");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isTrue();
  }

  @Test
  void 로그인된_사용자가_접속한_URL이_Resource_목록에_있고_Resource에_필요한_Role이_일치하지_않으면_거부된다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/test2");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isFalse();
  }

  @Test
  void 로그인된_사용자가_접속한_URL이_Resource_목록에_있고_다중_ResourceRole중_일치하지_않으면_거부된다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/test4");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isFalse();
  }

  @Test
  void 로그인된_사용자가_접속한_URL이_Resource_목록에_있고_다중_ResourceRole중_일치하면_승인된다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/test5");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isTrue();
  }

  @Test
  void 로그인된_다중_Role_사용자가_접속한_URL이_Resource_목록에_있고_다중_ResourceRole중_일치하지_않다면_거부된다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/test4");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getMultiRoleAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isFalse();
  }

  @Test
  void 로그인된_다중_Role_사용자가_접속한_URL이_Resource_목록에_있고_다중_ResourceRole중_일치하면_승인된다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/test5");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getMultiRoleAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isTrue();
  }


  @Test
  void 로그인된_상위계층_사용자가_접속한_URL이_Resource_목록에_있고_하위_Role중_일치하면_승인된다(){
    // given
    MockHttpServletRequest request = getMockHttpServletRequest("/test3");
    RequestAuthorizationContext context = new RequestAuthorizationContext(request);

    // when
    AuthorizationDecision decision = urlBaseAuthorizationManager.check(getAdminAuthentication(), context);

    //then
    assertThat(decision.isGranted()).isTrue();
  }



  private static Supplier<Authentication> getMultiRoleAuthentication() {
    Supplier<Authentication> auth = new Supplier<Authentication>() {
      @Override
      public Authentication get() {
        return new JwtLoginToken(
            "testName",
            "testPassword",
            AuthorityUtils.createAuthorityList("ROLE_USER","ROLE_PRO")
        );
      }
    };

    return auth;
  }

  private static Supplier<Authentication> getAdminAuthentication() {
    Supplier<Authentication> auth = new Supplier<Authentication>() {
      @Override
      public Authentication get() {
        return new JwtLoginToken(
            "testName",
            "testPassword",
            AuthorityUtils.createAuthorityList("ROLE_ADMIN")
        );
      }
    };

    return auth;
  }

  private static Supplier<Authentication> getAuthentication() {
    Supplier<Authentication> auth = new Supplier<Authentication>() {
      @Override
      public Authentication get() {
        return new JwtLoginToken(
            "testName",
            "testPassword",
            AuthorityUtils.createAuthorityList("ROLE_USER")
        );
      }
    };

    return auth;
  }

  private static Supplier<Authentication> getAnonymousAuthentication() {
    Supplier<Authentication> auth = new Supplier<Authentication>() {
      @Override
      public Authentication get() {
        return new AnonymousAuthenticationToken(
            "key",
            "anonymous",
            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
        );
      }
    };

    return auth;
  }

  private static MockHttpServletRequest getMockHttpServletRequest(String url) {
    MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), url);
    request.setServletPath(url);
    return request;
  }

}