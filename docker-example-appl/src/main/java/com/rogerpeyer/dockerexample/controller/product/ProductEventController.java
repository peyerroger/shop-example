package com.rogerpeyer.dockerexample.controller.product;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import com.rogerpeyer.dockerexample.service.product.ProductEventService;
import com.rogerpeyer.spi.proto.ProductOuterClass.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventController {

  public static final String TOPIC = "product";

  private final ProductRepository productRepository;
  private final ProductEventService productEventService;

  @Autowired
  public ProductEventController(
      ProductRepository productRepository, ProductEventService productEventService) {
    this.productRepository = productRepository;
    this.productEventService = productEventService;
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
      productPo = productEventService.calculatePersistentObject(product, productPo);
      productRepository.save(productPo);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
}
