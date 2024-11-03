package com.backend.accountmanagement.mock;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.security.jwt.service.AccountContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class FakeUserDetailService implements UserDetailsService {

  private final Account account;
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    List<SimpleGrantedAuthority> roles = account.getRoleSet().stream()
        .map(role -> new SimpleGrantedAuthority(role.getRoleName())).toList();
    return new AccountContext(account, roles);
  }
}
