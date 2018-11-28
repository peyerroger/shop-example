package com.rogerpeyer.ordermanagement.apicontroller.order;

import com.rogerpeyer.ordermanagement.api.OrdersApi;
import com.rogerpeyer.ordermanagement.api.model.Order;
import com.rogerpeyer.ordermanagement.api.model.OrderInput;
import com.rogerpeyer.ordermanagement.apicontroller.order.converter.OrderConverter;
import com.rogerpeyer.ordermanagement.eventpublisher.order.OrderEventPublisher;
import com.rogerpeyer.ordermanagement.persistence.model.OrderPo;
import com.rogerpeyer.ordermanagement.persistence.repository.jpa.OrderRepository;
import com.rogerpeyer.ordermanagement.service.pricing.OrderPricingService;
import com.rogerpeyer.ordermanagement.service.pricing.model.OrderPricing;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrdersApiImpl implements OrdersApi {

  private final OrderPricingService orderPricingService;
  private final OrderConverter orderConverter;
  private final OrderRepository orderRepository;
  private final OrderEventPublisher orderEventPublisher;

  /**
   * Constructor.
   *
   * @param orderPricingServiceImpl the order pricing service
   * @param orderConverter the order converter
   * @param orderRepository the order repository
   * @param orderEventPublisher the order event producer
   */
  @Autowired
  public OrdersApiImpl(
      OrderPricingService orderPricingServiceImpl,
      OrderConverter orderConverter,
      OrderRepository orderRepository,
      OrderEventPublisher orderEventPublisher) {
    this.orderPricingService = orderPricingServiceImpl;
    this.orderConverter = orderConverter;
    this.orderRepository = orderRepository;
    this.orderEventPublisher = orderEventPublisher;
  }

  @Override
  public ResponseEntity<Void> deleteOrderByOrderId(Long orderId) {
    orderRepository.deleteById(orderId);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Order> getOrderByOrderId(Long orderId) {
    OrderPo orderPo =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new RuntimeException("Could not find Order."));
    OrderPricing orderPricing = orderPricingService.getOrderPricing(orderPo);
    return ResponseEntity.ok(orderConverter.convertOutput(orderPo, orderPricing));
  }

  @Override
  public ResponseEntity<List<Order>> getOrders() {
    List<OrderPo> orderPos = orderRepository.findAll();

    Map<String, OrderPricing> orderIdOrderPricingMap =
        orderPricingService.getOrderIdOrderPricingMap(orderPos);

    return ResponseEntity.ok(orderConverter.convertOutput(orderPos, orderIdOrderPricingMap));
  }

  @Override
  public ResponseEntity<Order> postOrder(@Valid OrderInput orderInput) {
    OrderPo orderPo = orderConverter.convertInput(orderInput, null);
    orderPo = orderRepository.save(orderPo);
    OrderPricing orderPricing = orderPricingService.getOrderPricing(orderPo);
    Order order = orderConverter.convertOutput(orderPo, orderPricing);
    orderEventPublisher.publish(orderPo);
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }

  @Override
  public ResponseEntity<Order> putOrderByOrderId(Long orderId, @Valid OrderInput orderInput) {
    OrderPo orderPo =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new RuntimeException("Could not find Order."));
    orderPo = orderConverter.convertInput(orderInput, orderPo);
    orderPo = orderRepository.save(orderPo);
    OrderPricing orderPricing = orderPricingService.getOrderPricing(orderPo);
    orderEventPublisher.publish(orderPo);
    return ResponseEntity.ok(orderConverter.convertOutput(orderPo, orderPricing));
  }
}
