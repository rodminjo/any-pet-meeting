package com.backend.accountmanagement.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class LongSetConverter implements AttributeConverter<Set<Long>, String> {

  private static final String SPLIT_CHAR = ",";


  @Override
  public String convertToDatabaseColumn(Set<Long> attribute) {
    return attribute.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(SPLIT_CHAR));

  }

  @Override
  public Set<Long> convertToEntityAttribute(String dbData) {
    return Arrays.stream(dbData.split(SPLIT_CHAR))
        .map(Long::parseLong)
        .collect(Collectors.toSet());

  }
}
