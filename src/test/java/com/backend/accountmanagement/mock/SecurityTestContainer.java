package com.backend.accountmanagement.mock;

import com.backend.accountmanagement.account.controller.port.AccountService;
import com.backend.accountmanagement.account.controller.port.EmailService;
import com.backend.accountmanagement.account.controller.port.RefreshTokenService;
import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.account.service.port.AccountSNSRepository;
import com.backend.accountmanagement.account.service.port.BannedIpRepository;
import com.backend.accountmanagement.account.service.port.AccountRepository;
import com.backend.accountmanagement.account.service.port.RefreshTokenRepository;
import com.backend.accountmanagement.account.service.port.ResourceRepository;
import com.backend.accountmanagement.account.service.port.RoleHierarchyRepository;
import com.backend.accountmanagement.account.service.port.RoleRepository;
import com.backend.accountmanagement.common.service.port.ClockHolder;
import com.backend.accountmanagement.mock.repo.FakeAccountSNSRepository;
import com.backend.accountmanagement.mock.repo.FakeBannedIpRepository;
import com.backend.accountmanagement.mock.repo.FakeAccountRepository;
import com.backend.accountmanagement.mock.repo.FakeRedisRepository;
import com.backend.accountmanagement.mock.repo.FakeRefreshTokenRepository;
import com.backend.accountmanagement.mock.repo.FakeResourceRepository;
import com.backend.accountmanagement.mock.repo.FakeRoleHierarchyRepository;
import com.backend.accountmanagement.mock.repo.FakeRoleRepository;
import com.backend.accountmanagement.redis.service.port.RedisRepository;
import com.backend.accountmanagement.security.jwt.manager.JwtManager;
import com.backend.accountmanagement.web.configs.properties.MailProperties;
import com.backend.accountmanagement.web.configs.properties.SecurityProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityTestContainer {

  public final String name = "test";
  public final String email = "test@example.com";
  public final String password = "testPassword";
  public final List<SimpleGrantedAuthority> authorities = new ArrayList<>(List.of(new SimpleGrantedAuthority("ROLE_USER")));
  public final Account account;
  public final TestingAuthenticationToken token = new TestingAuthenticationToken(email, "", authorities);
  public final Role role;
  public final String code = "1q2w3e";

  public final SecurityProperties securityProperties;
  public final MailProperties mailProperties;
  public final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  public final ClockHolder clockHolder;
  public final JwtManager jwtManager;
  public final AccountRepository accountRepository;
  public final RoleRepository roleRepository;
  public final RefreshTokenRepository refreshTokenRepository;
  public final ResourceRepository resourceRepository;
  public final RoleHierarchyRepository roleHierarchyRepository;
  public final BannedIpRepository bannedIpRepository;
  public final AccountSNSRepository accountSNSRepository;
  public final RedisRepository redisRepository;
  public final FakeRefreshTokenService refreshTokenService;
  public final UserDetailsService userDetailService;
  public final EmailService emailService;
  public final AccountService accountService;



  @Builder
  public SecurityTestContainer(ClockHolder clockHolder, long expiredTime) {
    this.securityProperties = new SecurityProperties("123412341234123412341234123412342374ijdskfg.", expiredTime, expiredTime);
    this.mailProperties = new MailProperties("127.0.0.1", 3025, "test", "1q2w3e!!", 30);
    this.jwtManager = new JwtManager(securityProperties, clockHolder);
    this.clockHolder = clockHolder;
    this.role = Role.builder()
        .roleName("ROLE_USER")
        .roleDesc("DEFAULT")
        .build();
    this.account = Account.builder()
        .name(name)
        .email(email)
        .password(this.passwordEncoder.encode(this.password))
        .build();
    this.account.getRoleSet().add(this.role);

    this.roleRepository = new FakeRoleRepository();
    this.accountRepository = new FakeAccountRepository();
    this.resourceRepository = new FakeResourceRepository();
    this.refreshTokenRepository = new FakeRefreshTokenRepository();
    this.roleHierarchyRepository = new FakeRoleHierarchyRepository();
    this.bannedIpRepository = new FakeBannedIpRepository();
    this.accountSNSRepository = new FakeAccountSNSRepository();
    this.redisRepository = new FakeRedisRepository();

    this.refreshTokenService = new FakeRefreshTokenService();
    this.userDetailService = new FakeUserDetailService(this.account);
    this.emailService = new FakeEmailService(code);
    this.accountService = new FakeAccountService(passwordEncoder, code);
  }

}
