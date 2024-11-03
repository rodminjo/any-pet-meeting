package com.backend.accountmanagement.account.infrastructure.jpa;

import com.backend.accountmanagement.account.infrastructure.entity.RoleResourceEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleResourceJpaRepository extends JpaRepository<RoleResourceEntity, Long> {
  @Query("SELECT rr "
      + "FROM RoleResourceEntity rr "
      + "LEFT JOIN FETCH rr.resourceEntity "
      + "LEFT JOIN FETCH rr.roleEntity")
  List<RoleResourceEntity> findFetchAll();

  void deleteAllByResourceEntityId(long id);
}
