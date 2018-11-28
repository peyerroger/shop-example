package com.rogerpeyer.orderreadcache.integrationtest.redis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

@Component
public class EmbeddedRedis {

  private RedisServer redisServer;

  @PostConstruct
  public void startRedis() {
    redisServer = new RedisServer();
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    redisServer.stop();
  }
}
