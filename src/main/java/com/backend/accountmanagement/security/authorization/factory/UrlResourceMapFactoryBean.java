package com.backend.accountmanagement.security.authorization.factory;

import com.backend.accountmanagement.account.controller.port.ResourceService;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlResourceMapFactoryBean implements FactoryBean<LinkedHashMap<RequestMatcher, List<ConfigAttribute>>> {


  private final ResourceService resourceService;

  private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourcesMap;

  public void init() {
    resourcesMap = resourceService.getResourceList();
  }

  public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getObject() {
    if (resourcesMap == null) {
      init();
    }
    return resourcesMap;
  }

  // 추후 갱신하기 위한 메서드
  public void reload(){
    resourcesMap.clear();
    resourcesMap.putAll(resourceService.getResourceList());
  }

  public Class<LinkedHashMap> getObjectType() {
    return LinkedHashMap.class;
  }

  public boolean isSingleton() {
    return true;
  }
}
