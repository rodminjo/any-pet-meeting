package com.backend.accountmanagement.security.authorization.factory;

import com.backend.accountmanagement.account.controller.port.ResourceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BannedIpMapFactoryBean implements FactoryBean<List<String>> {


  private final ResourceService resourceService;

  private List<String> ipList;

  public void init() {
    ipList = resourceService.getBannedIpList();
  }

  public List<String> getObject() {
    if (ipList == null) {
      init();
    }

    return ipList;
  }

  public void reload(){
    ipList.clear();
    ipList.addAll(resourceService.getBannedIpList());
  }

  @Override
  public Class<List> getObjectType() {
    return List.class;
  }

  @Override
  public boolean isSingleton() {
    return FactoryBean.super.isSingleton();
  }
}
