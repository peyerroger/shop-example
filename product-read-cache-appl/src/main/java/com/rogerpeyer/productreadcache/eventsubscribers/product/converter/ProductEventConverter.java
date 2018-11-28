package com.rogerpeyer.productreadcache.eventsubscribers.product.converter;

import com.google.protobuf.Timestamp;
import com.rogerpeyer.product.event.spi.ProductOuterClass.Product;
import com.rogerpeyer.productreadcache.persistence.model.ProductPo;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
    productPo.setName(product.getName());
    productPo.setPrice(new BigDecimal(product.getPrice()));
    productPo.setReleaseDate(convertToLocalDate(product.getReleaseDate()));
    productPo.setCreatedOn(convertToLocalDateTime(product.getCreatedOn()));
    productPo.setLastModified(convertToLocalDateTime(product.getLastModified()));
    return productPo;
  }

  private LocalDate convertToLocalDate(Timestamp timestamp) {
    return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()), ZoneId.of("UTC"))
        .toLocalDate();
  }

  private OffsetDateTime convertToLocalDateTime(Timestamp timestamp) {
    return OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()), ZoneId.of("UTC"));
  }
}
