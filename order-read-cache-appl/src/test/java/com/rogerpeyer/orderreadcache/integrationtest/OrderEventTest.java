package com.rogerpeyer.orderreadcache.integrationtest;

import com.google.protobuf.Timestamp;
import com.rogerpeyer.orderreadcache.eventsubscribers.order.OrderEventSubscriber;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.persistence.repository.OrderRepository;
import com.rogerpeyer.spi.proto.OrderOuterClass.Order;
import com.rogerpeyer.spi.proto.OrderOuterClass.OrderItem;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

public class OrderEventTest extends AbstractTest {

  @Autowired private OrderRepository orderRepository;
  @Autowired private RedisTemplate<String, String> redisTemplate;

  @After
  public void tearDown() {
    orderRepository.deleteAll();
  }

  @Test
  public void newOrder() throws Exception {
    Order order = newOrderInstance();
    KafkaTemplate<String, byte[]> template = getKafkaTemplate();
    template.sendDefault(order.getId(), order.toByteArray());

    Thread.sleep(1000L);

    OrderPo orderPo = orderRepository.findById(order.getId()).orElse(null);
    Assert.assertNotNull(orderPo);
  }

  @Test
  public void modifyOrder() throws Exception {
    Order order = newOrderInstance();
    KafkaTemplate<String, byte[]> template = getKafkaTemplate();
    template.sendDefault(order.getId(), order.toByteArray());

    OrderItem toBeKept = order.getItems(0);
    Order order1 = newOrderInstance(order.getId(), toBeKept);
    template.sendDefault(order1.getId(), order1.toByteArray());

    Thread.sleep(1000L);

    OrderPo orderPo = orderRepository.findById(order.getId()).orElse(null);
    Assert.assertNotNull(orderPo);
    Assert.assertNotNull(orderPo.getItems());
    Assert.assertEquals(4, orderPo.getItems().size());

    orderPo
        .getItems()
        .forEach(
            orderItemPo -> {
              Set<String> orders = redisTemplate.opsForSet().members(orderItemPo.getProductId());
              Assert.assertTrue(Objects.requireNonNull(orders).contains(orderPo.getId()));
            });
  }

  @Test
  public void performanceTest() throws Exception {
    int numberOfOrders = 100;
    KafkaTemplate<String, byte[]> template = getKafkaTemplate();
    for (int i = 0; i < numberOfOrders; i++) {
      Order order = newOrderInstance();
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

  private Order newOrderInstance() {
    return newOrderInstance(UUID.randomUUID().toString(), null);
  }

  private Order newOrderInstance(String id, OrderItem toBeAdded) {
    return Order.newBuilder()
        .setId(id)
        .setCreatedOn(
            Timestamp.newBuilder().setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
        .setLastModified(
            Timestamp.newBuilder().setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
        .addAllItems(createOrderItemsList(toBeAdded))
        .build();
  }

  private Iterable<OrderItem> createOrderItemsList(OrderItem toBeAdded) {
    List<OrderItem> orderItems;
    if (toBeAdded != null) {
      orderItems =
          Arrays.asList(
              newOrderItemInstance(), newOrderItemInstance(), newOrderItemInstance(), toBeAdded);
    } else {
      orderItems =
          Arrays.asList(newOrderItemInstance(), newOrderItemInstance(), newOrderItemInstance());
    }
    return orderItems;
  }

  private OrderItem newOrderItemInstance() {
    return newOrderItemInstance(UUID.randomUUID().toString());
  }

  private OrderItem newOrderItemInstance(String productId) {
    return OrderItem.newBuilder()
        .setQuantity(new Random().nextInt())
        .setProductId(productId)
        .build();
  }
}
