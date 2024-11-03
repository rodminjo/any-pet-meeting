package com.backend.accountmanagement.account.service.port;

import com.backend.accountmanagement.account.domain.Resource;
import java.util.List;

public interface ResourceRepository {

  List<Resource> findAllResources();

  Resource save(Resource resource);
}
