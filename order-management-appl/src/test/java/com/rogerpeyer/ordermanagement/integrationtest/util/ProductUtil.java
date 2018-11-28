package com.rogerpeyer.ordermanagement.integrationtest.util;

import com.rogerpeyer.ordermanagement.persistence.model.ProductPo;
import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public class ProductUtil {

  private ProductUtil() {}

  /**
   * Generates a new product persistence object.
   *
   * @return the persistence object
   */
  public static ProductPo newPoInstance() {
    return newPoInstance(
        UUID.randomUUID().toString(), BigDecimal.valueOf(new Random().nextDouble()));
  }

  public static ProductPo newPoInstance(BigDecimal price) {
    return newPoInstance(UUID.randomUUID().toString(), price);
  }

  private static ProductPo newPoInstance(String id, BigDecimal price) {
    ProductPo productPo = new ProductPo();
    productPo.setId(id);
    productPo.setPrice(price);
    return productPo;
  }
}
