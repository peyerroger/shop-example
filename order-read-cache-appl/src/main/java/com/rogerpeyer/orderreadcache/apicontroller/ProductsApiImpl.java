package com.rogerpeyer.orderreadcache.apicontroller;

import com.rogerpeyer.orderreadcache.api.ProductsApi;
import com.rogerpeyer.orderreadcache.api.model.Order;
import com.rogerpeyer.orderreadcache.apicontroller.converter.OrderConverter;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.persistence.repository.OrderRepository;
import com.rogerpeyer.orderreadcache.service.pricing.OrderPricingService;
import com.rogerpeyer.orderreadcache.service.pricing.model.OrderPricing;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductsApiImpl implements ProductsApi {

  private final RedisTemplate<String, String> redisTemplate;
  private final OrderRepository orderRepository;
  private final OrderPricingService orderPricingService;
  private final OrderConverter orderConverter;

  /**
   * Constructor.
   *
   * @param redisTemplate the redis template
   * @param orderRepository the order repository
   * @param orderPricingService the order pricing service
   * @param orderConverter the order converter
   */
  @Autowired
  public ProductsApiImpl(
      RedisTemplate<String, String> redisTemplate,
      OrderRepository orderRepository,
      OrderPricingService orderPricingService,
      OrderConverter orderConverter) {
    this.redisTemplate = redisTemplate;
    this.orderRepository = orderRepository;
    this.orderPricingService = orderPricingService;
    this.orderConverter = orderConverter;
  }

  @Override
  public ResponseEntity<List<Order>> getOrdersByProductsId(String productId) {
    Set<String> members = redisTemplate.opsForSet().members(productId);

    Iterable<OrderPo> orderPos = orderRepository.findAllById(members);
    Map<String, OrderPricing> orderIdOrderPricingMap =
        orderPricingService.getOrderIdOrderPricingMap(orderPos);

    return ResponseEntity.ok(orderConverter.convertOutput(orderPos, orderIdOrderPricingMap));
  }
}
