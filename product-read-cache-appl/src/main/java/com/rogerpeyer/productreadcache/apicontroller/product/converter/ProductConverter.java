package com.rogerpeyer.productreadcache.apicontroller.product.converter;

import com.rogerpeyer.productreadcache.api.model.Product;
import com.rogerpeyer.productreadcache.persistence.model.ProductPo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class ProductConverter {

  /**
   * Calculate the output product.
   *
   * @param productPo the persistence object
   * @return the api object.
   */
  public Product calculateOutput(ProductPo productPo) {
    Product product = new Product();
    product.setId(productPo.getId());
    product.setReleaseDate(productPo.getReleaseDate());
    product.setCreatedOn(productPo.getCreatedOn());
    product.setLastModified(productPo.getLastModified());
    product.setName(productPo.getName());
    product.setPrice(productPo.getPrice());
    return product;
  }

  /**
   * Converts a list of persistence objects to a list of api objects.
   *
   * @param productPos the persistence objects
   * @return the api objects.
   */
  public List<Product> calculateOutput(Iterable<ProductPo> productPos) {
    if (productPos == null) {
      return new ArrayList<>();
    } else {
      return StreamSupport.stream(productPos.spliterator(), false)
          .map(this::calculateOutput)
          .collect(Collectors.toList());
    }
  }
}
