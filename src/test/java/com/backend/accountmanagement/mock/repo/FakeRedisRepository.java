package com.backend.accountmanagement.mock.repo;

import com.backend.accountmanagement.redis.service.port.RedisRepository;
import com.backend.accountmanagement.utils.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class FakeRedisRepository implements RedisRepository {

  private final Map<String, String> store = new HashMap<>();
  private final ObjectMapper objectMapper = MapperUtils.getMapper();

  @Override
  public <V> V getData(String key, Class<V> valueType) throws JsonProcessingException {
    String jsonResult = store.get(key);
    if (jsonResult == null || jsonResult.isBlank()) {
      return null;
    }
    return objectMapper.readValue(jsonResult, valueType);
  }

  @Override
  public <V> void setData(String key, V value) throws JsonProcessingException {
    String jsonValue = objectMapper.writeValueAsString(value);
    store.put(key, jsonValue);

  }

  @Override
  public <V> void setDataWithExpired(String key, V value, long durationMillis) throws JsonProcessingException {
    setData(key, value);

  }

  @Override
  public void deleteData(String key) {
    store.remove(key);
  }


}
