package com.backend.accountmanagement.account.infrastructure;

import com.backend.accountmanagement.account.domain.Resource;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.account.infrastructure.entity.ResourceEntity;
import com.backend.accountmanagement.account.infrastructure.entity.RoleEntity;
import com.backend.accountmanagement.account.infrastructure.entity.RoleResourceEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.ResourceJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleResourceJpaRepository;
import com.backend.accountmanagement.account.service.port.ResourceRepository;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

  private final ResourceJpaRepository resourceJpaRepository;
  private final RoleResourceJpaRepository roleResourceJpaRepository;
  private final RoleJpaRepository roleJpaRepository;


  @Override
  public List<Resource> findAllResources() {
    List<Resource> allResources = resourceJpaRepository.findAll().stream()
        .map(ResourceEntity::toDomain)
        .toList();

    roleResourceJpaRepository.findFetchAll().forEach(roleResource->{
      for (Resource re : allResources){
        if (re.getId() == roleResource.getResourceEntity().getId()){
          re.getRoleSet().add(roleResource.getRoleEntity().toDomain());
          return;
        }
      }
    });

    return allResources;

  }

  @Override
  public Resource save(Resource resource) {
    ResourceEntity savedEntity = resourceJpaRepository.save(ResourceEntity.from(resource));
    List<RoleEntity> roleEntityList = this.findRoleEntities(resource.getRoleSet());
    updateRoleResource(resource, savedEntity, roleEntityList);

    Resource savedResource = savedEntity.toDomain();
    Set<Role> roleSet = roleEntityList.stream().map(RoleEntity::toDomain).collect(Collectors.toSet());
    savedResource.getRoleSet().addAll(roleSet);

    return savedResource;

  }

  private List<RoleEntity> findRoleEntities(Set<Role> roles) {
    List<String> roleNames = roles.stream()
        .map(Role::getRoleName)
        .toList();

    List<RoleEntity> roleEntities = roleJpaRepository.findAllByRoleNameIn(roleNames);

    if (roleEntities.size() != roleNames.size()) {
      throw new IllegalArgumentException(ExceptionMessage.ROLE_NOT_FOUND);
    }

    return roleEntities;
  }

  private void updateRoleResource(Resource resource, ResourceEntity savedEntity, List<RoleEntity> roleEntities) {
    if (resource.getId() > 0L) {
      roleResourceJpaRepository.deleteAllByResourceEntityId(resource.getId());
    }

    List<RoleResourceEntity> roleResourceEntities = roleEntities.stream()
        .map(roleEntity -> RoleResourceEntity.builder()
            .roleEntity(roleEntity)
            .resourceEntity(savedEntity)
            .build()
        )
        .toList();

    roleResourceJpaRepository.saveAll(roleResourceEntities);
  }
}
