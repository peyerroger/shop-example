package com.rogerpeyer.orderservice.eventpublisher.order.converter;

import com.rogerpeyer.orderservice.persistence.model.OrderItemPo;
import com.rogerpeyer.spi.proto.OrderOuterClass.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemEventConverter {

  /**
   * Converts PO to TO.
   *
   * @param orderItemPo the PO
   * @return the TO
   */
  public OrderItem convert(OrderItemPo orderItemPo) {
    return OrderItem.newBuilder()
        .setProductId(orderItemPo.getProductId())
        .setQuantity(orderItemPo.getQuantity())
        .build();
  }
}
