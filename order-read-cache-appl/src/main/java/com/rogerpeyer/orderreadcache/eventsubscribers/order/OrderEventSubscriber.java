package com.rogerpeyer.orderreadcache.eventsubscribers.order;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rogerpeyer.orderreadcache.eventsubscribers.order.converter.OrderEventConverter;
import com.rogerpeyer.orderreadcache.persistence.model.OrderItemPo;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.persistence.repository.OrderRepository;
import com.rogerpeyer.orderreadcache.persistence.repository.ProductOrderMapRepository;
import com.rogerpeyer.spi.proto.OrderOuterClass.Order;
import com.rogerpeyer.spi.proto.OrderOuterClass.OrderItem;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
  private final ProductOrderMapRepository productOrderMapRepository;

  /**
   * Constructor.
   *
   * @param orderRepository the order repository
   * @param orderEventConverter the order event converter
   * @param productOrderMapRepository the product order map repository
   */
  @Autowired
  public OrderEventSubscriber(
      OrderRepository orderRepository,
      OrderEventConverter orderEventConverter,
      ProductOrderMapRepository productOrderMapRepository) {
    this.orderRepository = orderRepository;
    this.orderEventConverter = orderEventConverter;
    this.productOrderMapRepository = productOrderMapRepository;
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
      OrderPo existingOrderPo = orderRepository.findById(order.getId()).orElse(null);

      // Update Product Order Maps
      if (existingOrderPo != null && !existingOrderPo.getItems().isEmpty()) {

        // Remove all order items which do not exist anymore
        Map<String, String> toBeRemoved = getToBeRemoved(existingOrderPo, order);
        productOrderMapRepository.removeAll(toBeRemoved);

        // Add all order items which are new
        Map<String, String> toBeAdded = getToBeAdded(order, existingOrderPo);
        productOrderMapRepository.putAll(toBeAdded);
      } else {
        // Add all order items
        Map<String, String> toBeAdded = getProductOrderMap(order);
        productOrderMapRepository.putAll(toBeAdded);
      }

      // Store order itself
      OrderPo orderPo = orderEventConverter.convertOrder(order);
      orderRepository.save(orderPo);

    } catch (InvalidProtocolBufferException e) {
      log.error("Could not process product event.", e);
    }
  }

  private Map<String, String> getProductOrderMap(OrderPo orderPo) {
    return orderPo
        .getItems()
        .stream()
        .collect(Collectors.toMap(OrderItemPo::getProductId, orderItem -> orderPo.getId()));
  }

  private Map<String, String> getProductOrderMap(Order order) {
    return order
        .getItemsList()
        .stream()
        .collect(Collectors.toMap(OrderItem::getProductId, orderItem -> order.getId()));
  }

  private Set<String> getProductSet(OrderPo orderPo) {
    return orderPo.getItems().stream().map(OrderItemPo::getProductId).collect(Collectors.toSet());
  }

  private Set<String> getProductSet(Order order) {
    return order.getItemsList().stream().map(OrderItem::getProductId).collect(Collectors.toSet());
  }

  private Map<String, String> getToBeRemoved(OrderPo orderPo, Order order) {
    Map<String, String> productOrderMap = getProductOrderMap(orderPo);
    Set<String> productSet = getProductSet(order);
    productOrderMap.keySet().removeAll(productSet);
    return productOrderMap;
  }

  private Map<String, String> getToBeAdded(Order order, OrderPo orderPo) {
    Map<String, String> productOrderMap = getProductOrderMap(order);
    Set<String> productSet = getProductSet(orderPo);
    productOrderMap.keySet().removeAll(productSet);
    return productOrderMap;
  }
}
