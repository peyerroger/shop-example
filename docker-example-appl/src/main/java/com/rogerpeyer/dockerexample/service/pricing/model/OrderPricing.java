package com.rogerpeyer.dockerexample.service.pricing.model;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class OrderPricing {
  private BigDecimal price;
  private Map<String, BigDecimal> pricePerOrderItemMap;
}
