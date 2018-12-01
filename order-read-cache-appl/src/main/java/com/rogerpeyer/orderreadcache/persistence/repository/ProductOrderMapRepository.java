package com.rogerpeyer.orderreadcache.persistence.repository;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

@Repository
public class ProductOrderMapRepository {
  private final SetOperations<String, String> setOperations;

  @Autowired
  public ProductOrderMapRepository(RedisTemplate<String, String> redisTemplate) {
    this.setOperations = redisTemplate.opsForSet();
  }

  public void putAll(Map<String, String> productOrderMap) {
    productOrderMap.forEach(this::put);
  }

  public void removeAll(Map<String, String> productOrderMap) {
    productOrderMap.forEach(this::remove);
  }

  private Long put(String productId, String orderId) {
    return setOperations.add(productId, orderId);
  }

  private Long remove(String productId, String orderId) {
    return setOperations.remove(productId, orderId);
  }
}
