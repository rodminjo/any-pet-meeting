package com.backend.accountmanagement.common.infrastructure;

import com.backend.accountmanagement.common.service.port.ClockHolder;
import java.util.Date;
import org.springframework.stereotype.Component;


@Component
public class SystemClockHolder implements ClockHolder {

  @Override
  public Date getNow() {
    return new Date();
  }
}
