package com.backend.accountmanagement.redis.service.port;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface RedisRepository {

  <V> V getData(String key, Class<V> valueType) throws JsonProcessingException;

  <V> void setData(String key, V value) throws JsonProcessingException;

  <V> void setDataWithExpired(String key, V value, long durationMillis) throws JsonProcessingException;

  void deleteData(String key);

}
