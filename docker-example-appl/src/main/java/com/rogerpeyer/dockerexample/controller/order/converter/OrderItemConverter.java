package com.rogerpeyer.dockerexample.controller.order.converter;

import com.rogerpeyer.dockerexample.api.model.OrderItem;
import com.rogerpeyer.dockerexample.api.model.OrderItemInput;
import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.service.pricing.model.OrderPricing;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter {

  /**
   * Converts a TO to a PO.
   *
   * @param orderItemInput the TO
   * @return the PO
   */
  public OrderItemPo convert(OrderItemInput orderItemInput) {
    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(orderItemInput.getQuantity());
    orderItemPo.setProductId(orderItemInput.getProductId());
    return orderItemPo;
  }

  /**
   * Converts a PO to a TO.
   *
   * @param orderItemPo the PO
   * @param orderPricing the pricing
   * @return the TO
   */
  public OrderItem convert(OrderItemPo orderItemPo, OrderPricing orderPricing) {
    OrderItem orderItem = new OrderItem();
    orderItem.setQuantity(orderItemPo.getQuantity());
    orderItem.setProductId(orderItemPo.getProductId());
    orderItem.setPrice(orderPricing.getOrderItemPriceMap().get(orderItemPo.getProductId()));
    return orderItem;
  }
}
