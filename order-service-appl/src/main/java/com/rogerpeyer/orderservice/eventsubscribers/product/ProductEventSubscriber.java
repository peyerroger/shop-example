package com.rogerpeyer.orderservice.eventsubscribers.product;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rogerpeyer.orderservice.eventsubscribers.product.converter.ProductEventConverter;
import com.rogerpeyer.orderservice.persistence.model.ProductPo;
import com.rogerpeyer.orderservice.persistence.repository.redis.ProductRepository;
import com.rogerpeyer.spi.proto.ProductOuterClass.Product;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ProductEventSubscriber {

  public static final String TOPIC = "product";

  private final ProductRepository productRepository;
  private final ProductEventConverter productEventConverter;

  /**
   * Constructor.
   *
   * @param productRepository the product repository
   * @param productEventConverter the product converter
   */
  @Autowired
  public ProductEventSubscriber(
      ProductRepository productRepository, ProductEventConverter productEventConverter) {
    this.productRepository = productRepository;
    this.productEventConverter = productEventConverter;
  }

  /**
   * Receives and processes product events.
   *
   * @param data the product as byte string
   */
  @KafkaListener(topics = TOPIC)
  public void listen(
      @Payload byte[] data,
      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.OFFSET) long offset) {
    try {
      log.debug(
          "Received Product Event. [ MessageKey={}, Partition={}, Topic={}, Offset={} ]",
          key,
          partition,
          topic,
          offset);
      Product product = Product.parseFrom(data);
      ProductPo productPo = productRepository.findById(product.getId()).orElse(new ProductPo());
      productPo = productEventConverter.convert(product, productPo);
      productRepository.save(productPo);
    } catch (InvalidProtocolBufferException e) {
      log.error("Could not process product event.", e);
    }
  }
}
