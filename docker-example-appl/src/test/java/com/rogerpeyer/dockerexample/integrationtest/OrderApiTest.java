package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.api.model.Order;
import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.dockerexample.persistence.repository.redis.OrderRepository;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OrderApiTest extends ApiTest {

  @Autowired
  private OrderRepository orderRepository;

  @Test
  public void getOrder() {

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7);
    orderItemPo.setProductId(new Random().nextLong());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    orderPo = orderRepository.save(orderPo);

    ResponseEntity<Order> responseEntity = testRestTemplate.getForEntity(
        "/orders/" + orderPo.getId(),
        Order.class
    );

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    Order order = responseEntity.getBody();

    Assert.assertNotNull(order.getId());

  }

}
