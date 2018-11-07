package com.rogerpeyer.dockerexample.persistence.model;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("order")
public class OrderPo {

  @Id
  private String id;

  private List<OrderItemPo> items;

  private OffsetDateTime lastModified;

  private OffsetDateTime createdOn;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<OrderItemPo> getItems() {
    return items;
  }

  public void setItems(List<OrderItemPo> items) {
    this.items = items;
  }

  public OffsetDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public OffsetDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(OffsetDateTime createdOn) {
    this.createdOn = createdOn;
  }
}
