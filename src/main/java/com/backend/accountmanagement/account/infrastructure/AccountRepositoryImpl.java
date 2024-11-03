package com.backend.accountmanagement.account.infrastructure;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.account.infrastructure.entity.AccountEntity;
import com.backend.accountmanagement.account.infrastructure.entity.AccountRoleEntity;
import com.backend.accountmanagement.account.infrastructure.entity.RoleEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.AccountJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.AccountRoleJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleJpaRepository;
import com.backend.accountmanagement.account.service.port.AccountRepository;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.common.exception.ResourceNotConvertException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

  private final AccountJpaRepository accountJpaRepository;
  private final AccountRoleJpaRepository accountRoleJpaRepository;
  private final RoleJpaRepository roleJpaRepository;


  @Override
  public Optional<Account> findByEmail(String email) {
    Optional<Account> findAccount = accountJpaRepository.findByEmail(email)
        .map(AccountEntity::toDomain);

    if (findAccount.isPresent()) {
      Set<Role> roleSet = this.getRolesByEmail(email);
      findAccount.ifPresent(account -> account.getRoleSet().addAll(roleSet));
    }

    return findAccount;

  }


  @Override
  public boolean existByEmail(String email) {
    return accountJpaRepository.existsByEmail(email);

  }


  @Override
  public Account save(Account account) {
    AccountEntity savedAccountEntity = accountJpaRepository.save(AccountEntity.from(account));
    List<RoleEntity> findRoleList = this.findRoleEntities(account.getRoleSet());
    this.updateAccountRoles(account, savedAccountEntity, findRoleList);

    Account savedAccount = savedAccountEntity.toDomain();
    Set<Role> roleSet = findRoleList.stream().map(RoleEntity::toDomain).collect(Collectors.toSet());
    savedAccount.getRoleSet().addAll(roleSet);

    return savedAccount;

  }

  private Set<Role> getRolesByEmail(String email) {
    List<AccountRoleEntity> accountRoleEntities = accountRoleJpaRepository.findAllByAccountEntityEmail(
        email);
    return accountRoleEntities.stream()
        .map(ar -> ar.getRoleEntity().toDomain())
        .collect(Collectors.toSet());

  }

  private List<RoleEntity> findRoleEntities(Set<Role> roles) {
    List<String> roleNames = roles.stream()
        .map(Role::getRoleName)
        .collect(Collectors.toList());

    List<RoleEntity> roleEntities = roleJpaRepository.findAllByRoleNameIn(roleNames);

    if (roleEntities.size() != roleNames.size()) {
      throw new ResourceNotConvertException(ExceptionMessage.ROLE_NOT_FOUND);
    }

    return roleEntities;
  }

  private void updateAccountRoles(Account account, AccountEntity savedAccountEntity, List<RoleEntity> roleEntities) {
    if (account.getId() > 0L) {
      accountRoleJpaRepository.deleteAllByAccountEntityId(account.getId());
    }

    List<AccountRoleEntity> accountRoleEntities = roleEntities.stream()
        .map(roleEntity -> AccountRoleEntity.builder()
                .accountEntity(savedAccountEntity)
                .roleEntity(roleEntity)
                .build()
        )
        .collect(Collectors.toList());

    accountRoleJpaRepository.saveAll(accountRoleEntities);
  }


}
