package com.rogerpeyer.orderpricing.service.pricing.impl;

import com.rogerpeyer.orderpricing.api.model.Order;
import com.rogerpeyer.orderpricing.api.model.OrderItemPricing;
import com.rogerpeyer.orderpricing.api.model.OrderPricing;
import com.rogerpeyer.orderpricing.api.model.OrderPricings;
import com.rogerpeyer.orderpricing.api.model.Orders;
import com.rogerpeyer.orderpricing.service.pricing.OrderPricingService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
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
      Order order, Map<String, BigDecimal> productUnitPriceMap) {
    return order
        .getItems()
        .stream()
        .collect(
            Collectors.toMap(
                com.rogerpeyer.orderpricing.api.model.OrderItem::getProductId,
                orderItemPo ->
                    productUnitPriceMap
                        .get(orderItemPo.getProductId())
                        .multiply(BigDecimal.valueOf(orderItemPo.getQuantity()))));
  }

  private BigDecimal getOrderPrice(Map<String, BigDecimal> orderItemPriceMap) {
    return orderItemPriceMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private OrderPricing getOrderPricing2(Order order, Map<String, BigDecimal> orderItemPriceMap) {
    OrderPricing orderPricing = new OrderPricing();
    orderPricing.setPrice(getOrderPrice(orderItemPriceMap));
    orderPricing.setOrderId(order.getId());
    orderItemPriceMap.forEach(
        (productId, price) -> {
          OrderItemPricing orderItemPricing = new OrderItemPricing();
          orderItemPricing.setProductId(productId);
          orderItemPricing.setPrice(price);
          orderPricing.addOrderItemPricingsItem(orderItemPricing);
        });
    return orderPricing;
  }

  @Override
  public OrderPricing getOrderPricing(Order order) {
    Map<String, BigDecimal> productUnitPriceMap =
        productPriceCollector.getProductUnitPriceMap(order);
    return getOrderPricing2(order, getOrderItemPriceMap(order, productUnitPriceMap));
  }

  private OrderPricing getOrderPricing(Order order, Map<String, BigDecimal> productUnitPriceMap) {
    return getOrderPricing2(order, getOrderItemPriceMap(order, productUnitPriceMap));
  }

  @Override
  public OrderPricings getBulkOrderPricing(Orders orders) {
    // Create Product / UnitPrice Map
    Map<String, BigDecimal> productUnitPriceMap =
        productPriceCollector.getProductUnitPriceMap(orders);

    OrderPricings orderPricings = new OrderPricings();
    orders
        .getOrders()
        .forEach(
            order ->
                orderPricings.addOrderPricingsItem(getOrderPricing(order, productUnitPriceMap)));

    return orderPricings;
  }
}
