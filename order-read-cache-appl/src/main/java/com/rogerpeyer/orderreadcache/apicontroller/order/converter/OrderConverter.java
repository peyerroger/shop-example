package com.rogerpeyer.orderreadcache.apicontroller.order.converter;

import com.rogerpeyer.orderreadcache.api.model.Order;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.service.pricing.model.OrderPricing;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter {

  private final OrderItemConverter orderItemConverter;

  @Autowired
  public OrderConverter(OrderItemConverter orderItemConverter) {
    this.orderItemConverter = orderItemConverter;
  }

  /**
   * Calculate the output product.
   *
   * @param orderPo the persistence object
   * @param orderPricing the order pricing
   * @return the api object.
   */
  public Order convertOutput(OrderPo orderPo, OrderPricing orderPricing) {
    Order order = new Order();
    order.setId(orderPo.getId());
    order.setPrice(orderPricing.getPrice());
    order.setCreatedOn(orderPo.getCreatedOn());
    order.setLastModified(orderPo.getLastModified());
    if (orderPo.getItems() != null) {
      order.setItems(
          orderPo
              .getItems()
              .stream()
              .map(orderItemPo -> orderItemConverter.convert(orderItemPo, orderPricing))
              .collect(Collectors.toList()));
    }
    return order;
  }

  /**
   * Converts a list of persistence objects to a list of api objects.
   *
   * @param orderPos the persistence objects
   * @param orderPricingPerOrderIdMap Order prices per order id map
   * @return the api objects.
   */
  public List<Order> convertOutput(
      Iterable<OrderPo> orderPos, Map<String, OrderPricing> orderPricingPerOrderIdMap) {
    if (orderPos == null) {
      return new ArrayList<>();
    } else {
      return StreamSupport.stream(orderPos.spliterator(), false)
          .map(
              orderPo ->
                  convertOutput(orderPo, orderPricingPerOrderIdMap.get(orderPo.getId().toString())))
          .collect(Collectors.toList());
    }
  }
}
