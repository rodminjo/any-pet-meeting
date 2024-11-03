package com.backend.accountmanagement.account.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.backend.accountmanagement.account.domain.Resource;
import com.backend.accountmanagement.account.infrastructure.entity.ResourceEntity;
import com.backend.accountmanagement.account.infrastructure.entity.RoleEntity;
import com.backend.accountmanagement.account.infrastructure.entity.RoleResourceEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.ResourceJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleResourceJpaRepository;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

@ExtendWith({MockitoExtension.class})
class ResourceRepositoryTest {

  @Mock
  ResourceJpaRepository resourceJpaRepository;
  @Mock
  RoleResourceJpaRepository roleResourceJpaRepository;
  @Mock
  RoleJpaRepository roleJpaRepository;

  @InjectMocks
  ResourceRepositoryImpl resourceRepositoryImpl;

  private RoleEntity adminRole;
  private RoleEntity userRole;
  private RoleEntity anonymousRole;
  private ResourceEntity resourceNew;
  private ResourceEntity resourceNoneRole;
  private ResourceEntity resourceOneRole;
  private ResourceEntity resourceTwoRole;
  private RoleResourceEntity resourceRole;
  private RoleResourceEntity resourceRole2;
  private RoleResourceEntity resourceRole3;


  @BeforeEach
  void setUp(){
    adminRole = RoleEntity.builder()
        .roleName("ROLE_ADMIN")
        .roleDesc("ADMIN")
        .build();
    userRole = RoleEntity.builder()
        .roleName("ROLE_USER")
        .roleDesc("USER")
        .build();
    anonymousRole = RoleEntity.builder()
        .roleName("ROLE_ANONYMOUS")
        .roleDesc("ANONYMOUS")
        .build();

    resourceNew = ResourceEntity.builder()
        .resourceName("/test1")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(1)
        .build();
    resourceNoneRole = ResourceEntity.builder()
        .id(1L)
        .resourceName("/test1")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(1)
        .build();
    resourceOneRole = ResourceEntity.builder()
        .id(2L)
        .resourceName("/test2")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(2)
        .build();
    resourceTwoRole = ResourceEntity.builder()
        .id(3L)
        .resourceName("/test2")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(3)
        .build();

    resourceRole = RoleResourceEntity.builder()
        .resourceEntity(resourceOneRole)
        .roleEntity(anonymousRole)
        .build();
    resourceRole2 = RoleResourceEntity.builder()
        .resourceEntity(resourceTwoRole)
        .roleEntity(adminRole)
        .build();
    resourceRole3 = RoleResourceEntity.builder()
        .resourceEntity(resourceTwoRole)
        .roleEntity(userRole)
        .build();

  }

  @Test
  @DisplayName("findAllResources-case1")
  void 모든_리소스와_그에_해당하는_Role을_가져올_수_있다() {
    // given
    when(resourceJpaRepository.findAll())
        .thenReturn(List.of(resourceNoneRole, resourceOneRole, resourceTwoRole));
    when(roleResourceJpaRepository.findFetchAll())
        .thenReturn(List.of(resourceRole, resourceRole2, resourceRole3));

    // when
    List<Resource> allResources = resourceRepositoryImpl.findAllResources();
    Resource noneResource = allResources.stream()
        .filter(re -> re.getId() == resourceNoneRole.getId()).findFirst().get();
    Resource oneResource = allResources.stream()
        .filter(re -> re.getId() == resourceOneRole.getId()).findFirst().get();
    Resource twoResource = allResources.stream()
        .filter(re -> re.getId() == resourceTwoRole.getId()).findFirst().get();


    // then
    assertThat(allResources.size()).isEqualTo(3);
    assertThat(allResources.stream()
        .anyMatch(re -> re.getResourceName().equals(resourceNoneRole.getResourceName())))
        .isTrue();
    assertThat(noneResource.getRoleSet().size()).isEqualTo(0);
    assertThat(oneResource.getRoleSet().size()).isEqualTo(1);
    assertThat(twoResource.getRoleSet().size()).isEqualTo(2);

  }


  @Test
  @DisplayName("save-case1")
  void 신규저장시_Role이_없다면_문제없이_저장된다() {
    // given
    when(resourceJpaRepository.save(any(ResourceEntity.class)))
        .thenReturn(resourceNoneRole);
    Resource resource = resourceNew.toDomain();

    // when
    Resource savedResource = resourceRepositoryImpl.save(resource);

    // then
    assertThat(savedResource.getResourceName()).isEqualTo(resourceNoneRole.getResourceName());
    assertThat(savedResource.getResourceType()).isEqualTo(resourceNoneRole.getResourceType());
    assertThat(savedResource.getHttpMethod()).isEqualTo(resourceNoneRole.getHttpMethod());
  }

  @Test
  @DisplayName("save-case2")
  void 신규저장시_기존_Role에_존재하지_않는_Role이_있다면_예외를_반환한다() {
    // given
    when(resourceJpaRepository.save(any(ResourceEntity.class)))
        .thenReturn(resourceNoneRole);
    when(roleJpaRepository.findAllByRoleNameIn(anyList()))
        .thenReturn(List.of());
    Resource resource = resourceNew.toDomain();
    resource.getRoleSet().add(anonymousRole.toDomain());

    // when, then
    assertThatThrownBy(() -> resourceRepositoryImpl.save(resource))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage(ExceptionMessage.ROLE_NOT_FOUND);

  }

  @Test
  @DisplayName("save-case3")
  void 신규저장시_Role이_정상적으로_존재한다면_저장한다() {
    // given
    when(resourceJpaRepository.save(any(ResourceEntity.class)))
        .thenReturn(resourceNoneRole);
    when(roleJpaRepository.findAllByRoleNameIn(anyList()))
        .thenReturn(List.of(anonymousRole));
    Resource resource = resourceNew.toDomain();
    resource.getRoleSet().add(anonymousRole.toDomain());

    // when
    Resource savedResource = resourceRepositoryImpl.save(resource);

    assertThat(savedResource.getResourceName()).isEqualTo(resourceNoneRole.getResourceName());
    assertThat(savedResource.getResourceType()).isEqualTo(resourceNoneRole.getResourceType());
    assertThat(savedResource.getRoleSet().size()).isEqualTo(1);
    assertThat(savedResource.getRoleSet().stream()
        .anyMatch(re -> re.getRoleName().equals(anonymousRole.getRoleName()))).isTrue();

  }

  @Test
  @DisplayName("save-case4")
  void 수정시_Role이_존재하지_않는다면_예외를_반환한다() {
    // given
    when(resourceJpaRepository.save(any(ResourceEntity.class)))
        .thenReturn(resourceTwoRole);
    when(roleJpaRepository.findAllByRoleNameIn(anyList()))
        .thenReturn(List.of());
    Resource resource = resourceTwoRole.toDomain();
    resource.getRoleSet().addAll(Set.of(userRole.toDomain(), adminRole.toDomain()));

    // when, then
    assertThatThrownBy(() -> resourceRepositoryImpl.save(resource))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage(ExceptionMessage.ROLE_NOT_FOUND);

  }

  @Test
  @DisplayName("save-case5")
  void 수정시_Role이_정상적으로_존재한다면_저장한다() {
    // given
    when(resourceJpaRepository.save(any(ResourceEntity.class)))
        .thenReturn(resourceTwoRole);
    when(roleJpaRepository.findAllByRoleNameIn(anyList()))
        .thenReturn(List.of(userRole, adminRole));
    Resource resource = resourceTwoRole.toDomain();
    resource.getRoleSet().addAll(Set.of(userRole.toDomain(), adminRole.toDomain()));

    // when
    Resource savedResource = resourceRepositoryImpl.save(resource);

    // then
    assertThat(savedResource.getResourceName()).isEqualTo(resourceTwoRole.getResourceName());
    assertThat(savedResource.getResourceType()).isEqualTo(resourceTwoRole.getResourceType());
    assertThat(savedResource.getRoleSet().size()).isEqualTo(2);
    assertThat(savedResource.getRoleSet().stream()
        .anyMatch(re -> re.getRoleName().equals(userRole.getRoleName()))).isTrue();
    assertThat(savedResource.getRoleSet().stream()
        .anyMatch(re -> re.getRoleName().equals(adminRole.getRoleName()))).isTrue();

  }

}