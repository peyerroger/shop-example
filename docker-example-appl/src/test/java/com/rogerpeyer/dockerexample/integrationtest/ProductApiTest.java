package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.api.model.Product;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ProductApiTest extends AbstractTest {

  @Autowired private ProductRepository productRepository;

  @After
  public void after() {
    productRepository.deleteAll();
  }

  @Test
  public void getProduct() {

    ProductPo productPo = new ProductPo();
    productPo.setId(UUID.randomUUID().toString());
    productPo.setName(UUID.randomUUID().toString());
    productPo.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    productPo.setReleaseDate(LocalDate.now().minusYears(1));

    productPo = productRepository.save(productPo);

    ResponseEntity<Product> responseEntity =
        testRestTemplate.getForEntity("/products/" + productPo.getId(), Product.class);

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    Product product1 = responseEntity.getBody();

    Assert.assertNotNull(product1.getId());
  }


  @Test
  public void getProducts() {

    ProductPo productPo1 = new ProductPo();
    productPo1.setId(UUID.randomUUID().toString());
    productPo1.setName(UUID.randomUUID().toString());
    productPo1.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    productPo1.setReleaseDate(LocalDate.now().minusYears(1));
    productRepository.save(productPo1);

    ProductPo productPo2 = new ProductPo();
    productPo2.setId(UUID.randomUUID().toString());
    productPo2.setName(UUID.randomUUID().toString());
    productPo2.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    productPo2.setReleaseDate(LocalDate.now().minusYears(1));
    productRepository.save(productPo2);

    ResponseEntity<List<Product>> responseEntity =
        testRestTemplate.exchange(
            "/products", HttpMethod.GET, null, new ParameterizedTypeReference<List<Product>>() {});

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    List<Product> products = responseEntity.getBody();

    Assert.assertNotNull(products);
    Assert.assertEquals(2, products.size());
  }
}
