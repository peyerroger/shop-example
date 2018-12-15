package com.rogerpeyer.ordermanagement.service.orderpricing;

import com.rogerpeyer.ordermanagement.persistence.model.OrderPo;
import com.rogerpeyer.orderpricing.client.api.model.OrderPricing;
import com.rogerpeyer.orderpricing.client.api.model.OrderPricings;
import java.util.Collection;

public interface OrderPricingService {

  OrderPricing getOrderPricing(OrderPo orderPo);

  OrderPricings getOrderPricings(Collection<OrderPo> orderPos);
}
