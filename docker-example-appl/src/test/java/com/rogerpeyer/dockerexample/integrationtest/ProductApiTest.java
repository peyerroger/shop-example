package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.api.model.Product;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductApiTest {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private ProductRepository productRepository;

  @After
  public void after() {
    productRepository.deleteAllInBatch();
  }

  @Test
  public void postProduct() {

    Product product = new Product();
    product.setName(UUID.randomUUID().toString());
    product.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    product.setReleaseDate(LocalDate.now().minusYears(1));

    ResponseEntity<Product> responseEntity = testRestTemplate.postForEntity(
        "/products",
        product,
        Product.class
    );

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    Product product1 = responseEntity.getBody();

    Assert.assertNotNull(product1);

  }

  @Test
  public void getProduct() {

    ProductPo productPo = new ProductPo();
    productPo.setName(UUID.randomUUID().toString());
    productPo.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    productPo.setReleaseDate(LocalDate.now().minusYears(1));

    productPo = productRepository.saveAndFlush(productPo);

    ResponseEntity<Product> responseEntity = testRestTemplate.getForEntity(
        "/products/" + productPo.getId(),
        Product.class
    );

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    Product product1 = responseEntity.getBody();

    Assert.assertNotNull(product1);

  }

  @Test
  public void putProduct() {

    ProductPo productPo = new ProductPo();
    productPo.setName(UUID.randomUUID().toString());
    productPo.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    productPo.setReleaseDate(LocalDate.now().minusYears(1));
    productPo = productRepository.saveAndFlush(productPo);

    Product product = new Product();
    product.setName(UUID.randomUUID().toString());
    product.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    product.setReleaseDate(LocalDate.now().minusYears(2));
    product.setId(productPo.getId());
    product.setVersion(productPo.getVersion());

    ResponseEntity<Product> responseEntity = testRestTemplate.exchange(
        "/products/" + productPo.getId(),
        HttpMethod.PUT,
        new HttpEntity<>(product, new HttpHeaders()),
        Product.class
    );

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    Product product1 = responseEntity.getBody();

    Assert.assertNotNull(product1);
    Assert.assertEquals(product.getName(), product1.getName());

  }

  @Test
  public void deleteProduct() {

    ProductPo productPo = new ProductPo();
    productPo.setName(UUID.randomUUID().toString());
    productPo.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    productPo.setReleaseDate(LocalDate.now().minusYears(1));

    productPo = productRepository.saveAndFlush(productPo);

    ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
        "/products/" + productPo.getId(),
        HttpMethod.DELETE,
        null,
        Void.class
    );

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

  }

  @Test
  public void getProducts() {

    ProductPo productPo1 = new ProductPo();
    productPo1.setName(UUID.randomUUID().toString());
    productPo1.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    productPo1.setReleaseDate(LocalDate.now().minusYears(1));
    productRepository.saveAndFlush(productPo1);

    ProductPo productPo2 = new ProductPo();
    productPo2.setName(UUID.randomUUID().toString());
    productPo2.setPrice(BigDecimal.valueOf(new Random().nextDouble()));
    productPo2.setReleaseDate(LocalDate.now().minusYears(1));
    productRepository.saveAndFlush(productPo2);

    ResponseEntity<List<Product>> responseEntity = testRestTemplate.exchange(
        "/products",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Product>>(){}
    );

    Assert.assertNotNull(responseEntity);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assert.assertNotNull(responseEntity.getBody());

    List<Product> products = responseEntity.getBody();

    Assert.assertNotNull(products);
    Assert.assertEquals(2, products.size());

  }


}