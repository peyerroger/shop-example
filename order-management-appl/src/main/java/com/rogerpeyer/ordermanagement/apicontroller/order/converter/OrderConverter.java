package com.rogerpeyer.ordermanagement.apicontroller.order.converter;

import com.rogerpeyer.ordermanagement.api.model.Order;
import com.rogerpeyer.ordermanagement.api.model.OrderInput;
import com.rogerpeyer.ordermanagement.persistence.model.OrderPo;
import com.rogerpeyer.ordermanagement.service.pricing.model.OrderPricing;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    order.setVersion(orderPo.getVersion());
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
      List<OrderPo> orderPos, Map<String, OrderPricing> orderPricingPerOrderIdMap) {
    if (orderPos == null) {
      return new ArrayList<>();
    } else {
      return orderPos
          .stream()
          .map(
              orderPo ->
                  convertOutput(orderPo, orderPricingPerOrderIdMap.get(orderPo.getId().toString())))
          .collect(Collectors.toList());
    }
  }

  /**
   * Merges an existing persistence object with an api object.
   *
   * @param orderInput the api object
   * @param orderPo the existing persistence object
   * @return the order Po
   */
  public OrderPo convertInput(OrderInput orderInput, OrderPo orderPo) {
    if (orderPo == null) {
      orderPo = new OrderPo();
    } else {
      orderPo.setVersion(orderInput.getVersion());
    }
    if (orderInput.getItems() != null) {
      orderPo.setItems(
          orderInput
              .getItems()
              .stream()
              .map(orderItemConverter::convert)
              .collect(Collectors.toList()));
    } else {
      orderPo.setItems(null);
    }
    return orderPo;
  }
}
