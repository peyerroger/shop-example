package com.rogerpeyer.orderreadcache.integrationtest;

import com.google.protobuf.Timestamp;
import com.rogerpeyer.orderreadcache.eventsubscribers.order.OrderEventSubscriber;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.persistence.repository.redis.OrderRepository;
import com.rogerpeyer.spi.proto.OrderOuterClass.Order;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

public class OrderEventTest extends AbstractTest {

  @Autowired private OrderRepository orderRepository;

  @Before
  public void setUp() {
    orderRepository.deleteAll();
  }

  @Test
  public void test() throws Exception {
    Order order = newTestInstance();
    KafkaTemplate<String, byte[]> template = getKafkaTemplate();
    template.sendDefault(order.getId(), order.toByteArray());

    Thread.sleep(1000L);

    OrderPo orderPo = orderRepository.findById(order.getId()).orElse(null);
    Assert.assertNotNull(orderPo);
  }

  @Test
  public void test2() throws Exception {
    int numberOfOrders = 100;
    KafkaTemplate<String, byte[]> template = getKafkaTemplate();
    for (int i = 0; i < numberOfOrders; i++) {
      Order order = newTestInstance();
      template.sendDefault(order.getId(), order.toByteArray());
    }

    Thread.sleep(1000L);

    Assert.assertEquals(numberOfOrders, orderRepository.count());
  }

  private KafkaTemplate<String, byte[]> getKafkaTemplate() {
    Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
    senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    senderProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
    ProducerFactory<String, byte[]> pf = new DefaultKafkaProducerFactory<>(senderProps);
    KafkaTemplate<String, byte[]> template = new KafkaTemplate<>(pf);
    template.setDefaultTopic(OrderEventSubscriber.TOPIC);
    return template;
  }

  private Order newTestInstance() {
    return Order.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setCreatedOn(
            Timestamp.newBuilder().setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
        .setLastModified(
            Timestamp.newBuilder().setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
        .build();
  }
}
