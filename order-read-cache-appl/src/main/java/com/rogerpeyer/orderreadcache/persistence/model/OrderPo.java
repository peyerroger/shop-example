package com.rogerpeyer.orderreadcache.persistence.model;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("order")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPo {

  @Id
  private String id;

  private OffsetDateTime lastModified;
  private OffsetDateTime createdOn;

  private List<OrderItemPo> items;

}
