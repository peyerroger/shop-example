package com.rogerpeyer.dockerexample.eventproducer.order.converter;

import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.spi.proto.OrderOuterClass.OrderItem;
import org.springframework.stereotype.Component;

@Component("event.orderItemConverter")
public class OrderItemConverter {

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
