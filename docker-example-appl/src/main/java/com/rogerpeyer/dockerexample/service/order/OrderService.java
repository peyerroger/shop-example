package com.rogerpeyer.dockerexample.service.order;

import com.rogerpeyer.dockerexample.api.model.Order;
import com.rogerpeyer.dockerexample.api.model.OrderInput;
import com.rogerpeyer.dockerexample.api.model.OrderItem;
import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private final ProductRepository productRepository;

  @Autowired
  public OrderService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  /**
   * Calculate the output product.
   *
   * @param orderPo the persistence object
   * @return the api object.
   */
  public Order calculateOutput(OrderPo orderPo) {
    Order order = new Order();
    order.setId(orderPo.getId());
    order.setVersion(orderPo.getVersion());
    order.setCreatedOn(orderPo.getCreatedOn());
    order.setLastModified(orderPo.getLastModified());
    if (orderPo.getItems() != null) {
      order.setItems(
          orderPo
              .getItems()
              .stream()
              .map(
                  orderItemPo -> {
                    OrderItem orderItems = new OrderItem();
                    orderItems.setQuantity(orderItemPo.getQuantity());
                    orderItems.setProductId(orderItemPo.getProductId());
                    return orderItems;
                  })
              .collect(Collectors.toList()));
    }
    return calculatePrices(order);
  }

  /**
   * Converts a list of persistence objects to a list of api objects.
   *
   * @param orderPos the persistence objects
   * @return the api objects.
   */
  public List<Order> calculateOutput(List<OrderPo> orderPos) {
    if (orderPos == null) {
      return new ArrayList<>();
    } else {
      return orderPos.stream().map(this::calculateOutput).collect(Collectors.toList());
    }
  }

  /**
   * Merges an existing persistence object with an api object.
   *
   * @param orderInput the api object
   * @param orderPo the existing persistence object
   */
  public OrderPo calculateInput(OrderInput orderInput, OrderPo orderPo) {
    if (orderPo == null) {
      orderPo = new OrderPo();
    } else {
      orderPo.setVersion(orderInput.getVersion());
    }
    if (orderInput.getItems() != null) {
      orderPo.setItems(
          orderInput
              .getItems()
              .stream()
              .map(
                  orderInputItems -> {
                    OrderItemPo orderItemPo = new OrderItemPo();
                    orderItemPo.setQuantity(orderInputItems.getQuantity());
                    orderItemPo.setProductId(orderInputItems.getProductId());
                    return orderItemPo;
                  })
              .collect(Collectors.toList()));
    } else {
      orderPo.setItems(null);
    }
    return orderPo;
  }

  private Order calculatePrices(Order order) {

    // Create Price Map
    Map<String, BigDecimal> priceMap =
        StreamSupport.stream(
                productRepository
                    .findAllById(
                        order
                            .getItems()
                            .stream()
                            .map(OrderItem::getProductId)
                            .collect(Collectors.toSet()))
                    .spliterator(),
                false)
            .collect(Collectors.toMap(ProductPo::getId, ProductPo::getPrice));

    // Set Price of each order item.
    order
        .getItems()
        .forEach(orderItemPo -> orderItemPo.setPrice(priceMap.get(orderItemPo.getProductId())));

    // Set order price
    order.setPrice(
        order
            .getItems()
            .stream()
            .map(OrderItem::getPrice)
            .collect(Collectors.toList())
            .stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add));

    return order;
  }
}
