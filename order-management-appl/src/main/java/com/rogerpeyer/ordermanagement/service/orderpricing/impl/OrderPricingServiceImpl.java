package com.rogerpeyer.ordermanagement.service.orderpricing.impl;

import com.rogerpeyer.ordermanagement.persistence.model.OrderPo;
import com.rogerpeyer.ordermanagement.service.orderpricing.OrderPricingService;
import com.rogerpeyer.ordermanagement.service.orderpricing.impl.converter.OrderConverter;
import com.rogerpeyer.orderpricing.client.api.OrderPricingApi;
import com.rogerpeyer.orderpricing.client.api.model.Order;
import com.rogerpeyer.orderpricing.client.api.model.OrderPricing;
import com.rogerpeyer.orderpricing.client.api.model.OrderPricings;
import com.rogerpeyer.orderpricing.client.api.model.Orders;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderPricingServiceImpl implements OrderPricingService {

  private final OrderConverter orderConverter;
  private final OrderPricingApi orderPricingApi;

  @Autowired
  public OrderPricingServiceImpl(OrderConverter orderConverter, OrderPricingApi orderPricingApi) {
    this.orderConverter = orderConverter;
    this.orderPricingApi = orderPricingApi;
  }

  @Override
  public OrderPricing getOrderPricing(OrderPo orderPo) {
    Order order = orderConverter.convert(orderPo);
    return orderPricingApi.requestOrderPricing(order);
  }

  @Override
  public OrderPricings getOrderPricings(Collection<OrderPo> orderPos) {
    Orders orders = orderConverter.convert(orderPos);
    return orderPricingApi.requestBulkOrderPricing(orders);
  }
}
