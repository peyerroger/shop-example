package com.rogerpeyer.ordermanagement.apicontroller.order.converter;

import com.rogerpeyer.ordermanagement.api.model.OrderItem;
import com.rogerpeyer.ordermanagement.api.model.OrderItemInput;
import com.rogerpeyer.ordermanagement.persistence.model.OrderItemPo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
   * @param orderItemPricing the pricing
   * @return the TO
   */
  public OrderItem convert(
      OrderItemPo orderItemPo,
      com.rogerpeyer.orderpricing.client.api.model.OrderItemPricing orderItemPricing) {
    OrderItem orderItem = new OrderItem();
    orderItem.setQuantity(orderItemPo.getQuantity());
    orderItem.setProductId(orderItemPo.getProductId());
    orderItem.setPrice(orderItemPricing.getPrice());
    return orderItem;
  }
}
