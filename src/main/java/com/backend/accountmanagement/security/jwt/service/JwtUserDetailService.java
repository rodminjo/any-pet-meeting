package com.backend.accountmanagement.security.jwt.service;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.account.service.port.AccountRepository;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailService implements UserDetailsService {

  private final AccountRepository accountRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 계정 정보 찾기
    Account account = accountRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessage.ACCOUNT_NOT_FOUND));


    // 권한정보 작성
    List<GrantedAuthority> roles = !account.getRoleSet().isEmpty() ?
        createAuthorityList(
            account.getRoleSet().stream()
            .map(Role::getRoleName)
            .toList()
        ) :
        List.of();


    // account -> userDetails 형태로 변환해서 보내주기
    AccountContext accountContext = new AccountContext(account, roles);

    return accountContext;
  }
}
