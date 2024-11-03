package com.backend.accountmanagement.account.controller.port;

import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

public interface ResourceService {

  LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList();

  List<String> getBannedIpList();
}
