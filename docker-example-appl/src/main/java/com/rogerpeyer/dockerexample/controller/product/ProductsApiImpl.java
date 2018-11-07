package com.rogerpeyer.dockerexample.controller.product;

import com.rogerpeyer.dockerexample.api.ProductsApi;
import com.rogerpeyer.dockerexample.api.model.Product;
import com.rogerpeyer.dockerexample.api.model.ProductInput;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.jpa.ProductRepository;
import com.rogerpeyer.dockerexample.service.product.ProductService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ProductsApiImpl implements ProductsApi {

  private final ProductService productService;
  private final ProductRepository productRepository;

  /**
   * Constructor.
   *
   * @param productService    the product converter
   * @param productRepository the product repository
   */
  @Autowired
  public ProductsApiImpl(
      ProductService productService,
      ProductRepository productRepository) {
    this.productService = productService;
    this.productRepository = productRepository;
  }

  @Override
  public ResponseEntity<Void> deleteProductByProductId(Long productId) {
    ProductPo productPo = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Could not find Product."));
    productRepository.delete(productPo);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<Product>> getProducts() {
    List<ProductPo> productPos = productRepository.findAll();
    return ResponseEntity.ok(productService.calculateOutput(productPos));
  }

  @Override
  public ResponseEntity<Product> postProduct(@Valid ProductInput productInput) {
    ProductPo productPo = productService.calculateInput(productInput, null);
    productPo = productRepository.saveAndFlush(productPo);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(productService.calculateOutput(productPo));
  }

  @Override
  public ResponseEntity<Product> getProductByProductId(Long productId) {
    ProductPo productPo = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Could not find Product."));
    return ResponseEntity.ok(productService.calculateOutput(productPo));
  }

  @Override
  public ResponseEntity<Product> putProductByProductId(Long productId,
      @Valid ProductInput productInput) {
    ProductPo productPo = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Could not find Product."));
    productPo = productService.calculateInput(productInput, productPo);
    productPo = productRepository.saveAndFlush(productPo);
    return ResponseEntity.ok(productService.calculateOutput(productPo));
  }
}
