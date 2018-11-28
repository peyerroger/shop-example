package com.rogerpeyer.orderreadcache.apicontroller.order.converter;

import com.rogerpeyer.orderreadcache.api.model.OrderItem;
import com.rogerpeyer.orderreadcache.persistence.model.OrderItemPo;
import com.rogerpeyer.orderreadcache.service.pricing.model.OrderPricing;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter {


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
    orderItem.setPrice(orderPricing.getPricePerOrderItemMap().get(orderItemPo.getProductId()));
    return orderItem;
  }
}
