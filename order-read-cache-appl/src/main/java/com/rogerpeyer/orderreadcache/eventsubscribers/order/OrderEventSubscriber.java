package com.rogerpeyer.orderreadcache.eventsubscribers.order;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rogerpeyer.orderreadcache.eventsubscribers.order.converter.OrderEventConverter;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.persistence.repository.redis.OrderRepository;
import com.rogerpeyer.spi.proto.OrderOuterClass.Order;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class OrderEventSubscriber {

  public static final String TOPIC = "order";

  private final OrderRepository orderRepository;
  private final OrderEventConverter orderEventConverter;

  public OrderEventSubscriber(OrderRepository orderRepository,
      OrderEventConverter orderEventConverter) {
    this.orderRepository = orderRepository;
    this.orderEventConverter = orderEventConverter;
  }

  /**
   * Receives and processes order events.
   *
   * @param data the product as byte string
   * @param key the key
   * @param partition the partition
   * @param topic the topic
   * @param offset the offset
   */
  @KafkaListener(topics = TOPIC)
  public void listen(
      @Payload byte[] data,
      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.OFFSET) long offset) {
    try {
      log.debug(
          "Received Order Event. [ MessageKey={}, Partition={}, Topic={}, Offset={} ]",
          key,
          partition,
          topic,
          offset);
      Order order = Order.parseFrom(data);
      OrderPo orderPo = orderEventConverter.convertOrderItem(order);
      orderRepository.save(orderPo);
    } catch (InvalidProtocolBufferException e) {
      log.error("Could not process product event.", e);
    }
  }
}
