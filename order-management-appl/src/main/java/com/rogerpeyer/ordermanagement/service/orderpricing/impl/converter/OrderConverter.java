package com.rogerpeyer.ordermanagement.service.orderpricing.impl.converter;

import com.rogerpeyer.ordermanagement.persistence.model.OrderItemPo;
import com.rogerpeyer.ordermanagement.persistence.model.OrderPo;
import com.rogerpeyer.orderpricing.client.api.model.Order;
import com.rogerpeyer.orderpricing.client.api.model.OrderItem;
import com.rogerpeyer.orderpricing.client.api.model.Orders;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component("orderPricing.orderConverter")
public class OrderConverter {

  private OrderItem convert(OrderItemPo orderItemPo) {
    OrderItem orderItem = new OrderItem();
    orderItem.setQuantity(orderItemPo.getQuantity());
    orderItem.setProductId(orderItemPo.getProductId());
    return orderItem;
  }

  public Order convert(OrderPo orderPo) {
    Order order = new Order();
    order.setId(orderPo.getId().toString());
    order.setItems(orderPo.getItems().stream().map(this::convert).collect(Collectors.toList()));
    return order;
  }

  public Orders convert(Collection<OrderPo> orderPos) {
    Orders orders = new Orders();
    orders.setOrders(orderPos.stream().map(this::convert).collect(Collectors.toList()));
    return orders;
  }
}
