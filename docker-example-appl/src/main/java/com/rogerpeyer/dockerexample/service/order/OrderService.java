package com.rogerpeyer.dockerexample.service.order;

import com.rogerpeyer.dockerexample.api.model.Order;
import com.rogerpeyer.dockerexample.api.model.OrderInput;
import com.rogerpeyer.dockerexample.api.model.OrderItems;
import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  /**
   * Calculate the output product.
   *
   * @param orderPo the persistence object
   * @return the api object.
   */
  public Order calculateOutput(OrderPo orderPo) {
    Order order = new Order();
    order.setId(orderPo.getId());
    order.setCreatedOn(orderPo.getCreatedOn());
    order.setLastModified(orderPo.getLastModified());
    if (orderPo.getItems() != null) {
      order.setItems(orderPo.getItems().stream().map(orderItemPo -> {
        OrderItems orderItems = new OrderItems();
        orderItems.setQuantity(orderItemPo.getQuantity());
        orderItems.setProductId(orderItemPo.getProductId());
        return orderItems;
      }).collect(Collectors.toList()));
    }
    return order;
  }

  /**
   * Converts a list of persistence objects to a list of api objects.
   *
   * @param orderPos the persistence objects
   * @return the api objects.
   */
  public List<Order> calculateOutput(Iterable<OrderPo> orderPos) {
    if (orderPos == null) {
      return new ArrayList<>();
    } else {
      return StreamSupport.stream(orderPos.spliterator(), false)
          .map(this::calculateOutput).collect(Collectors.toList());
    }
  }

  /**
   * Merges an existing persistence object with an api object.
   *
   * @param orderInput the api object
   * @param orderPo    the existing persistence object
   */
  public OrderPo calculateInput(OrderInput orderInput, OrderPo orderPo) {
    if (orderPo == null) {
      orderPo = new OrderPo();
      orderPo.setCreatedOn(OffsetDateTime.now());
    }
    orderPo.setLastModified(OffsetDateTime.now());
    if (orderInput.getItems() != null) {
      orderPo.setItems(orderInput.getItems().stream().map(orderInputItems -> {
        OrderItemPo orderItemPo = new OrderItemPo();
        orderItemPo.setQuantity(orderInputItems.getQuantity());
        orderItemPo.setProductId(orderInputItems.getProductId());
        return orderItemPo;
      }).collect(Collectors.toList()));
    } else {
      orderPo.setItems(null);
    }
    return orderPo;
  }

}
