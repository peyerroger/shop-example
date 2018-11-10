package com.rogerpeyer.dockerexample.controller.product;

import com.rogerpeyer.dockerexample.api.ProductsApi;
import com.rogerpeyer.dockerexample.api.model.Product;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import com.rogerpeyer.dockerexample.service.product.ProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ProductsApiImpl implements ProductsApi {

  private final ProductService productService;
  private final ProductRepository productRepository;

  /**
   * Constructor.
   *
   * @param productService the product converter
   * @param productRepository the product repository
   */
  @Autowired
  public ProductsApiImpl(ProductService productService, ProductRepository productRepository) {
    this.productService = productService;
    this.productRepository = productRepository;
  }

  @Override
  public ResponseEntity<List<Product>> getProducts() {
    Iterable<ProductPo> productPos = productRepository.findAll();
    return ResponseEntity.ok(productService.calculateOutput(productPos));
  }

  @Override
  public ResponseEntity<Product> getProductByProductId(String productId) {
    ProductPo productPo =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Could not find Product."));
    return ResponseEntity.ok(productService.calculateOutput(productPo));
  }
}
