package com.rogerpeyer.dockerexample.service.pricing;

import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.dockerexample.service.pricing.model.OrderPricing;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderPricingService {

  private final ProductPriceCollector productPriceCollector;

  @Autowired
  public OrderPricingService(ProductPriceCollector productPriceCollector) {
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
    orderPricing.setOrderItemPriceMap(orderItemPriceMap);
    return orderPricing;
  }

  /**
   * Get the order pricing.
   *
   * @param orderPo the order to be priced
   * @return the order pricing
   */
  public OrderPricing getOrderPricing(OrderPo orderPo) {
    Map<String, BigDecimal> productUnitPriceMap =
        productPriceCollector.getProductUnitPriceMap(orderPo);
    return getOrderPricing(getOrderItemPriceMap(orderPo, productUnitPriceMap));
  }

  /**
   * Get the order pricing per order id map.
   *
   * @param orders the orders to be priced
   * @return the order pricing per order id map
   */
  public Map<String, OrderPricing> getOrderIdOrderPricingMap(List<OrderPo> orders) {
    // Create Product / UnitPrice Map
    Map<String, BigDecimal> productUnitPriceMap =
        productPriceCollector.getProductUnitPriceMap(orders);

    return orders
        .stream()
        .collect(
            Collectors.toMap(
                orderPo -> orderPo.getId().toString(),
                orderPo -> getOrderPricing(getOrderItemPriceMap(orderPo, productUnitPriceMap))));
  }
}
