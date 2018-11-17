package com.rogerpeyer.dockerexample.eventpublisher.order;

import com.rogerpeyer.dockerexample.eventpublisher.order.converter.OrderEventConverter;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.spi.proto.OrderOuterClass.Order;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Log4j2
public class OrderEventPublisher {

  public static final String TOPIC = "order";

  private final OrderEventConverter orderEventConverter;
  private final KafkaTemplate<String, byte[]> kafkaTemplate;

  /**
   * Constructor.
   *
   * @param orderEventConverter the order converter
   * @param kafkaTemplate the kafka template
   */
  @Autowired
  public OrderEventPublisher(
      OrderEventConverter orderEventConverter, KafkaTemplate<String, byte[]> kafkaTemplate) {
    this.orderEventConverter = orderEventConverter;
    this.kafkaTemplate = kafkaTemplate;
  }

  /**
   * Publishes an order event.
   *
   * @param orderPo the PO
   */
  public void publish(OrderPo orderPo) {
    Order order = orderEventConverter.convert(orderPo);
    log.debug("Sending order event. [id={}]", order.getId());
    kafkaTemplate.send(TOPIC, order.getId(), order.toByteArray());
  }
}
