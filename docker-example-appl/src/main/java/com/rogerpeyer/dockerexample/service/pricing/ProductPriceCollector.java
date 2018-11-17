package com.rogerpeyer.dockerexample.service.pricing;

import com.rogerpeyer.dockerexample.persistence.model.OrderItemPo;
import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
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

  Map<String, BigDecimal> getProductUnitPriceMap(OrderPo orderPo) {
    return StreamSupport.stream(
            productRepository.findAllById(collectProductIds(orderPo)).spliterator(), false)
        .collect(Collectors.toMap(ProductPo::getId, ProductPo::getPrice));
  }

  Map<String, BigDecimal> getProductUnitPriceMap(List<OrderPo> orderPos) {
    return StreamSupport.stream(
            productRepository.findAllById(collectProductIds(orderPos)).spliterator(), false)
        .collect(Collectors.toMap(ProductPo::getId, ProductPo::getPrice));
  }

  private Set<String> collectProductIds(OrderPo orderPo) {
    return orderPo.getItems().stream().map(OrderItemPo::getProductId).collect(Collectors.toSet());
  }

  private Set<String> collectProductIds(List<OrderPo> orderPos) {
    return orderPos
        .stream()
        .flatMap(orderPo -> collectProductIds(orderPo).stream())
        .collect(Collectors.toSet());
  }
}
