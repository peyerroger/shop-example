package com.rogerpeyer.dockerexample.eventpublisher.order.converter;

import com.google.protobuf.Timestamp;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.spi.proto.OrderOuterClass.Order;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConverter {

  private final OrderItemEventConverter orderItemEventConverter;

  /**
   * Constructor.
   *
   * @param orderItemEventConverter the order item converter
   */
  @Autowired
  public OrderEventConverter(OrderItemEventConverter orderItemEventConverter) {
    this.orderItemEventConverter = orderItemEventConverter;
  }

  /**
   * Converts a Po to To.
   *
   * @param orderPo the Po
   * @return the To
   */
  public Order convert(OrderPo orderPo) {
    return Order.newBuilder()
        .setId(orderPo.getId().toString())
        .setCreatedOn(convert(orderPo.getCreatedOn()))
        .setLastModified(convert(orderPo.getLastModified()))
        .addAllItems(
            orderPo
                .getItems()
                .stream()
                .map(orderItemEventConverter::convert)
                .collect(Collectors.toList()))
        .build();
  }

  private Timestamp convert(OffsetDateTime offsetDateTime) {
    Instant instant = offsetDateTime.toInstant();
    return Timestamp.newBuilder()
        .setSeconds(instant.getEpochSecond())
        .setNanos(instant.getNano())
        .build();
  }
}
