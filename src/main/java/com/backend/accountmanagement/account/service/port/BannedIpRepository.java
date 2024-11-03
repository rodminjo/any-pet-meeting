package com.backend.accountmanagement.account.service.port;

import com.backend.accountmanagement.account.domain.BannedIp;
import java.util.List;

public interface BannedIpRepository {

  List<BannedIp> findAll();

  BannedIp save(BannedIp bannedIp);



}
