package com.rogerpeyer.orderpricing.service.pricing;

import com.rogerpeyer.orderpricing.api.model.Order;
import com.rogerpeyer.orderpricing.api.model.OrderPricing;
import com.rogerpeyer.orderpricing.api.model.OrderPricings;
import com.rogerpeyer.orderpricing.api.model.Orders;

public interface OrderPricingService {

  /**
   * Get the order pricing.
   *
   * @param order the order to be priced
   * @return the order pricing
   */
  OrderPricing getOrderPricing(Order order);

  /**
   * Get the order pricing per order id map.
   *
   * @param orders the orders to be priced
   * @return the order pricing per order id map
   */
  OrderPricings getBulkOrderPricing(Orders orders);
}
