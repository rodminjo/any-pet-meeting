package com.backend.accountmanagement.account.infrastructure;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.backend.accountmanagement.account.domain.BannedIp;
import com.backend.accountmanagement.account.infrastructure.entity.BannedIpEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.BannedIpJpaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class BannedIpRepositoryTest {

  @Mock
  private BannedIpJpaRepository bannedIpJpaRepository;

  @InjectMocks
  private BannedIpRepositoryImpl accessIpRepository;

  private BannedIpEntity localIp;
  private BannedIpEntity etcIp;

  @BeforeEach
  void setUp() {
    localIp = BannedIpEntity.builder()
        .id(1)
        .ipAddress("127.0.0.1")
        .build();

    etcIp = BannedIpEntity.builder()
        .id(1)
        .ipAddress("122.0.0.1")
        .build();
  }

  @Test
  void 모든_AccessIp를_가져온다(){
    //given
    when(bannedIpJpaRepository.findAll())
        .thenReturn(List.of(localIp, etcIp));

    // when
    List<BannedIp> all = accessIpRepository.findAll();

    // then
    assertThat(all.size()).isEqualTo(2);
    assertThat(all.get(0).getIpAddress()).isEqualTo(localIp.getIpAddress());
    assertThat(all.get(1).getIpAddress()).isEqualTo(etcIp.getIpAddress());
  }

  @Test
  void AccessIp를_저장한다(){
    //given
    when(bannedIpJpaRepository.save(any(BannedIpEntity.class)))
        .thenReturn(localIp);

    // when
    BannedIp savedBannedIp = accessIpRepository.save(localIp.toDomain());

    // then
    assertThat(savedBannedIp.getId()).isEqualTo(localIp.getId());
    assertThat(savedBannedIp.getIpAddress()).isEqualTo(localIp.getIpAddress());
  }

}