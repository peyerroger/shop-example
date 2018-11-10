package com.rogerpeyer.dockerexample.controller.order;

import com.rogerpeyer.dockerexample.api.OrdersApi;
import com.rogerpeyer.dockerexample.api.model.Order;
import com.rogerpeyer.dockerexample.api.model.OrderInput;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.dockerexample.persistence.repository.jpa.OrderRepository;
import com.rogerpeyer.dockerexample.service.order.OrderService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrdersApiImpl implements OrdersApi {

  private final OrderService orderService;
  private final OrderRepository orderRepository;

  @Autowired
  public OrdersApiImpl(OrderService orderService, OrderRepository orderRepository) {
    this.orderService = orderService;
    this.orderRepository = orderRepository;
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
    return ResponseEntity.ok(orderService.calculateOutput(orderPo));
  }

  @Override
  public ResponseEntity<List<Order>> getOrders() {
    List<OrderPo> orderPos = orderRepository.findAll();
    return ResponseEntity.ok(orderService.calculateOutput(orderPos));
  }

  @Override
  public ResponseEntity<Order> postOrder(@Valid OrderInput orderInput) {
    OrderPo orderPo = orderService.calculateInput(orderInput, null);
    orderPo = orderRepository.save(orderPo);
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.calculateOutput(orderPo));
  }

  @Override
  public ResponseEntity<Order> putOrderByOrderId(Long orderId, @Valid OrderInput orderInput) {
    OrderPo orderPo =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new RuntimeException("Could not find Order."));
    orderPo = orderService.calculateInput(orderInput, orderPo);
    orderPo = orderRepository.save(orderPo);
    return ResponseEntity.ok(orderService.calculateOutput(orderPo));
  }
}
