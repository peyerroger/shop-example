package com.rogerpeyer.orderpricing.service.pricing.impl;

import com.rogerpeyer.orderpricing.persistence.model.ProductPo;
import com.rogerpeyer.orderpricing.persistence.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductPriceCollector {

  private final ProductRepository productRepository;

  @Autowired
  public ProductPriceCollector(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  Map<String, BigDecimal> getProductUnitPriceMap(
      com.rogerpeyer.orderpricing.api.model.Order orderPo) {
    return StreamSupport.stream(
            productRepository.findAllById(collectProductIds(orderPo)).spliterator(), false)
        .collect(Collectors.toMap(ProductPo::getId, ProductPo::getPrice));
  }

  Map<String, BigDecimal> getProductUnitPriceMap(
      com.rogerpeyer.orderpricing.api.model.Orders orderPos) {
    return StreamSupport.stream(
            productRepository.findAllById(collectProductIds(orderPos)).spliterator(), false)
        .collect(Collectors.toMap(ProductPo::getId, ProductPo::getPrice));
  }

  private Set<String> collectProductIds(com.rogerpeyer.orderpricing.api.model.Order orderPo) {
    return orderPo
        .getItems()
        .stream()
        .map(com.rogerpeyer.orderpricing.api.model.OrderItem::getProductId)
        .collect(Collectors.toSet());
  }

  private Set<String> collectProductIds(com.rogerpeyer.orderpricing.api.model.Orders orderPos) {
    return orderPos
        .getOrders()
        .stream()
        .flatMap(orderPo -> collectProductIds(orderPo).stream())
        .collect(Collectors.toSet());
  }
}
