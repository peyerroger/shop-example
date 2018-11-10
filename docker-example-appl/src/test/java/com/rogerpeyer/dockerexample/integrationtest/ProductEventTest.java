package com.rogerpeyer.dockerexample.integrationtest;

import com.google.protobuf.Timestamp;
import com.rogerpeyer.dockerexample.controller.product.ProductEventController;
import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import com.rogerpeyer.dockerexample.persistence.repository.redis.ProductRepository;
import com.rogerpeyer.spi.proto.ProductOuterClass.Product;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

public class ProductEventTest extends AbstractTest {

  @Autowired private ProductRepository productRepository;

  @Test
  public void test() throws Exception {
    Product product =
        Product.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setName(UUID.randomUUID().toString())
            .setPrice("4.32")
            .setReleaseDate(
                Timestamp.newBuilder()
                    .setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
            .setCreatedOn(
                Timestamp.newBuilder()
                    .setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
            .setLastModified(
                Timestamp.newBuilder()
                    .setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
            .build();
    Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
    senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    senderProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
    ProducerFactory<String, byte[]> pf = new DefaultKafkaProducerFactory<>(senderProps);
    KafkaTemplate<String, byte[]> template = new KafkaTemplate<>(pf);
    template.setDefaultTopic(ProductEventController.TOPIC);
    template.sendDefault(product.getId(), product.toByteArray());

    Thread.sleep(1000L);

    ProductPo productPo = productRepository.findById(product.getId()).orElse(null);
    Assert.assertNotNull(productPo);
  }
}
