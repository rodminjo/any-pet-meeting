package com.backend.accountmanagement.mock.utils;

import com.backend.accountmanagement.common.service.port.CodeGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockCodeGenerator implements CodeGenerator {

  private final String codeStr;

  @Override
  public String generateCode() {
    return codeStr;
  }
}
