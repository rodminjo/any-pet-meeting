package com.backend.accountmanagement.mock.data;

import com.backend.accountmanagement.account.domain.AccountStatus;
import com.backend.accountmanagement.account.domain.AccountVerification;
import com.backend.accountmanagement.account.infrastructure.entity.AccountEntity;
import com.backend.accountmanagement.account.infrastructure.entity.AccountRoleEntity;
import com.backend.accountmanagement.account.infrastructure.entity.ResourceEntity;
import com.backend.accountmanagement.account.infrastructure.entity.RoleEntity;
import com.backend.accountmanagement.account.infrastructure.entity.RoleHierarchyEntity;
import com.backend.accountmanagement.account.infrastructure.entity.RoleResourceEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.AccountJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.AccountRoleJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.ResourceJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleHierarchyJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleResourceJpaRepository;
import com.backend.accountmanagement.account.service.AccountServiceImpl;
import com.backend.accountmanagement.redis.service.port.RedisRepository;
import com.backend.accountmanagement.security.authorization.factory.RoleHierarchyMapFactoryBean;
import com.backend.accountmanagement.security.authorization.factory.UrlResourceMapFactoryBean;
import com.backend.accountmanagement.utils.RandomUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TestDataInitializer {

  private final PasswordEncoder passwordEncoder;

  private final RoleJpaRepository roleJpaRepository;
  private final AccountJpaRepository accountJpaRepository;
  private final AccountRoleJpaRepository accountRoleJpaRepository;
  private final ResourceJpaRepository resourceJpaRepository;
  private final RoleResourceJpaRepository roleResourceJpaRepository;
  private final RoleHierarchyJpaRepository roleHierarchyJpaRepository;

  private final RedisRepository redisRepository;

  private final UrlResourceMapFactoryBean urlResourceMapFactoryBean;
  private final RoleHierarchyMapFactoryBean roleHierarchyMapFactoryBean;




  public void initUser() {
    RoleEntity adminRole = RoleEntity.builder()
        .roleName("ROLE_ADMIN")
        .roleDesc("ADMIN")
        .build();

    RoleEntity userRole = RoleEntity.builder()
        .roleName("ROLE_USER")
        .roleDesc("USER")
        .build();

    RoleEntity userLowRole = RoleEntity.builder()
        .roleName("ROLE_USER_LOW")
        .roleDesc("USER")
        .build();

    RoleEntity anonymousRole = RoleEntity.builder()
        .roleName("ROLE_ANONYMOUS")
        .roleDesc("ANONYMOUS")
        .build();

    AccountEntity account = AccountEntity.builder()
        .name("authTest")
        .email("authTest@naver.com")
        .password(passwordEncoder.encode("password"))
        .nickname(RandomUtils.generateRandomMixNumNStr(15))
        .phoneNumber("01012345678")
        .birthDate(LocalDate.of(2000,1,1))
        .gender("MALE")
        .address("서울특별시 양천구 목동")
        .addressDetail("강서고등학교")
        .status(AccountStatus.ACTIVE)
        .build();

    AccountEntity account2 = AccountEntity.builder()
        .name("test")
        .email("test@naver.com")
        .password(passwordEncoder.encode("password"))
        .nickname(RandomUtils.generateRandomMixNumNStr(15))
        .phoneNumber("01012341234")
        .birthDate(LocalDate.of(2000,1,1))
        .gender("FEMALE")
        .address("서울특별시 양천구 목동")
        .addressDetail("강서고등학교")
        .status(AccountStatus.ACTIVE)
        .build();

    AccountRoleEntity accountRole = AccountRoleEntity.builder()
        .accountEntity(account)
        .roleEntity(userRole)
        .build();

    ResourceEntity resourceNoneRole = ResourceEntity.builder()
        .resourceName("/test/auth/")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(1)
        .build();

    ResourceEntity resourceAnonymousRole = ResourceEntity.builder()
        .resourceName("/test/auth/anonymous")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(2)
        .build();

    ResourceEntity resourceAdminAndUserRole = ResourceEntity.builder()
        .resourceName("/test/auth/user")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(3)
        .build();

    ResourceEntity resourceUserLowRole = ResourceEntity.builder()
        .resourceName("/test/auth/user/low")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(3)
        .build();

    RoleResourceEntity resourceRole1 = RoleResourceEntity.builder()
        .resourceEntity(resourceAnonymousRole)
        .roleEntity(anonymousRole)
        .build();

    RoleResourceEntity resourceRole2 = RoleResourceEntity.builder()
        .resourceEntity(resourceAdminAndUserRole)
        .roleEntity(adminRole)
        .build();

    RoleResourceEntity resourceRole3 = RoleResourceEntity.builder()
        .resourceEntity(resourceAdminAndUserRole)
        .roleEntity(userRole)
        .build();

    RoleResourceEntity resourceRole4 = RoleResourceEntity.builder()
        .resourceEntity(resourceUserLowRole)
        .roleEntity(userLowRole)
        .build();

    RoleHierarchyEntity userRoleHierarchy = RoleHierarchyEntity.builder()
        .childName("ROLE_USER")
        .parentName(null)
        .build();

    RoleHierarchyEntity lowUserRoleHierarchy = RoleHierarchyEntity.builder()
        .childName("ROLE_USER_LOW")
        .parentName(userRoleHierarchy)
        .build();

    roleJpaRepository.saveAll(List.of(adminRole, userRole, anonymousRole, userLowRole));
    accountJpaRepository.saveAll(List.of(account, account2));
    accountRoleJpaRepository.saveAll(List.of(accountRole));
    resourceJpaRepository.saveAll(List.of(resourceNoneRole, resourceAnonymousRole, resourceAdminAndUserRole, resourceUserLowRole));
    roleResourceJpaRepository.saveAll(List.of(resourceRole1, resourceRole2, resourceRole3, resourceRole4));
    roleHierarchyJpaRepository.saveAll(List.of(userRoleHierarchy, lowUserRoleHierarchy));
    urlResourceMapFactoryBean.reload();
    roleHierarchyMapFactoryBean.reload();
  }

  public void initEmailTestData() throws JsonProcessingException {
    String email1 = "test1@gmail.com";
    String email2 = "test2@gmail.com";
    String code1 = "rightCo";
    String code2 = "rightCo";

    AccountVerification valid = AccountVerification.createVerification(AccountServiceImpl.VERIFIED_EMAIL_PREFIX + email1, code1, 5);
    AccountVerification expired = AccountVerification.createVerification(AccountServiceImpl.VERIFIED_EMAIL_PREFIX + email2, code2, 0);

    redisRepository.setDataWithExpired(valid.getEmail(), valid, 5 * 1000 * 60);
    redisRepository.setDataWithExpired(expired.getEmail(), expired, 1);
  }

}
