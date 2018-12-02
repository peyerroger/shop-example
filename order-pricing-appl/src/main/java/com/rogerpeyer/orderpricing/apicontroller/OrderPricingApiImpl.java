package com.rogerpeyer.orderpricing.apicontroller;

import com.rogerpeyer.orderpricing.api.model.Order;
import com.rogerpeyer.orderpricing.api.model.OrderPricing;
import com.rogerpeyer.orderpricing.api.model.OrderPricings;
import com.rogerpeyer.orderpricing.api.model.Orders;
import com.rogerpeyer.orderpricing.service.pricing.OrderPricingService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderPricingApiImpl implements com.rogerpeyer.orderpricing.api.OrdersPricingApi {

  private final OrderPricingService orderPricingService;

  @Autowired
  public OrderPricingApiImpl(OrderPricingService orderPricingService) {
    this.orderPricingService = orderPricingService;
  }

  @Override
  public ResponseEntity<OrderPricing> requestOrderPricing(@Valid Order order) {
    return ResponseEntity.ok(orderPricingService.getOrderPricing(order));
  }

  @Override
  public ResponseEntity<OrderPricings> requestBulkOrderPricing(@Valid Orders orders) {
    return ResponseEntity.ok(orderPricingService.getBulkOrderPricing(orders));
  }
}
