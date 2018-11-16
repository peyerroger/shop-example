package com.rogerpeyer.dockerexample.service.order.model;

import java.math.BigDecimal;
import java.util.Map;

public class OrderPricing {

  private BigDecimal price;
  private Map<String, BigDecimal> orderItemPriceMap;

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Map<String, BigDecimal> getOrderItemPriceMap() {
    return orderItemPriceMap;
  }

  public void setOrderItemPriceMap(Map<String, BigDecimal> orderItemPriceMap) {
    this.orderItemPriceMap = orderItemPriceMap;
  }
}
