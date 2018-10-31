package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.api.model.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka
public class ProductApiTest {

  @Autowired
  private TestRestTemplate testRestTemplate;

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
    Assert.assertEquals(product.getName(), product1.getName());

  }

}