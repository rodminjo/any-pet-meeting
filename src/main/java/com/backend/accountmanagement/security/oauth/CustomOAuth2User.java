package com.backend.accountmanagement.security.oauth;

import com.backend.accountmanagement.account.domain.Account;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

  private Account account;

  public CustomOAuth2User(
      Account account,
      Collection<? extends GrantedAuthority> authorities,
      Map<String, Object> attributes,
      String nameAttributeKey) {
    super(authorities, attributes, nameAttributeKey);
    this.account = account;
  }
}
