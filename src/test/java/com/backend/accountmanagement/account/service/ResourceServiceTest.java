package com.backend.accountmanagement.account.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.accountmanagement.account.controller.port.ResourceService;
import com.backend.accountmanagement.account.domain.BannedIp;
import com.backend.accountmanagement.account.domain.Resource;
import com.backend.accountmanagement.mock.SecurityTestContainer;
import com.backend.accountmanagement.mock.utils.MockClockHolder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

class ResourceServiceTest {

  private ResourceService resourceService;

  @BeforeEach
  void setUp() {
    MockClockHolder mockClockHolder = new MockClockHolder(new Date());
    SecurityTestContainer testContainer = new SecurityTestContainer(mockClockHolder, 10000L);
    resourceService = new ResourceServiceImpl(testContainer.resourceRepository, testContainer.bannedIpRepository);

    Resource resource1 = Resource.builder()
        .id(0L)
        .resourceName("/test1")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(1)
        .build();
    Resource resource2 = Resource.builder()
        .id(0L)
        .resourceName("/test2")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(2)
        .build();
    Resource resource3 = Resource.builder()
        .id(0L)
        .resourceName("/test3")
        .resourceType("url")
        .httpMethod(HttpMethod.GET.name())
        .orderNum(3)
        .build();

    resource1.getRoleSet().add(testContainer.role);
    resource2.getRoleSet().add(testContainer.role);
    resource3.getRoleSet().add(testContainer.role);

    testContainer.resourceRepository.save(resource1);
    testContainer.resourceRepository.save(resource2);
    testContainer.resourceRepository.save(resource3);

    BannedIp localIp = BannedIp.builder()
        .id(1)
        .ipAddress("127.0.0.1")
        .build();

    BannedIp etcIp = BannedIp.builder()
        .id(2)
        .ipAddress("122.0.0.1")
        .build();

    testContainer.bannedIpRepository.save(localIp);
    testContainer.bannedIpRepository.save(etcIp);
  }

  @Test
  void DB에서_Resource를_전부_불러와_RequestMatcher로_변환한다(){

    // when
    LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceList = resourceService.getResourceList();
    MockHttpServletRequest request1 = new MockHttpServletRequest(HttpMethod.GET.name(), "/test1");
    request1.setServletPath("/test1");

    Entry<RequestMatcher, List<ConfigAttribute>> requestEntry = resourceList.entrySet().stream()
        .filter(entry -> entry.getKey().matches(request1))
        .findFirst()
        .orElse(null);


    // then
    assertThat(resourceList.size()).isEqualTo(3);
    assertThat(requestEntry).isNotNull();
    assertThat(requestEntry.getValue().size()).isEqualTo(1);
    assertThat(requestEntry.getValue().stream()
        .filter(config -> config.getAttribute().equals("ROLE_USER")).findFirst()).isNotEmpty();
  }

  @Test
  void DB에서_IP를_전부_불러와_String_리스트로_반환한다(){
    // when
    List<String> accessIpList = resourceService.getBannedIpList();

    // then
    assertThat(accessIpList.size()).isEqualTo(2);
    assertThat(accessIpList.stream().filter(val -> val.equals("127.0.0.1")).findFirst()).isNotNull();
    assertThat(accessIpList.stream().filter(val -> val.equals("122.0.0.1")).findFirst()).isNotNull();

  }
}