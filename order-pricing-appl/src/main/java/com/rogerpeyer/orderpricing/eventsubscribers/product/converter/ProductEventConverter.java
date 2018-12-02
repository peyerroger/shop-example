package com.rogerpeyer.orderpricing.eventsubscribers.product.converter;

import com.rogerpeyer.orderpricing.persistence.model.ProductPo;
import com.rogerpeyer.product.event.spi.ProductOuterClass.Product;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ProductEventConverter {

  /**
   * Calculates the persistent object.
   *
   * @param product the product event.
   * @param productPo the persistent object.
   * @return the persistent object.
   */
  public ProductPo convert(Product product, @NotNull ProductPo productPo) {
    productPo.setId(product.getId());
    productPo.setPrice(new BigDecimal(product.getPrice()));
    return productPo;
  }
}
