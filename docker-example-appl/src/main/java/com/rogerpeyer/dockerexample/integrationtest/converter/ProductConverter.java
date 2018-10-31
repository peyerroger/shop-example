package com.rogerpeyer.dockerexample.integrationtest.converter;


import com.rogerpeyer.dockerexample.api.model.Product;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

  public Product convert(ProductPo productPo){
    Product product = new Product();
    product.setId(productPo.getId());
    product.setVersion(productPo.getVersion());
    product.setReleaseDate(productPo.getReleaseDate());
    product.setCreatedOn(productPo.getCreatedOn());
    product.setLastModified(productPo.getLastModified());
    product.setName(productPo.getName());
    product.setPrice(productPo.getPrice());
    return product;
  }

  public List<Product> convert(List<ProductPo> productPos) {
    if (productPos == null){
      return new ArrayList<>();
    } else {
      return productPos.stream().map(this::convert).collect(Collectors.toList());
    }
  }

  public ProductPo convert(Product product){
    ProductPo productPo = new ProductPo();
    productPo.setReleaseDate(product.getReleaseDate());
    productPo.setName(product.getName());
    productPo.setPrice(product.getPrice());
    return productPo;
  }

  public ProductPo convert(Product product, ProductPo oldProductPo){
    oldProductPo.setVersion(product.getVersion());
    oldProductPo.setReleaseDate(product.getReleaseDate());
    oldProductPo.setName(product.getName());
    oldProductPo.setPrice(product.getPrice());
    return oldProductPo;
  }

}
