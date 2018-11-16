package com.rogerpeyer.dockerexample.controller.product;

import com.rogerpeyer.dockerexample.api.ProductsApi;
import com.rogerpeyer.dockerexample.api.model.Product;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import com.rogerpeyer.dockerexample.controller.product.converter.ProductConverter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ProductsApiImpl implements ProductsApi {

  private final ProductConverter productConverter;
  private final ProductRepository productRepository;

  /**
   * Constructor.
   *
   * @param productConverter the product converter
   * @param productRepository the product repository
   */
  @Autowired
  public ProductsApiImpl(ProductConverter productConverter, ProductRepository productRepository) {
    this.productConverter = productConverter;
    this.productRepository = productRepository;
  }

  @Override
  public ResponseEntity<List<Product>> getProducts() {
    Iterable<ProductPo> productPos = productRepository.findAll();
    return ResponseEntity.ok(productConverter.calculateOutput(productPos));
  }

  @Override
  public ResponseEntity<Product> getProductByProductId(String productId) {
    ProductPo productPo =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Could not find Product."));
    return ResponseEntity.ok(productConverter.calculateOutput(productPo));
  }
}
