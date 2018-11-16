package com.rogerpeyer.dockerexample.controller.order.converter;

import com.rogerpeyer.dockerexample.api.model.Order;
import com.rogerpeyer.dockerexample.api.model.OrderInput;
import com.rogerpeyer.dockerexample.api.model.OrderItem;
import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.dockerexample.service.order.model.OrderPricing;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter {

  /**
   * Calculate the output product.
   *
   * @param orderPo the persistence object
   * @param orderPricing
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
              .map(
                  orderItemPo -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setQuantity(orderItemPo.getQuantity());
                    orderItem.setProductId(orderItemPo.getProductId());
                    orderItem.setPrice(
                        orderPricing.getOrderItemPriceMap().get(orderItemPo.getProductId()));
                    return orderItem;
                  })
              .collect(Collectors.toList()));
    }
    return order;
  }

  /**
   * Converts a list of persistence objects to a list of api objects.
   *
   * @param orderPos the persistence objects
   * @return the api objects.
   */
  public List<Order> convertOutput(
      List<OrderPo> orderPos, Map<String, OrderPricing> orderIdOrderPricingMap) {
    if (orderPos == null) {
      return new ArrayList<>();
    } else {
      return orderPos
          .stream()
          .map(
              orderPo ->
                  convertOutput(orderPo, orderIdOrderPricingMap.get(orderPo.getId().toString())))
          .collect(Collectors.toList());
    }
  }

  /**
   * Merges an existing persistence object with an api object.
   *
   * @param orderInput the api object
   * @param orderPo the existing persistence object
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
              .map(
                  orderInputItems -> {
                    OrderItemPo orderItemPo = new OrderItemPo();
                    orderItemPo.setQuantity(orderInputItems.getQuantity());
                    orderItemPo.setProductId(orderInputItems.getProductId());
                    return orderItemPo;
                  })
              .collect(Collectors.toList()));
    } else {
      orderPo.setItems(null);
    }
    return orderPo;
  }
}
