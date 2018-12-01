package com.rogerpeyer.orderreadcache.integrationtest;

import com.rogerpeyer.orderreadcache.api.model.Order;
import com.rogerpeyer.orderreadcache.integrationtest.util.ProductUtil;
import com.rogerpeyer.orderreadcache.persistence.model.OrderItemPo;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.persistence.model.ProductPo;
import com.rogerpeyer.orderreadcache.persistence.repository.OrderRepository;
import com.rogerpeyer.orderreadcache.persistence.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Log4j2
public class ProductsApiTest extends AbstractTest {

  @Autowired private OrderRepository orderRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private RedisTemplate<String, String> redisTemplate;

  @After
  public void after() {
    orderRepository.deleteAll();
    productRepository.deleteAll();
  }

  @Test
  public void getOrdersByProduct() {

    ProductPo productPo = productRepository.save(ProductUtil.newPoInstance(BigDecimal.TEN));

    OrderItemPo orderItemPo = new OrderItemPo();
    orderItemPo.setQuantity(7L);
    orderItemPo.setProductId(productPo.getId());

    OrderPo orderPo = new OrderPo();
    orderPo.setCreatedOn(OffsetDateTime.now());
    orderPo.setLastModified(OffsetDateTime.now());
    orderPo.setItems(Collections.singletonList(orderItemPo));

    OrderItemPo orderItemPo1 = new OrderItemPo();
    orderItemPo1.setQuantity(8L);
    orderItemPo1.setProductId(productPo.getId());

    OrderPo orderPo1 = new OrderPo();
    orderPo1.setCreatedOn(OffsetDateTime.now());
    orderPo1.setLastModified(OffsetDateTime.now());
    orderPo1.setItems(Collections.singletonList(orderItemPo1));

    orderPo = orderRepository.save(orderPo);
    orderPo1 = orderRepository.save(orderPo1);

    redisTemplate.opsForSet().add(productPo.getId(), orderPo.getId(), orderPo1.getId());

    ResponseEntity<List<Order>> responseEntity =
        testRestTemplate.exchange(
            "/products/" + productPo.getId() + "/orders",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Order>>() {});

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    List<Order> orders = responseEntity.getBody();

    Assert.assertNotNull(orders);
    Assert.assertEquals(2, orders.size());
  }
}
