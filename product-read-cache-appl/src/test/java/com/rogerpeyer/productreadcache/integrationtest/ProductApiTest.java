package com.rogerpeyer.productreadcache.integrationtest;

import com.rogerpeyer.productreadcache.api.model.Product;
import com.rogerpeyer.productreadcache.integrationtest.util.ProductUtil;
import com.rogerpeyer.productreadcache.persistence.model.ProductPo;
import com.rogerpeyer.productreadcache.persistence.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
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

    ProductPo productPo = ProductUtil.newPoInstance();
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

    ProductPo productPo1 = ProductUtil.newPoInstance();
    ProductPo productPo2 = ProductUtil.newPoInstance();
    productRepository.saveAll(Arrays.asList(productPo1, productPo2));

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
