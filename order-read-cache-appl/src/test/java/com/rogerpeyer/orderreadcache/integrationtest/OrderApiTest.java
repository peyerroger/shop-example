package com.rogerpeyer.orderreadcache.integrationtest;

import com.rogerpeyer.orderreadcache.api.model.Order;
import com.rogerpeyer.orderreadcache.api.model.OrderItem;
import com.rogerpeyer.orderreadcache.integrationtest.util.ProductUtil;
import com.rogerpeyer.orderreadcache.persistence.model.OrderItemPo;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.persistence.model.ProductPo;
import com.rogerpeyer.orderreadcache.persistence.repository.redis.OrderRepository;
import com.rogerpeyer.orderreadcache.persistence.repository.redis.ProductRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    orderItemPo.setQuantity(7L);
    orderItemPo.setProductId(productPo1.getId());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    ProductPo productPo2 = productRepository.save(ProductUtil.newPoInstance(BigDecimal.valueOf(6)));

    OrderItemPo orderItemPo1 = new OrderItemPo();
    orderItemPo1.setQuantity(8L);
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

  private Order findOrder(String orderId, List<Order> orders) {
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
    orderItemPo.setQuantity(7L);
    orderItemPo.setProductId(productPo.getId());

    OrderItemPo orderItemPo2 = new OrderItemPo();
    orderItemPo2.setQuantity(8L);
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
}
