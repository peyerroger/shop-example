package com.rogerpeyer.dockerexample.eventproducer.order;

import com.rogerpeyer.dockerexample.eventproducer.order.converter.OrderConverter;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.spi.proto.OrderOuterClass.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class OrderProducer {

  public static final String TOPIC = "order";

  private final OrderConverter orderConverter;
  private final KafkaTemplate<String, byte[]> kafkaTemplate;

  /**
   * Constructor.
   *
   * @param orderConverter the order converter
   * @param kafkaTemplate the kafka template
   */
  @Autowired
  public OrderProducer(OrderConverter orderConverter, KafkaTemplate<String, byte[]> kafkaTemplate) {
    this.orderConverter = orderConverter;
    this.kafkaTemplate = kafkaTemplate;
  }

  /**
   * Publishes an order event.
   *
   * @param orderPo the PO
   */
  public void publishEvent(OrderPo orderPo) {
    Order order = orderConverter.convert(orderPo);
    kafkaTemplate.send(TOPIC, order.getId(), order.toByteArray());
  }
}
