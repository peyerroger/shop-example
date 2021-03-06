package com.rogerpeyer.productreadcache.apicontroller.product;

import com.rogerpeyer.productreadcache.api.ProductsApi;
import com.rogerpeyer.productreadcache.api.model.Product;
import com.rogerpeyer.productreadcache.apicontroller.product.converter.ProductConverter;
import com.rogerpeyer.productreadcache.persistence.model.ProductPo;
import com.rogerpeyer.productreadcache.persistence.repository.ProductRepository;
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
