package com.rogerpeyer.dockerexample.integrationtest;

import com.google.common.collect.Iterators;
import com.rogerpeyer.dockerexample.api.model.Order;
import com.rogerpeyer.dockerexample.api.model.OrderInput;
import com.rogerpeyer.dockerexample.api.model.OrderItem;
import com.rogerpeyer.dockerexample.api.model.OrderItemInput;
import com.rogerpeyer.dockerexample.eventproducer.order.OrderProducer;
import com.rogerpeyer.dockerexample.integrationtest.util.ProductUtil;
import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.jpa.OrderRepository;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import com.rogerpeyer.spi.proto.OrderOuterClass;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@Log4j2
public class OrderApiTest extends AbstractTest {

  @Autowired private OrderRepository orderRepository;
  @Autowired private ProductRepository productRepository;

  @After
  public void after() {
    orderRepository.deleteAll();
    productRepository.deleteAll();
  }

  @Test
  public void getOrders() {

    ProductPo productPo1 = productRepository.save(ProductUtil.newPoInstance(BigDecimal.TEN));

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7);
    orderItemPo.setProductId(productPo1.getId());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    ProductPo productPo2 = productRepository.save(ProductUtil.newPoInstance(BigDecimal.valueOf(6)));

    OrderItemPo orderItemPo1 = new OrderItemPo();
    orderItemPo1.setQuantity(8);
    orderItemPo1.setProductId(productPo2.getId());

    OrderPo orderPo1 = new OrderPo();
    orderPo1.setCreatedOn(OffsetDateTime.now());
    orderPo1.setLastModified(OffsetDateTime.now());
    orderPo1.setItems(Collections.singletonList(orderItemPo1));

    orderRepository.save(orderPo);
    orderRepository.save(orderPo1);

    ResponseEntity<List<Order>> responseEntity =
        testRestTemplate.exchange(
            "/orders", HttpMethod.GET, null, new ParameterizedTypeReference<List<Order>>() {});

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    List<Order> orders = responseEntity.getBody();

    Assert.assertNotNull(orders);
    Assert.assertEquals(2, orders.size());

    Order order1 = findOrder(orderPo.getId(), orders);
    Assert.assertEquals(BigDecimal.valueOf(70), order1.getPrice());

    Order order2 = findOrder(orderPo1.getId(), orders);
    Assert.assertEquals(BigDecimal.valueOf(48), order2.getPrice());
  }

  private Order findOrder(Long orderId, List<Order> orders) {
    for (Order order : orders) {
      if (order.getId().equals(orderId)) {
        return order;
      }
    }
    return null;
  }

  @Test
  public void getOrder() {

    ProductPo productPo = ProductUtil.newPoInstance(BigDecimal.TEN);
    productPo = productRepository.save(productPo);

    ProductPo productPo2 = ProductUtil.newPoInstance(BigDecimal.valueOf(6));
    productPo2 = productRepository.save(productPo2);

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7);
    orderItemPo.setProductId(productPo.getId());

    OrderItemPo orderItemPo2 = new OrderItemPo();
    orderItemPo2.setQuantity(8);
    orderItemPo2.setProductId(productPo2.getId());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Arrays.asList(orderItemPo, orderItemPo2));

    orderPo = orderRepository.save(orderPo);

    ResponseEntity<Order> responseEntity =
        testRestTemplate.getForEntity("/orders/" + orderPo.getId(), Order.class);

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    Order order = responseEntity.getBody();

    Assert.assertNotNull(order.getId());
    Assert.assertEquals(BigDecimal.valueOf(118), order.getPrice());

    OrderItem orderItem1 = findOrderItem(orderItemPo.getProductId(), order.getItems());
    assert orderItem1 != null;
    Assert.assertEquals(BigDecimal.valueOf(70), orderItem1.getPrice());

    OrderItem orderItem2 = findOrderItem(orderItemPo2.getProductId(), order.getItems());
    Assert.assertEquals(BigDecimal.valueOf(48), orderItem2.getPrice());
  }

  private OrderItem findOrderItem(String productId, List<OrderItem> orderItems) {
    for (OrderItem orderItem : orderItems) {
      if (orderItem.getProductId().equals(productId)) {
        return orderItem;
      }
    }
    return null;
  }

  @Test
  public void putOrder() throws Exception {

    ProductPo productPo = productRepository.save(ProductUtil.newPoInstance());

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(8);
    orderItemPo.setProductId(productPo.getId());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    orderPo = orderRepository.save(orderPo);

    OrderItemInput orderInputItems = new OrderItemInput();
    orderInputItems.setQuantity(7);
    orderInputItems.setProductId(productPo.getId());

    ProductPo productPo2 = productRepository.save(ProductUtil.newPoInstance());

    OrderItemInput orderInputItems2 = new OrderItemInput();
    orderInputItems2.setQuantity(8);
    orderInputItems2.setProductId(productPo2.getId());

    OrderInput orderInput = new OrderInput();
    orderInput.setVersion(orderPo.getVersion());
    orderInput.setItems(Arrays.asList(orderInputItems, orderInputItems2));

    ResponseEntity<Order> responseEntity =
        testRestTemplate.exchange(
            "/orders/" + orderPo.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(orderInput, new HttpHeaders()),
            Order.class);

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    Order order = responseEntity.getBody();

    Assert.assertNotNull(order.getId());
    Assert.assertEquals(orderPo.getId(), order.getId());

    orderPo =
        orderRepository
            .findById(order.getId())
            .orElseThrow(() -> new RuntimeException("Could not find order."));

    Assert.assertNotNull(orderPo.getItems());
    Assert.assertEquals(2, orderPo.getItems().size());

    try (Consumer<String, byte[]> consumer = getConsumer()) {
      embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, OrderProducer.TOPIC);
      Iterator<ConsumerRecord<String, byte[]>> iterator =
          KafkaTestUtils.getRecords(consumer, 1000).iterator();
      ConsumerRecord<String, byte[]> last = Iterators.getLast(iterator);
      OrderOuterClass.Order orderEvent = OrderOuterClass.Order.parseFrom(last.value());
      Assert.assertEquals(order.getId().toString(), orderEvent.getId());
    }
  }

  @Test
  public void postOrder() throws Exception {

    ProductPo productPo = productRepository.save(ProductUtil.newPoInstance());

    OrderItemInput orderInputItems = new OrderItemInput();
    orderInputItems.setQuantity(7);
    orderInputItems.setProductId(productPo.getId());

    OrderInput orderInput = new OrderInput();
    orderInput.setItems(Collections.singletonList(orderInputItems));

    ResponseEntity<Order> responseEntity =
        testRestTemplate.postForEntity("/orders", orderInput, Order.class);

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    Order order = responseEntity.getBody();

    Assert.assertNotNull(order.getId());

    OrderPo orderPo =
        orderRepository
            .findById(order.getId())
            .orElseThrow(() -> new RuntimeException("Could not find order."));

    Assert.assertNotNull(orderPo.getItems());
    Assert.assertEquals(1, orderPo.getItems().size());

    try (Consumer<String, byte[]> consumer = getConsumer()) {
      embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, OrderProducer.TOPIC);
      Iterator<ConsumerRecord<String, byte[]>> iterator =
          KafkaTestUtils.getRecords(consumer, 1000).iterator();
      ConsumerRecord<String, byte[]> last = Iterators.getLast(iterator);
      OrderOuterClass.Order orderEvent = OrderOuterClass.Order.parseFrom(last.value());
      Assert.assertEquals(order.getId().toString(), orderEvent.getId());
    }
  }

  @Test
  public void deleteOrder() {

    ProductPo productPo = productRepository.save(ProductUtil.newPoInstance());

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7);
    orderItemPo.setProductId(productPo.getId());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    orderPo = orderRepository.save(orderPo);

    ResponseEntity<Void> responseEntity =
        testRestTemplate.exchange(
            "/orders/" + orderPo.getId(), HttpMethod.DELETE, null, Void.class);

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    orderPo = orderRepository.findById(orderPo.getId()).orElse(null);

    Assert.assertNull(orderPo);
  }

  private Consumer<String, byte[]> getConsumer() {

    Map<String, Object> props =
        KafkaTestUtils.consumerProps(UUID.randomUUID().toString(), "true", embeddedKafkaBroker);
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);

    DefaultKafkaConsumerFactory<String, byte[]> consumerFactory =
        new DefaultKafkaConsumerFactory<>(props);
    return consumerFactory.createConsumer();
  }
}
