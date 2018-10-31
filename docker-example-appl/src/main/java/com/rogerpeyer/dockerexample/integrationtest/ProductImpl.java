package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.api.ProductsApi;
import com.rogerpeyer.dockerexample.api.model.Product;
import com.rogerpeyer.dockerexample.integrationtest.converter.ProductConverter;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ProductImpl implements ProductsApi {

  private final ProductConverter productConverter;
  private final ProductRepository productRepository;

  @Autowired
  public ProductImpl(
      ProductConverter productConverter,
      ProductRepository productRepository) {
    this.productConverter = productConverter;
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
    return ResponseEntity.ok(productConverter.convert(productPos));
  }

  @Override
  public ResponseEntity<Product> postProduct(@Valid Product product) {
    ProductPo productPo = productConverter.convert(product);
    productPo = productRepository.saveAndFlush(productPo);
    return ResponseEntity.status(HttpStatus.CREATED).body(productConverter.convert(productPo));
  }

  @Override
  public ResponseEntity<Product> getProductByProductId(Long productId) {
    ProductPo productPo = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Could not find Product."));
    return ResponseEntity.ok(productConverter.convert(productPo));
  }

  @Override
  public ResponseEntity<Product> putProductByProductId(Long productId, @Valid Product product) {
    ProductPo productPo = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Could not find Product."));
    productPo = productConverter.convert(product, productPo);
    productPo = productRepository.saveAndFlush(productPo);
    return ResponseEntity.ok(productConverter.convert(productPo));
  }

  @Override
  public ResponseEntity<BigDecimal> getProductByProductIdPrice(Long productId) {
    ProductPo productPo = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Could not find Product."));
    return ResponseEntity.ok(productPo.getPrice());
  }
}
