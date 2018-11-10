package com.rogerpeyer.dockerexample.integrationtest.util;

import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import java.math.BigDecimal;
import java.time.LocalDate;
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

  private static ProductPo newPoInstance(String id, BigDecimal price) {
    ProductPo productPo = new ProductPo();
    productPo.setId(id);
    productPo.setName(UUID.randomUUID().toString());
    productPo.setPrice(price);
    productPo.setReleaseDate(LocalDate.now().minusYears(1));
    return productPo;
  }
}
