package com.backend.accountmanagement.account.infrastructure.jpa;

import com.backend.accountmanagement.account.infrastructure.entity.AccountRoleEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRoleJpaRepository extends JpaRepository<AccountRoleEntity, Long> {

  @Query("SELECT ar "
      + "FROM AccountRoleEntity ar "
      + "LEFT JOIN FETCH ar.accountEntity "
      + "LEFT JOIN FETCH ar.roleEntity "
      + "WHERE ar.accountEntity.email = ?1")
  List<AccountRoleEntity> findAllByAccountEntityEmail(String email);

  void deleteAllByAccountEntityId(long id);
}
