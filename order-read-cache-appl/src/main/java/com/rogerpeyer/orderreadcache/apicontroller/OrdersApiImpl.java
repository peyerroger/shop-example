package com.rogerpeyer.orderreadcache.apicontroller;

import com.rogerpeyer.orderreadcache.api.OrdersApi;
import com.rogerpeyer.orderreadcache.api.model.Order;
import com.rogerpeyer.orderreadcache.apicontroller.converter.OrderConverter;
import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import com.rogerpeyer.orderreadcache.persistence.repository.OrderRepository;
import com.rogerpeyer.orderreadcache.service.pricing.OrderPricingService;
import com.rogerpeyer.orderreadcache.service.pricing.model.OrderPricing;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrdersApiImpl implements OrdersApi {

  private final OrderPricingService orderPricingService;
  private final OrderConverter orderConverter;
  private final OrderRepository orderRepository;

  /**
   * Constructor.
   *
   * @param orderPricingServiceImpl the order pricing service
   * @param orderConverter the order converter
   * @param orderRepository the order repository
   */
  @Autowired
  public OrdersApiImpl(
      OrderPricingService orderPricingServiceImpl,
      OrderConverter orderConverter,
      OrderRepository orderRepository) {
    this.orderPricingService = orderPricingServiceImpl;
    this.orderConverter = orderConverter;
    this.orderRepository = orderRepository;
  }

  @Override
  public ResponseEntity<Order> getOrderByOrderId(String orderId) {
    OrderPo orderPo =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new RuntimeException("Could not find Order."));
    OrderPricing orderPricing = orderPricingService.getOrderPricing(orderPo);
    return ResponseEntity.ok(orderConverter.convertOutput(orderPo, orderPricing));
  }

  @Override
  public ResponseEntity<List<Order>> getOrders() {
    Iterable<OrderPo> orderPos = orderRepository.findAll();

    Map<String, OrderPricing> orderIdOrderPricingMap =
        orderPricingService.getOrderIdOrderPricingMap(orderPos);

    return ResponseEntity.ok(orderConverter.convertOutput(orderPos, orderIdOrderPricingMap));
  }
}
