package com.rogerpeyer.orderreadcache.eventsubscribers.order.converter;

import com.google.protobuf.Timestamp;
import com.rogerpeyer.orderreadcache.persistence.model.OrderItemPo;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.spi.proto.OrderOuterClass.Order;
import com.rogerpeyer.spi.proto.OrderOuterClass.OrderItem;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConverter {

  /**
   * Converts an Order to an OrderPo.
   *
   * @param order the order event
   * @return the OrderPo
   */
  public OrderPo convertOrderItem(Order order) {
    return OrderPo.builder()
        .id(order.getId())
        .createdOn(convertToOffsetDateTime(order.getCreatedOn()))
        .lastModified(convertToOffsetDateTime(order.getLastModified()))
        .items(convertOrderItems(order.getItemsList()))
        .build();
  }

  private OffsetDateTime convertToOffsetDateTime(Timestamp timestamp) {
    return OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()), ZoneId.of("UTC"));
  }

  private List<OrderItemPo> convertOrderItems(Collection<OrderItem> orderItems) {
    if (orderItems == null || orderItems.isEmpty()) {
      return new ArrayList<>();
    } else {
      return orderItems.stream().map(this::convertOrderItem).collect(Collectors.toList());
    }
  }

  private OrderItemPo convertOrderItem(OrderItem orderItem) {
    return OrderItemPo.builder()
        .productId(orderItem.getProductId())
        .quantity(orderItem.getQuantity())
        .build();
  }
}
