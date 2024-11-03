package com.backend.accountmanagement.mock.utils;

import com.backend.accountmanagement.common.service.port.ClockHolder;
import java.util.Date;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockClockHolder implements ClockHolder {

  private final Date date;
  @Override
  public Date getNow() {
    return date;
  }
}
