package com.backend.accountmanagement.security.oauth.service;


import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.AccountCreate;
import com.backend.accountmanagement.account.domain.AccountSNS;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.account.domain.SNSProvider;
import com.backend.accountmanagement.account.service.port.AccountRepository;
import com.backend.accountmanagement.account.service.port.AccountSNSRepository;
import com.backend.accountmanagement.security.oauth.CustomOAuth2User;
import com.backend.accountmanagement.security.oauth.dto.OAuthAttributesDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final AccountRepository accountRepository;
  private final AccountSNSRepository accountSNSRepository;
  private final PasswordEncoder passwordEncoder;

  @Setter
  private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    /* OAuth2 서비스 구분코드 (구글, 카카오, 네이버) */
    String registrationId = userRequest
        .getClientRegistration()
        .getRegistrationId();


    /* OAuth2 로그인 진행시 키가 되는 필드값(PK), 구글의 기본코드는 sub, 네이버는 이 메서드로 보이지 않아 직접 입력 필요 */
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName();

    /* OAuthAttributes 객체 생성 */
    OAuthAttributesDto attributes = OAuthAttributesDto
        .of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

    Account account = doLoginOrJoin(attributes, registrationId);
    List<String> roleList = account.getRoleSet().stream()
        .map(Role::getRoleName)
        .toList();

    return new CustomOAuth2User(
        account,
        AuthorityUtils.createAuthorityList(roleList),
        attributes.getAttributes(),
        attributes.getNameAttributeKey()
    );

  }

  public Account doLoginOrJoin(OAuthAttributesDto attributes, String registrationId) {
    AccountSNS accountSNS = findOrCreateAccountSNS(attributes, registrationId);
    Account account = findOrCreateAccount(accountSNS.getEmail(), attributes);
    return account;
  }

  private Account findOrCreateAccount(String email, OAuthAttributesDto attributes) {
    return accountRepository.findByEmail(email)
        .orElseGet(() -> {
          AccountCreate accountCreate = attributes.toAccountCreateDomain();
          Account newAccount = accountCreate.of(passwordEncoder);
          return accountRepository.save(newAccount);
        });
  }

  private AccountSNS findOrCreateAccountSNS(OAuthAttributesDto attributes, String registrationId) {
    return accountSNSRepository.findByEmailAndProvider(attributes.getEmail(), SNSProvider.valueOf(registrationId.toUpperCase()))
        .orElseGet(() -> {
          AccountSNS accountSNS = attributes.toAccountSNSDomain();
          return accountSNSRepository.save(accountSNS);
        });
  }


}
