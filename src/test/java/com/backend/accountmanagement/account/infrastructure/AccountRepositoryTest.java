package com.backend.accountmanagement.account.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.account.infrastructure.entity.AccountEntity;
import com.backend.accountmanagement.account.infrastructure.entity.AccountRoleEntity;
import com.backend.accountmanagement.account.infrastructure.entity.RoleEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.AccountJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.AccountRoleJpaRepository;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleJpaRepository;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.common.exception.ResourceNotConvertException;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AccountRepositoryTest {

  @Mock
  private AccountJpaRepository accountJpaRepository;

  @Mock
  private AccountRoleJpaRepository accountRoleJpaRepository;

  @Mock
  private RoleJpaRepository roleJpaRepository;

  @InjectMocks
  private AccountRepositoryImpl accountRepositoryImpl;

  private Account accountNew;
  private Account accountUpdate;
  private AccountEntity accountNewEntity;
  private AccountEntity accountUpdateEntity;
  private Role userRole;
  private Role adminRole;
  private RoleEntity userRoleEntity;
  private RoleEntity adminRoleEntity;
  private AccountRoleEntity accountRoleEntity;

  @BeforeEach
  public void setUp() {
    MockClockHolder mockClockHolder = new MockClockHolder(new Date());
    SecurityTestContainer testContainer = new SecurityTestContainer(mockClockHolder, 10000L);

    userRole = Role.builder()
        .roleName("ROLE_USER")
        .roleDesc("DEFAULT")
        .build();

    adminRole = Role.builder()
        .roleName("ROLE_ADMIN")
        .roleDesc("ADMIN")
        .build();

    accountNew = Account.builder()
        .id(0L)
        .name("test")
        .email("test@example.com")
        .password(testContainer.passwordEncoder.encode("testPassword"))
        .build();

    accountUpdate = Account.builder()
        .id(1L)
        .name("testUpdate")
        .email("testUpdate@example.com")
        .password(testContainer.passwordEncoder.encode("testPassword2"))
        .build();

    accountNew.getRoleSet().add(userRole);
    accountUpdate.getRoleSet().addAll(Set.of(userRole, adminRole));

    userRoleEntity = RoleEntity.from(userRole);
    adminRoleEntity = RoleEntity.from(adminRole);

    accountNewEntity = AccountEntity.from(accountNew);
    accountUpdateEntity = AccountEntity.from(accountUpdate);

    accountRoleEntity = AccountRoleEntity.builder()
        .accountEntity(accountNewEntity)
        .roleEntity(userRoleEntity)
        .build();
  }


  @Test
  void 이메일로_계정과_권한을_찾아올_수_있다() {
    // given
    when(accountJpaRepository.findByEmail(accountUpdate.getEmail()))
        .thenReturn(Optional.of(accountUpdateEntity));
    when(accountRoleJpaRepository.findAllByAccountEntityEmail(accountUpdate.getEmail()))
        .thenReturn(Collections.singletonList(accountRoleEntity));

    // when
    Optional<Account> findAccount = accountRepositoryImpl.findByEmail(accountUpdate.getEmail());

    // then
    assertThat(findAccount.isPresent()).isTrue();
    assertThat(findAccount.get().getName()).isEqualTo(accountUpdate.getName());
    assertThat(findAccount.get().getPassword()).isEqualTo(accountUpdate.getPassword());
    assertThat(findAccount.get().getRoleSet().size()).isEqualTo(1);
    assertThat(findAccount.get().getRoleSet().stream().findFirst().get().getRoleName())
        .isEqualTo(userRole.getRoleName());
  }

  @Test
  void 이메일로_계정을_찾지_못한다면_빈값을_반환한다() {
    // given
    when(accountJpaRepository.findByEmail(anyString()))
        .thenReturn(Optional.empty());

    // when
    Optional<Account> findAccount = accountRepositoryImpl.findByEmail(accountNew.getEmail());

    // then
    assertThat(findAccount.isEmpty()).isTrue();
  }

  @Test
  void 이메일이_존재하는지_확인할_수_있다() {
    // given
    when(accountJpaRepository.existsByEmail(accountUpdate.getEmail()))
        .thenReturn(true);

    // when
    boolean result = accountRepositoryImpl.existByEmail(accountUpdate.getEmail());

    assertThat(result).isTrue();
  }

  @Test
  void 계정_신규저장시_Account에_기존에_들어있지_않은_Role이_있으면_예외를_발생시킨다() {
    when(accountJpaRepository.save(any(AccountEntity.class)))
        .thenReturn(accountNewEntity);
    when(roleJpaRepository.findAllByRoleNameIn(anyList()))
        .thenReturn(Collections.emptyList());

    assertThatThrownBy(() -> accountRepositoryImpl.save(accountNew))
        .isExactlyInstanceOf(ResourceNotConvertException.class)
        .hasMessage(ExceptionMessage.ROLE_NOT_FOUND);
  }

  @Test
  void 계정_신규저장시_Role이_정상적으로_존재한다면_저장한다() {
    // given
    when(accountJpaRepository.save(any(AccountEntity.class)))
        .thenReturn(accountNewEntity);
    when(roleJpaRepository.findAllByRoleNameIn(anyList()))
        .thenReturn(Collections.singletonList(userRoleEntity));
    when(accountRoleJpaRepository.saveAll(anyList()))
        .thenReturn(Collections.singletonList(accountRoleEntity));

    // when
    Account savedAccount = accountRepositoryImpl.save(accountNew);

    // then
    assertThat(savedAccount).isNotNull();
    assertThat(savedAccount.getName()).isEqualTo(accountNewEntity.getName());
    assertThat(savedAccount.getEmail()).isEqualTo(accountNewEntity.getEmail());
    assertThat(savedAccount.getRoleSet().stream()
        .anyMatch(r -> "ROLE_USER".equals(r.getRoleName()))).isTrue();
  }

  @Test
  public void 계정_수정시_Role이_정상적으로_존재하지_않는다면_예외를_발생시킨다() {
    // given
    when(accountJpaRepository.save(any(AccountEntity.class)))
        .thenReturn(accountUpdateEntity);
    when(roleJpaRepository.findAllByRoleNameIn(anyList()))
        .thenReturn(Collections.singletonList(userRoleEntity));

    // when, then
    assertThatThrownBy(() -> accountRepositoryImpl.save(accountUpdate))
        .isExactlyInstanceOf(ResourceNotConvertException.class)
        .hasMessage(ExceptionMessage.ROLE_NOT_FOUND);

  }

  @Test
  public void 계정_수정시_Role이_정상적으로_존재한다면_저장한다() {
    when(accountJpaRepository.save(any(AccountEntity.class)))
        .thenReturn(accountUpdateEntity);
    when(roleJpaRepository.findAllByRoleNameIn(anyList()))
        .thenReturn(List.of(userRoleEntity, adminRoleEntity));
    doNothing().when(accountRoleJpaRepository).deleteAllByAccountEntityId(anyLong());
    when(accountRoleJpaRepository.saveAll(anyList()))
        .thenReturn(Collections.singletonList(accountRoleEntity));

    Account savedAccount = accountRepositoryImpl.save(accountUpdate);

    // then
    assertThat(savedAccount).isNotNull();
    assertThat(savedAccount.getName()).isEqualTo(accountUpdateEntity.getName());
    assertThat(savedAccount.getEmail()).isEqualTo(accountUpdateEntity.getEmail());
    assertThat(savedAccount.getRoleSet().stream()
        .anyMatch(r -> "ROLE_USER".equals(r.getRoleName()))).isTrue();
  }


}