package com.backend.accountmanagement.security.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.AccountSNS;
import com.backend.accountmanagement.account.domain.Role;
import com.backend.accountmanagement.account.domain.SNSProvider;
import com.backend.accountmanagement.account.service.port.AccountRepository;
import com.backend.accountmanagement.account.service.port.AccountSNSRepository;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import com.backend.accountmanagement.security.oauth.CustomOAuth2User;
import com.backend.accountmanagement.utils.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails.UserInfoEndpoint;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;


@ExtendWith({MockitoExtension.class})
class CustomOAuth2UserServiceTest {

  private AccountRepository accountRepository;
  private AccountSNSRepository accountSNSRepository;
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup(){
    SecurityTestContainer testContainer = new SecurityTestContainer(new MockClockHolder(new Date()), 0L);
    accountRepository = testContainer.accountRepository;
    accountSNSRepository = testContainer.accountSNSRepository;
    passwordEncoder = testContainer.passwordEncoder;
  }

  private OAuth2UserRequest createMockOAuth2UserRequest(String registrationId) {
    OAuth2UserRequest mockRequest = mock(OAuth2UserRequest.class);
    ClientRegistration clientRegistration = mock(ClientRegistration.class);
    ProviderDetails providerDetails = mock(ProviderDetails.class);
    UserInfoEndpoint userInfoEndpoint = mock(UserInfoEndpoint.class);

    when(mockRequest.getClientRegistration()).thenReturn(clientRegistration);
    when(clientRegistration.getRegistrationId()).thenReturn(registrationId);
    when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
    when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);

    return mockRequest;
  }

  private DefaultOAuth2UserService createMockOAuth2UserService(OAuth2User mockUser) {
    DefaultOAuth2UserService mockService = mock(DefaultOAuth2UserService.class);
    when(mockService.loadUser(any(OAuth2UserRequest.class))).thenReturn(mockUser);
    return mockService;
  }


  private OAuth2User createMockOAuth2User(Map<String, Object> attributes) {
    OAuth2User mockUser = mock(OAuth2User.class);
    when(mockUser.getAttributes()).thenReturn(attributes);
    return mockUser;
  }

  private Map<String, Object> getGoogleTestJson() throws JsonProcessingException {
    String content = "{\n"
        + "  \"sub\": \"103058387739722400130\",\n"
        + "  \"name\": \"구글\",\n"
        + "  \"given_name\": \"글\",\n"
        + "  \"family_name\": \"구\",\n"
        + "  \"picture\": \"google-picture.com\",\n"
        + "  \"email\": \"google@gmail.com\",\n"
        + "  \"email_verified\": true,\n"
        + "  \"locale\": \"ko\"\n"
        + "}";
    TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {};
    return MapperUtils.getMapper().readValue(content, type);

  }

  private Map<String, Object> getNaverTestJson() throws JsonProcessingException {
    String content = "{\n"
        + "  \"resultcode\": \"00\",\n"
        + "  \"message\": \"success\",\n"
        + "  \"response\": {\n"
        + "    \"email\": \"naver@gmail.com\",\n"
        + "    \"nickname\": \"네이버닉네임\",\n"
        + "    \"profile_image\": \"naver-picture.com\",\n"
        + "    \"age\": \"40-49\",\n"
        + "    \"gender\": \"F\",\n"
        + "    \"id\": \"32742776\",\n"
        + "    \"name\": \"네이버\",\n"
        + "    \"birthday\": \"10-01\"\n"
        + "  }\n"
        + "}";
    TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {};
    return MapperUtils.getMapper().readValue(content, type);

  }

  private Map<String, Object> getKakaoTestJson() throws JsonProcessingException {
    String content = "{\n"
        + "  \"aud\": \"${APP_KEY}\",\n"
        + "  \"sub\": \"${USER_ID}\",\n"
        + "  \"auth_time\": 1661967952,\n"
        + "  \"iss\": \"https://kauth.kakao.com\",\n"
        + "  \"exp\": 1661967972,\n"
        + "  \"iat\": 1661967952,\n"
        + "  \"name\": \"카카오\",\n"
        + "  \"nickname\": \"카카오닉네임\",\n"
        + "  \"picture\": \"kakao-picture.com\",\n"
        + "  \"email\": \"kakao@kakao.com\"\n"
        + "}";
    TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {};
    return MapperUtils.getMapper().readValue(content, type);

  }

  private void verifyAccountAndSnsData(String registrationId,
      String provider,
      CustomOAuth2User oAuth2User,
      Map<String, Object> attributes) {
    Account accountForToken = oAuth2User.getAccount();

    assertThat(oAuth2User.getName()).isEqualTo(attributes.get(registrationId));

    Optional<Account> savedAccount = accountRepository.findByEmail(accountForToken.getEmail());
    assertThat(savedAccount).isPresent();
    assertThat(savedAccount.get().getEmail()).isEqualTo(accountForToken.getEmail());
    assertThat(savedAccount.get().getName()).isEqualTo(accountForToken.getName());
    assertThat(savedAccount.get().getProfileImageUrl()).isEqualTo(accountForToken.getProfileImageUrl());

    Optional<AccountSNS> savedSNS = accountSNSRepository.findByEmailAndProvider(accountForToken.getEmail(), SNSProvider.valueOf(provider.toUpperCase()));
    assertThat(savedSNS).isPresent();
    assertThat(savedSNS.get().getEmail()).isEqualTo(accountForToken.getEmail());
    assertThat(savedSNS.get().getProvider()).isEqualTo(SNSProvider.valueOf(provider.toUpperCase()));
  }


  @Test
  void 구글로그인시_인증_정보가_DB에_존재하지_않는다면_회원가입을_실행한다() throws JsonProcessingException {
    // given
    String registrationId = "sub";
    String provider = "google";
    OAuth2UserRequest mockRequest = createMockOAuth2UserRequest(provider);
    Map<String, Object> googleTestJson = getGoogleTestJson();
    OAuth2User mockUser = createMockOAuth2User(googleTestJson);
    DefaultOAuth2UserService mockDefaultOAuth2UserService = createMockOAuth2UserService(mockUser);
    when(mockRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName()).thenReturn(registrationId);

    CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService(accountRepository, accountSNSRepository, passwordEncoder);
    customOAuth2UserService.setDelegate(mockDefaultOAuth2UserService);

    // when
    CustomOAuth2User oAuth2User = (CustomOAuth2User) customOAuth2UserService.loadUser(mockRequest);

    // then
    verifyAccountAndSnsData(registrationId, provider, oAuth2User, googleTestJson);
  }

  @Test
  void 구글로그인시_인증_정보가_DB에_존재한다면_해당계정_정보를_가져온다() throws JsonProcessingException {
    // given
    String registrationId = "sub";
    String provider = "google";
    OAuth2UserRequest mockRequest = createMockOAuth2UserRequest(provider);
    Map<String, Object> googleTestJson = getGoogleTestJson();
    OAuth2User mockUser = createMockOAuth2User(googleTestJson);
    DefaultOAuth2UserService mockDefaultOAuth2UserService = createMockOAuth2UserService(mockUser);
    when(mockRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName()).thenReturn(registrationId);

    Account savedAccount = Account.builder()
        .id(10)
        .name("test")
        .email((String) googleTestJson.get("email"))
        .profileImageUrl("profile")
        .build();

    Role role = Role.builder()
        .id(1)
        .roleName("ROLE_USER")
        .roleDesc("DEFAULT")
        .build();

    savedAccount.getRoleSet().add(role);

    AccountSNS savedSNS = AccountSNS.builder()
        .id(10)
        .email((String) googleTestJson.get("email"))
        .provider(SNSProvider.valueOf(provider.toUpperCase()))
        .providerAccountId("googleTestAccountId")
        .build();
    accountRepository.save(savedAccount);
    accountSNSRepository.save(savedSNS);

    CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService(accountRepository, accountSNSRepository, passwordEncoder);
    customOAuth2UserService.setDelegate(mockDefaultOAuth2UserService);

    // when
    CustomOAuth2User oAuth2User = (CustomOAuth2User) customOAuth2UserService.loadUser(mockRequest);

    // then
    assertThat(oAuth2User.getAuthorities().size()).isEqualTo(1);
    assertThat(oAuth2User.getAuthorities().stream()
        .filter(auth -> auth.getAuthority().equals(role.getRoleName())).findFirst()).isNotEmpty();

    Account account = oAuth2User.getAccount();
    assertThat(account.getId()).isEqualTo(10);
    assertThat(account.getName()).isEqualTo("test");
    assertThat(account.getEmail()).isEqualTo(googleTestJson.get("email"));



  }

  @Test
  void 네이버로그인시_인증_정보가_DB에_존재하지_않는다면_회원가입을_실행한다() throws JsonProcessingException {
    // given
    String registrationId = "id";
    String provider = "naver";
    OAuth2UserRequest mockRequest = createMockOAuth2UserRequest(provider);
    Map<String, Object> naverTestJson = getNaverTestJson();
    OAuth2User mockUser = createMockOAuth2User(naverTestJson);
    DefaultOAuth2UserService mockDefaultOAuth2UserService = createMockOAuth2UserService(mockUser);
    when(mockRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName()).thenReturn("id");

    CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService(accountRepository, accountSNSRepository, passwordEncoder);
    customOAuth2UserService.setDelegate(mockDefaultOAuth2UserService);

    // when
    CustomOAuth2User oAuth2User = (CustomOAuth2User) customOAuth2UserService.loadUser(mockRequest);

    // then
    verifyAccountAndSnsData(registrationId, provider, oAuth2User, (Map<String, Object>) naverTestJson.get("response"));
  }

  @Test
  void 네이버로그인시_인증_정보가_DB에_존재한다면_해당계정_정보를_가져온다() throws JsonProcessingException {
    // given
    String registrationId = "id";
    String provider = "naver";
    OAuth2UserRequest mockRequest = createMockOAuth2UserRequest(provider);
    Map<String, Object> naverTestJson = getNaverTestJson();
    Map<String, Object> response = (Map<String, Object>) naverTestJson.get("response");
    OAuth2User mockUser = createMockOAuth2User(naverTestJson);
    DefaultOAuth2UserService mockDefaultOAuth2UserService = createMockOAuth2UserService(mockUser);
    when(mockRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName()).thenReturn(registrationId);

    Account savedAccount = Account.builder()
        .id(10)
        .name("test")
        .email((String) response.get("email"))
        .profileImageUrl("profile")
        .build();

    Role role = Role.builder()
        .id(1)
        .roleName("ROLE_USER")
        .roleDesc("DEFAULT")
        .build();

    savedAccount.getRoleSet().add(role);

    AccountSNS savedSNS = AccountSNS.builder()
        .id(10)
        .email((String) response.get("email"))
        .provider(SNSProvider.valueOf(provider.toUpperCase()))
        .providerAccountId("naverTestAccountId")
        .build();
    accountRepository.save(savedAccount);
    accountSNSRepository.save(savedSNS);

    CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService(accountRepository, accountSNSRepository, passwordEncoder);
    customOAuth2UserService.setDelegate(mockDefaultOAuth2UserService);

    // when
    CustomOAuth2User oAuth2User = (CustomOAuth2User) customOAuth2UserService.loadUser(mockRequest);

    // then
    assertThat(oAuth2User.getAuthorities().size()).isEqualTo(1);
    assertThat(oAuth2User.getAuthorities().stream()
        .filter(auth -> auth.getAuthority().equals(role.getRoleName())).findFirst()).isNotEmpty();

    Account account = oAuth2User.getAccount();
    assertThat(account.getId()).isEqualTo(10);
    assertThat(account.getName()).isEqualTo("test");
    assertThat(account.getEmail()).isEqualTo(response.get("email"));

  }

  @Test
  void 카카오로그인시_인증_정보가_DB에_존재하지_않는다면_회원가입을_실행한다() throws JsonProcessingException {
    // given
    String registrationId = "sub";
    String provider = "kakao";
    OAuth2UserRequest mockRequest = createMockOAuth2UserRequest(provider);
    Map<String, Object> kakaoTestJson = getKakaoTestJson();
    OAuth2User mockUser = createMockOAuth2User(kakaoTestJson);
    DefaultOAuth2UserService mockDefaultOAuth2UserService = createMockOAuth2UserService(mockUser);
    when(mockRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName()).thenReturn(registrationId);

    CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService(accountRepository, accountSNSRepository, passwordEncoder);
    customOAuth2UserService.setDelegate(mockDefaultOAuth2UserService);

    // when
    CustomOAuth2User oAuth2User = (CustomOAuth2User) customOAuth2UserService.loadUser(mockRequest);

    // then
    verifyAccountAndSnsData(registrationId, provider, oAuth2User, kakaoTestJson);
  }


  @Test
  void 카카오로그인시_인증_정보가_DB에_존재한다면_해당계정_정보를_가져온다() throws JsonProcessingException {
    // given
    String registrationId = "sub";
    String provider = "kakao";
    OAuth2UserRequest mockRequest = createMockOAuth2UserRequest(provider);
    Map<String, Object> kakaoTestJson = getKakaoTestJson();
    OAuth2User mockUser = createMockOAuth2User(kakaoTestJson);
    DefaultOAuth2UserService mockDefaultOAuth2UserService = createMockOAuth2UserService(mockUser);
    when(mockRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName()).thenReturn(registrationId);

    Account savedAccount = Account.builder()
        .id(10)
        .name("test")
        .email((String) kakaoTestJson.get("email"))
        .profileImageUrl("profile")
        .build();

    Role role = Role.builder()
        .id(1)
        .roleName("ROLE_USER")
        .roleDesc("DEFAULT")
        .build();

    savedAccount.getRoleSet().add(role);

    AccountSNS savedSNS = AccountSNS.builder()
        .id(10)
        .email((String) kakaoTestJson.get("email"))
        .provider(SNSProvider.valueOf(provider.toUpperCase()))
        .providerAccountId("kakaoTestAccountId")
        .build();
    accountRepository.save(savedAccount);
    accountSNSRepository.save(savedSNS);

    CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService(accountRepository, accountSNSRepository, passwordEncoder);
    customOAuth2UserService.setDelegate(mockDefaultOAuth2UserService);

    // when
    CustomOAuth2User oAuth2User = (CustomOAuth2User) customOAuth2UserService.loadUser(mockRequest);

    // then
    assertThat(oAuth2User.getAuthorities().size()).isEqualTo(1);
    assertThat(oAuth2User.getAuthorities().stream()
        .filter(auth -> auth.getAuthority().equals(role.getRoleName())).findFirst()).isNotEmpty();

    Account account = oAuth2User.getAccount();
    assertThat(account.getId()).isEqualTo(10);
    assertThat(account.getName()).isEqualTo("test");
    assertThat(account.getEmail()).isEqualTo(kakaoTestJson.get("email"));

  }






}