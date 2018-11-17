package com.rogerpeyer.dockerexample.eventlistener.product;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rogerpeyer.dockerexample.eventlistener.product.converter.ProductConverter;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import com.rogerpeyer.spi.proto.ProductOuterClass.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductListener {

  public static final String TOPIC = "product";

  private final ProductRepository productRepository;
  private final ProductConverter productConverter;

  /**
   * Constructor.
   *
   * @param productRepository the product repository
   * @param productConverter the product converter
   */
  @Autowired
  public ProductListener(ProductRepository productRepository, ProductConverter productConverter) {
    this.productRepository = productRepository;
    this.productConverter = productConverter;
  }

  /**
   * Receives and processes product events.
   *
   * @param data the product as byte string
   */
  @KafkaListener(topics = TOPIC)
  public void receiveProduct(byte[] data) {
    try {
      Product product = Product.parseFrom(data);
      ProductPo productPo = productRepository.findById(product.getId()).orElse(new ProductPo());
      productPo = productConverter.convert(product, productPo);
      productRepository.save(productPo);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
}
