package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.api.model.Order;
import com.rogerpeyer.dockerexample.api.model.OrderInput;
import com.rogerpeyer.dockerexample.api.model.OrderItemInput;
import com.rogerpeyer.dockerexample.integrationtest.util.ProductUtil;
import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.jpa.OrderRepository;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
  @Autowired private ProductRepository productRepository;

  @After
  public void after() {
    orderRepository.deleteAll();
    productRepository.deleteAll();
  }

  @Test
  public void getOrders() {

    ProductPo productPo1 = productRepository.save(ProductUtil.newPoInstance());

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7);
    orderItemPo.setProductId(productPo1.getId());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    ProductPo productPo2 = productRepository.save(ProductUtil.newPoInstance());

    OrderItemPo orderItemPo1 = new OrderItemPo();
    orderItemPo1.setQuantity(7);
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
  }

  @Test
  public void getOrder() {

    ProductPo productPo = ProductUtil.newPoInstance();
    productPo = productRepository.save(productPo);

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7);
    orderItemPo.setProductId(productPo.getId());

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
  }

  @Test
  public void postOrder() {

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
}
