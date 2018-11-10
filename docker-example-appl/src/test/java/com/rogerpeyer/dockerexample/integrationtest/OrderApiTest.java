package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.api.model.Order;
import com.rogerpeyer.dockerexample.api.model.OrderInput;
import com.rogerpeyer.dockerexample.api.model.OrderInputItems;
import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.dockerexample.persistence.repository.jpa.OrderRepository;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

public class OrderApiTest extends AbstractTest {

  @Autowired private OrderRepository orderRepository;

  @After
  public void after() {
    orderRepository.deleteAll();
  }

  @Test
  public void getOrders() {

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7);
    orderItemPo.setProductId(UUID.randomUUID().toString());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    OrderItemPo orderItemPo1 = new OrderItemPo();
    orderItemPo1.setQuantity(7);
    orderItemPo1.setProductId(UUID.randomUUID().toString());

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
  }

  @Test
  public void getOrder() {

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7);
    orderItemPo.setProductId(UUID.randomUUID().toString());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    orderPo = orderRepository.save(orderPo);

    ResponseEntity<Order> responseEntity =
        testRestTemplate.getForEntity("/orders/" + orderPo.getId(), Order.class);

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    Order order = responseEntity.getBody();

    Assert.assertNotNull(order.getId());
  }

  @Test
  public void putOrder() {

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(8);
    orderItemPo.setProductId(UUID.randomUUID().toString());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    orderPo = orderRepository.save(orderPo);

    OrderInputItems orderInputItems = new OrderInputItems();
    orderInputItems.setQuantity(7);
    orderInputItems.setProductId(UUID.randomUUID().toString());

    OrderInputItems orderInputItems2 = new OrderInputItems();
    orderInputItems2.setQuantity(7);
    orderInputItems2.setProductId(UUID.randomUUID().toString());

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
  }

  @Test
  public void postOrder() {

    OrderInputItems orderInputItems = new OrderInputItems();
    orderInputItems.setQuantity(7);
    orderInputItems.setProductId(UUID.randomUUID().toString());

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
  }

  @Test
  public void deleteOrder() {

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7);
    orderItemPo.setProductId(UUID.randomUUID().toString());

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
}
