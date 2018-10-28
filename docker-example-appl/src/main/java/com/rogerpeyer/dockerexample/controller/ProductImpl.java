package com.rogerpeyer.dockerexample.controller;

import com.rogerpeyer.dockerexample.api.ProductsApi;
import com.rogerpeyer.dockerexample.api.model.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ProductImpl implements ProductsApi {

  @Override
  public ResponseEntity<Void> deleteProductByProductId(String productId) {
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<Product>> getProducts() {
    Product product = new Product();
    product.setCreatedOn(OffsetDateTime.now());
    product.setId(UUID.randomUUID().toString());
    product.setPrice(BigDecimal.ONE);
    product.setReleaseDate(LocalDate.now());
    return ResponseEntity.ok(Collections.singletonList(product));
  }

  @Override
  public ResponseEntity<Product> postProduct(@Valid Product product) {
    return ResponseEntity.status(HttpStatus.CREATED).body(product);
  }

  @Override
  public ResponseEntity<Product> getProductByProductId(String productId) {
    return null;
  }

  @Override
  public ResponseEntity<Product> putProductByProductId(String productId, @Valid Product product) {
    return ResponseEntity.ok(product);
  }

  @Override
  public ResponseEntity<BigDecimal> getProductByProductIdPrice(String productId) {
    return ResponseEntity.ok(BigDecimal.ONE);
  }
}
