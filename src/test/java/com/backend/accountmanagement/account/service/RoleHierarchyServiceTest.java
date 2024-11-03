package com.backend.accountmanagement.account.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.accountmanagement.account.controller.port.RoleHierarchyService;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleHierarchyServiceTest {

  private RoleHierarchyService roleHierarchyService;

  @BeforeEach
  void setUp() {
    SecurityTestContainer testContainer =
        new SecurityTestContainer(new MockClockHolder(new Date()), 0L);
    roleHierarchyService = new RoleHierarchyServiceImpl(testContainer.roleHierarchyRepository);

  }

  @Test
  void 권한계층을_불러와서_정해진_혙태의_문자열로_반환한다(){
    // when
    String allHierarchy = roleHierarchyService.findAllHierarchy();

    // then
    assertThat(allHierarchy).isNotNull();
    assertThat(allHierarchy.contains("ROLE_ADMIN > ROLE_USER")).isTrue();
    assertThat(allHierarchy.contains("ROLE_USER > ROLE_NOT_USER")).isTrue();
    assertThat(allHierarchy.contains("ROLE_ADMIN > ROLE_ANONYMOUS")).isTrue();
    assertThat(allHierarchy.split("\n").length).isEqualTo(3);
  }

}