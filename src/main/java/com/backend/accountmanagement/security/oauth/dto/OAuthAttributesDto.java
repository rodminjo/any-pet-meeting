package com.backend.accountmanagement.security.oauth.dto;


import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.domain.AccountCreate;
import com.backend.accountmanagement.account.domain.AccountSNS;
import com.backend.accountmanagement.account.domain.SNSProvider;
import com.backend.accountmanagement.common.exception.ExceptionMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributesDto {

  // 고유인증번호
  private String nameAttributeKey;

  // 이름
  private String name;

  // 이메일
  private String email;

  // 프로필사진
  private String picture;

  private SNSProvider provider;

  // 모든 데이터들
  private Map<String, Object> attributes;

  @Builder
  public OAuthAttributesDto(String nameAttributeKey, String name, String email, String picture,
      SNSProvider provider, Map<String, Object> attributes) {
    this.nameAttributeKey = nameAttributeKey;
    this.name = name;
    this.email = email;
    this.picture = picture;
    this.provider = provider;
    this.attributes = attributes;
  }


  public AccountCreate toAccountCreateDomain() {
    return AccountCreate.builder()
        .name(name)
        .email(email)
        .profileImageUrl(picture)
        .build();

  }

  public AccountSNS toAccountSNSDomain() {
    return AccountSNS.builder()
        .email(email)
        .provider(provider)
        .providerAccountId(nameAttributeKey)
        .build();

  }


  /* 구글, 네이버 판단하기 위한 메소드 */
  public static OAuthAttributesDto of(String registrationId, String userNameAttributeName,
      Map<String, Object> attributes){

    /* 네이버의 경우 구글과 달리 userNameAttributeName은 response, 이걸 id로 바꿔주기 위해 직접 입력 */
    return switch (registrationId) {
      case "google" -> ofGoogle(userNameAttributeName, registrationId, attributes);
      case "naver" -> ofNaver("id", registrationId, attributes);
      case "kakao" -> ofKakao(userNameAttributeName, registrationId, attributes);
      default -> throw new IllegalStateException(ExceptionMessage.INVALID_OAUTH_PROVIDER);
    };

  }

  private static OAuthAttributesDto ofNaver(String userNameAttributeName,
      String registrationId,
      Map<String, Object> attributes) {
    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
    return OAuthAttributesDto.builder()
        .name((String) response.get("name"))
        .email((String) response.get("email"))
        .picture((String) response.get("profile_image"))
        .attributes(response)
        .provider(SNSProvider.valueOf(registrationId.toUpperCase()))
        .nameAttributeKey(userNameAttributeName)
        .build();

  }

  private static OAuthAttributesDto ofGoogle(String userNameAttributeName,
      String registrationId,
      Map<String, Object> attributes){
    return OAuthAttributesDto.builder()
        .name((String) attributes.get("name"))
        .email((String) attributes.get("email"))
        .picture((String) attributes.get("picture"))
        .attributes(attributes)
        .provider(SNSProvider.valueOf(registrationId.toUpperCase()))
        .nameAttributeKey(userNameAttributeName)
        .build();

  }

  private static OAuthAttributesDto ofKakao(String userNameAttributeName,
      String registrationId,
      Map<String, Object> attributes) {
    return OAuthAttributesDto.builder()
        .name((String) attributes.get("name"))
        .email((String) attributes.get("email"))
        .picture((String) attributes.get("picture"))
        .attributes(attributes)
        .provider(SNSProvider.valueOf(registrationId.toUpperCase()))
        .nameAttributeKey(userNameAttributeName)
        .build();

  }


}

