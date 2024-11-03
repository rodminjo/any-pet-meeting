package com.backend.accountmanagement.account.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.backend.accountmanagement.account.domain.RefreshToken;
import com.backend.accountmanagement.account.infrastructure.entity.RefreshTokenEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.RefreshTokenJpaRepository;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.common.exception.ResourceNotFoundException;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class RefreshTokenRepositoryTest {

  @Mock
  RefreshTokenJpaRepository refreshTokenJpaRepository;

  @InjectMocks
  RefreshTokenRepositoryImpl refreshTokenRepositoryImpl;

  private RefreshTokenEntity refreshTokenEntity;
  private RefreshToken refreshToken;

  @BeforeEach
  void setUp() {
    MockClockHolder clockHolder = new MockClockHolder(new Date());
    SecurityTestContainer testContainer = new SecurityTestContainer(clockHolder, 10000L);

    refreshTokenEntity = RefreshTokenEntity.builder()
        .id(0L)
        .email("test@example.com")
        .refreshToken("testRefreshToken")
        .build();

    refreshToken = refreshTokenEntity.toDomain();
  }

  @Test
  @DisplayName("findByEmail-case1")
  void 이메일로_refreshToken을_찾아올_수_있다(){
    when(refreshTokenJpaRepository.findByEmail(refreshToken.getEmail()))
        .thenReturn(Optional.of(refreshTokenEntity));

    Optional<RefreshToken> find = refreshTokenRepositoryImpl.findByEmail(refreshToken.getEmail());
    assertThat(find.isPresent()).isTrue();
    assertThat(find.get().getEmail()).isEqualTo(refreshToken.getEmail());
    assertThat(find.get().getRefreshToken()).isEqualTo(refreshToken.getRefreshToken());
  }

  @Test
  @DisplayName("findByEmail-case2")
  void 이메일로_찾을_수_없다면_빈값을_반환한다(){
    // given
    when(refreshTokenJpaRepository.findByEmail(refreshToken.getEmail()))
        .thenReturn(Optional.empty());

    // when
    Optional<RefreshToken> find = refreshTokenRepositoryImpl.findByEmail(refreshToken.getEmail());

    // then
    assertThat(find.isEmpty()).isTrue();
  }


  @Test
  @DisplayName("getByEmail-case1")
  void 이메일로_찾을_수_없다면_에외를_반환한다(){
    // given
    when(refreshTokenJpaRepository.findByEmail(refreshToken.getEmail()))
        .thenReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> refreshTokenRepositoryImpl.getByEmail(refreshToken.getEmail()))
        .isExactlyInstanceOf(ResourceNotFoundException.class)
        .hasMessage(ExceptionMessage.REFRESH_TOKEN_NOT_FOUND);

  }

  @Test
  @DisplayName("findByRefreshToken-case1")
  void 토큰으로_refreshToken을_찾아올_수_있다(){
    when(refreshTokenJpaRepository.findByRefreshToken(refreshToken.getRefreshToken()))
        .thenReturn(Optional.of(refreshTokenEntity));

    Optional<RefreshToken> find = refreshTokenRepositoryImpl
        .findByRefreshToken(refreshToken.getRefreshToken());
    assertThat(find.isPresent()).isTrue();
    assertThat(find.get().getEmail()).isEqualTo(refreshToken.getEmail());
    assertThat(find.get().getRefreshToken()).isEqualTo(refreshToken.getRefreshToken());
  }

  @Test
  @DisplayName("findByRefreshToken-case2")
  void 토큰으로_찾을_수_없다면_빈값을_반환한다(){
    // given
    when(refreshTokenJpaRepository.findByRefreshToken(refreshToken.getRefreshToken()))
        .thenReturn(Optional.empty());

    // when
    Optional<RefreshToken> find = refreshTokenRepositoryImpl
        .findByRefreshToken(refreshToken.getRefreshToken());

    // then
    assertThat(find.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("save-case1")
  void 리프레시토큰으로_저장할_수_있다(){
    // given
    when(refreshTokenJpaRepository.save(any(RefreshTokenEntity.class)))
        .thenReturn(refreshTokenEntity);

    // when
    RefreshToken savedRefreshToken = refreshTokenRepositoryImpl.save(refreshToken);

    // then
    assertThat(savedRefreshToken).isNotNull();
    assertThat(savedRefreshToken.getEmail()).isEqualTo(refreshToken.getEmail());
    assertThat(savedRefreshToken.getRefreshToken()).isEqualTo(refreshToken.getRefreshToken());
  }







}