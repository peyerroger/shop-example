package com.rogerpeyer.dockerexample.controller.converter;

import com.rogerpeyer.dockerexample.api.model.Product;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

  /**
   * Converts a persistence object to an api object.
   *
   * @param productPo the persistence object
   * @return the api object.
   */
  public Product convert(ProductPo productPo) {
    Product product = new Product();
    product.setId(productPo.getId());
    product.setVersion(productPo.getVersion());
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
  public List<Product> convert(List<ProductPo> productPos) {
    if (productPos == null) {
      return new ArrayList<>();
    } else {
      return productPos.stream().map(this::convert).collect(Collectors.toList());
    }
  }

  /**
   * Converts an api object to a persistence object.
   *
   * @param product the api object.
   * @return the persistence object.
   */
  public ProductPo convert(Product product) {
    ProductPo productPo = new ProductPo();
    productPo.setReleaseDate(product.getReleaseDate());
    productPo.setName(product.getName());
    productPo.setPrice(product.getPrice());
    return productPo;
  }

  /**
   * Merges an existing persistence object with an api object.
   *
   * @param product the api object
   * @param existingProductPo the existing persistence object
   */
  public ProductPo merge(Product product, ProductPo existingProductPo) {
    existingProductPo.setVersion(product.getVersion());
    existingProductPo.setReleaseDate(product.getReleaseDate());
    existingProductPo.setName(product.getName());
    existingProductPo.setPrice(product.getPrice());
    return existingProductPo;
  }

}
