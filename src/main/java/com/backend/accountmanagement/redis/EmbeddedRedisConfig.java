package com.backend.accountmanagement.redis;

import com.backend.accountmanagement.common.exception.ExceptionMessage;
import com.backend.accountmanagement.web.configs.properties.RedisProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

@Slf4j
@RequiredArgsConstructor
@Profile({"local","dev","test"})
@Configuration
public class EmbeddedRedisConfig {

  private final RedisProperties redisProperties;
  private RedisServer redisServer;


  @PostConstruct
  public void startRedis() throws IOException {
    int port = isRedisRunning() ? findAvailablePort() : redisProperties.getPort();
    if (isArmArchitecture()) {
      log.info("ARM Architecture");
      redisServer = new RedisServer(Objects.requireNonNull(getRedisServerExecutable()), port);
    } else {
      redisServer = RedisServer.builder()
          .port(port)
          .setting("maxmemory " + redisProperties.getMaxmemory())
          .build();
    }
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    redisServer.stop();
  }

  public int findAvailablePort() throws IOException {
    for (int port = 10000; port <= 65535; port++) {
      Process process = executeGrepProcessCommand(port);
      if (!isRunning(process)) {
        return port;
      }
    }

    throw new IllegalStateException(ExceptionMessage.NOT_FOUND_AVAILABLE_PORT);
  }

  private boolean isRedisRunning() throws IOException {
    return isRunning(executeGrepProcessCommand(redisProperties.getPort()));
  }

  private Process executeGrepProcessCommand(int redisPort) throws IOException {
    String command = String.format("netstat -nat | grep LISTEN|grep %d", redisPort);
    String[] shell = {"/bin/sh", "-c", command};

    return Runtime.getRuntime().exec(shell);
  }

  private boolean isRunning(Process process) {
    String line;
    StringBuilder pidInfo = new StringBuilder();

    try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      while ((line = input.readLine()) != null) {
        pidInfo.append(line);
      }
    } catch (Exception e) {
      throw new IllegalStateException(ExceptionMessage.ERROR_EXECUTING_EMBEDDED_REDIS);
    }
    return StringUtils.hasText(pidInfo.toString());
  }

  private File getRedisServerExecutable() {
    try {
      return new File("src/main/resources/binary/redis/redis-server-7.2.3-mac-arm64");
    } catch (Exception e) {
      throw new IllegalStateException(ExceptionMessage.REDIS_SERVER_EXCUTABLE_NOT_FOUND);
    }
  }

  private boolean isArmArchitecture() {
    return System.getProperty("os.arch").contains("aarch64");
  }
}
