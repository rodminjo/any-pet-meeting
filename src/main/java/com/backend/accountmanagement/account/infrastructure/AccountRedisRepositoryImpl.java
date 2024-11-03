package com.backend.accountmanagement.account.infrastructure;

import com.backend.accountmanagement.redis.service.port.RedisRepository;
import com.backend.accountmanagement.utils.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AccountRedisRepositoryImpl implements RedisRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public <V> V getData(String key, Class<V> valueType) throws JsonProcessingException {
    String jsonResult = (String) redisTemplate.opsForValue().get(key);

    if (StringUtils.isBlank(jsonResult)) {
      return null;
    } else {
      ObjectMapper mapper = MapperUtils.getMapper();
      return mapper.readValue(jsonResult, valueType);
    }
  }

  @Override
  public <V> void setData(String key, V value) throws JsonProcessingException {
    ObjectMapper mapper = MapperUtils.getMapper();
    String valueStr = mapper.writeValueAsString(value);
    redisTemplate.opsForValue().set(key, valueStr);
  }

  @Override
  public <V> void setDataWithExpired(String key, V value, long durationMillis)
      throws JsonProcessingException {
    ObjectMapper mapper = MapperUtils.getMapper();
    String valueStr = mapper.writeValueAsString(value);
    redisTemplate.opsForValue().set(key, valueStr, Duration.ofMillis(durationMillis));
  }

  @Override
  public void deleteData(String key) {
    redisTemplate.delete(key);
  }

}
