package com.backend.accountmanagement.account.service;

import com.backend.accountmanagement.account.controller.port.ResourceService;
import com.backend.accountmanagement.account.domain.BannedIp;
import com.backend.accountmanagement.account.domain.Resource;
import com.backend.accountmanagement.account.service.port.BannedIpRepository;
import com.backend.accountmanagement.account.service.port.ResourceRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final BannedIpRepository bannedIpRepository;


  @Override
  public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {
    LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
    List<Resource> resourcesList = resourceRepository.findAllResources();

    resourcesList.forEach(resource -> {
      List<ConfigAttribute> configAttributeList = new ArrayList<>();
      resource.getRoleSet().forEach(role -> {
        configAttributeList.add(new SecurityConfig(role.getRoleName()));
      });
      result.put(new AntPathRequestMatcher(resource.getResourceName()), configAttributeList);
    });

    return result;
  }

  @Override
  public List<String> getBannedIpList() {
    List<BannedIp> all = bannedIpRepository.findAll();
    List<String> list = all.stream()
        .map(BannedIp::getIpAddress)
        .toList();

    return list;
  }
}
