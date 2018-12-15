package com.rogerpeyer.ordermanagement.integrationtest.util.wiremock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.rogerpeyer.orderpricing.client.api.model.Order;
import com.rogerpeyer.orderpricing.client.api.model.OrderItemPricing;
import com.rogerpeyer.orderpricing.client.api.model.OrderPricing;
import com.rogerpeyer.orderpricing.client.api.model.OrderPricings;
import com.rogerpeyer.orderpricing.client.api.model.Orders;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

public class OrderPricingTransformer extends ResponseTransformer {

  public static final String NAME = "OrderPricingTransformer";

  private ObjectMapper objectMapper = new ObjectMapper();
  private Random random = new Random();

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Response transform(
      Request request, Response response, FileSource files, Parameters parameters) {
    try {
      byte[] body;
      if (request.getUrl().contains("/orders-pricing/bulk")) {
        Orders orders = objectMapper.readValue(request.getBody(), Orders.class);
        OrderPricings orderPricings = new OrderPricings();
        orderPricings.setOrderPricings(
            orders.getOrders().stream().map(this::getOrderPricing).collect(Collectors.toList()));
        body = objectMapper.writeValueAsBytes(orderPricings);
      } else {
        Order order = objectMapper.readValue(request.getBody(), Order.class);
        OrderPricing orderPricing = getOrderPricing(order);
        body = objectMapper.writeValueAsBytes(orderPricing);
      }

      return Response.Builder.like(response).but().body(body).build();
    } catch (IOException e) {
      return Response.Builder.like(response)
          .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .build();
    }
  }

  private OrderPricing getOrderPricing(Order order) {
    OrderPricing orderPricing = new OrderPricing();
    orderPricing.setOrderId(order.getId());
    orderPricing.setPrice(BigDecimal.valueOf(random.nextDouble()));
    orderPricing.setOrderItemPricings(
        order
            .getItems()
            .stream()
            .map(
                orderItem -> {
                  OrderItemPricing orderItemPricing = new OrderItemPricing();
                  orderItemPricing.setPrice(BigDecimal.valueOf(random.nextDouble()));
                  orderItemPricing.setProductId(orderItem.getProductId());
                  return orderItemPricing;
                })
            .collect(Collectors.toList()));
    return orderPricing;
  }
}
