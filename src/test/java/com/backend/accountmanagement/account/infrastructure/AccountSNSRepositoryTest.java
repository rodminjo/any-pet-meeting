package com.backend.accountmanagement.account.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.backend.accountmanagement.account.domain.AccountSNS;
import com.backend.accountmanagement.account.domain.SNSProvider;
import com.backend.accountmanagement.account.infrastructure.entity.AccountSNSEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.AccountSNSJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class AccountSNSRepositoryTest {

  @Mock
  private AccountSNSJpaRepository accountSNSJpaRepository;

  @InjectMocks
  private AccountSNSRepositoryImpl accountSNSRepositoryImpl;


  private AccountSNSEntity accountSNSEntity;
  private AccountSNSEntity newAccountSNSEntity;
  @BeforeEach
  void setUp() {
    accountSNSEntity = AccountSNSEntity.builder()
        .id(1)
        .email("test@naver.com")
        .provider(SNSProvider.NAVER)
        .providerAccountId("naver1")
        .build();

    newAccountSNSEntity = AccountSNSEntity.builder()
        .id(1)
        .email("testNew@naver.com")
        .provider(SNSProvider.GOOGLE)
        .providerAccountId("google1")
        .build();

  }


  @Test
  @DisplayName("SNS에서 provider와 email이 맞는 데이터를 가져온다")
  void findByEmailAndProvider_case1(){
    when(accountSNSJpaRepository.findByEmailAndProvider("test@naver.com", SNSProvider.NAVER))
        .thenReturn(Optional.ofNullable(accountSNSEntity));

    Optional<AccountSNS> find = accountSNSRepositoryImpl.findByEmailAndProvider("test@naver.com", SNSProvider.NAVER);

    assertThat(find).isPresent();
    assertThat(find.get().getId()).isEqualTo(1);
    assertThat(find.get().getEmail()).isEqualTo("test@naver.com");
    assertThat(find.get().getProvider()).isEqualTo(SNSProvider.NAVER);
    assertThat(find.get().getProviderAccountId()).isEqualTo("naver1");

  }


  @Test
  @DisplayName("SNS를 저장한다")
  void save_case1(){
    when(accountSNSJpaRepository.save(any(AccountSNSEntity.class))).thenReturn(newAccountSNSEntity);

    AccountSNS domain = newAccountSNSEntity.toDomain();
    AccountSNS savedSNS = accountSNSRepositoryImpl.save(domain);

    assertThat(savedSNS.getId()).isEqualTo(domain.getId());
    assertThat(savedSNS.getEmail()).isEqualTo(domain.getEmail());
    assertThat(savedSNS.getProvider()).isEqualTo(domain.getProvider());
    assertThat(savedSNS.getProviderAccountId()).isEqualTo(domain.getProviderAccountId());

  }

}