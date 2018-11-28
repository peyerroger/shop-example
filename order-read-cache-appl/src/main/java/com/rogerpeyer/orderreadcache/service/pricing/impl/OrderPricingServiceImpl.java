package com.rogerpeyer.orderreadcache.service.pricing.impl;

import com.rogerpeyer.orderreadcache.persistence.model.OrderItemPo;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.service.pricing.OrderPricingService;
import com.rogerpeyer.orderreadcache.service.pricing.model.OrderPricing;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderPricingServiceImpl implements OrderPricingService {

  private final ProductPriceCollector productPriceCollector;

  @Autowired
  public OrderPricingServiceImpl(ProductPriceCollector productPriceCollector) {
    this.productPriceCollector = productPriceCollector;
  }

  private Map<String, BigDecimal> getOrderItemPriceMap(
      OrderPo orderPo, Map<String, BigDecimal> productUnitPriceMap) {
    return orderPo
        .getItems()
        .stream()
        .collect(
            Collectors.toMap(
                OrderItemPo::getProductId,
                orderItemPo ->
                    productUnitPriceMap
                        .get(orderItemPo.getProductId())
                        .multiply(BigDecimal.valueOf(orderItemPo.getQuantity()))));
  }

  private BigDecimal getOrderPrice(Map<String, BigDecimal> orderItemPriceMap) {
    return orderItemPriceMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private OrderPricing getOrderPricing(Map<String, BigDecimal> orderItemPriceMap) {
    OrderPricing orderPricing = new OrderPricing();
    orderPricing.setPrice(getOrderPrice(orderItemPriceMap));
    orderPricing.setPricePerOrderItemMap(orderItemPriceMap);
    return orderPricing;
  }

  @Override
  public OrderPricing getOrderPricing(OrderPo orderPo) {
    Map<String, BigDecimal> productUnitPriceMap =
        productPriceCollector.getProductUnitPriceMap(orderPo);
    return getOrderPricing(getOrderItemPriceMap(orderPo, productUnitPriceMap));
  }

  @Override
  public Map<String, OrderPricing> getOrderIdOrderPricingMap(Iterable<OrderPo> orders) {
    // Create Product / UnitPrice Map
    Map<String, BigDecimal> productUnitPriceMap =
        productPriceCollector.getProductUnitPriceMap(orders);

    return StreamSupport.stream(orders.spliterator(), false)
        .collect(
            Collectors.toMap(
                OrderPo::getId,
                orderPo -> getOrderPricing(getOrderItemPriceMap(orderPo, productUnitPriceMap))));
  }
}
