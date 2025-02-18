package com.backend.accountmanagement.mock.repo;

import com.backend.accountmanagement.account.domain.BannedIp;
import com.backend.accountmanagement.account.service.port.BannedIpRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class FakeBannedIpRepository implements BannedIpRepository {

  private final AtomicLong autoGeneratedId = new AtomicLong(0);
  private final List<BannedIp> data = Collections.synchronizedList(new ArrayList<>());


  @Override
  public List<BannedIp> findAll() {
    return new ArrayList<>(data);

  }

  @Override
  public BannedIp save(BannedIp bannedIp){
    if (bannedIp.getId() == 0) {
      BannedIp newBannedIp = BannedIp.builder()
          .id(autoGeneratedId.incrementAndGet())
          .ipAddress(bannedIp.getIpAddress())
          .build();

      data.add(newBannedIp);
      return newBannedIp;

    } else {
      data.removeIf(item -> Objects.equals(item.getId(), bannedIp.getId()));
      data.add(bannedIp);
      return bannedIp;

    }

  }
}
