package com.backend.accountmanagement.common.infrastructure;

import com.backend.accountmanagement.common.service.port.CodeGenerator;
import com.backend.accountmanagement.utils.RandomUtils;
import org.springframework.stereotype.Component;

@Component
public class RandomCodeGenerator implements CodeGenerator {

  @Override
  public String generateCode() {
    return RandomUtils.generateRandomMixNumNStr(6);
  }
}
