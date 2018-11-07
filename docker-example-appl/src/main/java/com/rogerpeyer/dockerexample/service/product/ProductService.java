package com.rogerpeyer.dockerexample.service.product;

import com.rogerpeyer.dockerexample.api.model.Product;
import com.rogerpeyer.dockerexample.api.model.ProductInput;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  /**
   * Calculate the output product.
   *
   * @param productPo the persistence object
   * @return the api object.
   */
  public Product calculateOutput(ProductPo productPo) {
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
  public List<Product> calculateOutput(List<ProductPo> productPos) {
    if (productPos == null) {
      return new ArrayList<>();
    } else {
      return productPos.stream().map(this::calculateOutput).collect(Collectors.toList());
    }
  }

  /**
   * Merges an existing persistence object with an api object.
   *
   * @param productInput the api object
   * @param productPo    the existing persistence object
   */
  public ProductPo calculateInput(ProductInput productInput, ProductPo productPo) {
    if (productPo == null) {
      productPo = new ProductPo();
    } else {
      productPo.setVersion(productInput.getVersion());
    }
    productPo.setReleaseDate(productInput.getReleaseDate());
    productPo.setName(productInput.getName());
    productPo.setPrice(productInput.getPrice());
    return productPo;
  }

}
