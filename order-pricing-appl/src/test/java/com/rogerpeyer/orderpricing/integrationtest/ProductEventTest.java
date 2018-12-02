package com.rogerpeyer.orderpricing.integrationtest;

import com.google.protobuf.Timestamp;
import com.rogerpeyer.orderpricing.eventsubscribers.product.ProductEventSubscriber;
import com.rogerpeyer.orderpricing.persistence.model.ProductPo;
import com.rogerpeyer.orderpricing.persistence.repository.ProductRepository;
import com.rogerpeyer.product.event.spi.ProductOuterClass.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

public class ProductEventTest extends AbstractTest {

  @Autowired private ProductRepository productRepository;

  @Before
  public void setUp() {
    productRepository.deleteAll();
  }

  @Test
  public void test() throws Exception {
    Product product = newTestInstance();
    KafkaTemplate<String, byte[]> template = getKafkaTemplate();
    template.sendDefault(product.getId(), product.toByteArray());

    Thread.sleep(1000L);

    ProductPo productPo = productRepository.findById(product.getId()).orElse(null);
    Assert.assertNotNull(productPo);
  }

  @Test
  public void test2() throws Exception {
    int numberOfProducts = 100;
    KafkaTemplate<String, byte[]> template = getKafkaTemplate();
    for (int i = 0; i < numberOfProducts; i++) {
      Product product = newTestInstance();
      template.sendDefault(product.getId(), product.toByteArray());
    }

    Thread.sleep(1000L);

    Assert.assertEquals(numberOfProducts, productRepository.count());
  }

  private KafkaTemplate<String, byte[]> getKafkaTemplate() {
    Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
    senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    senderProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
    ProducerFactory<String, byte[]> pf = new DefaultKafkaProducerFactory<>(senderProps);
    KafkaTemplate<String, byte[]> template = new KafkaTemplate<>(pf);
    template.setDefaultTopic(ProductEventSubscriber.TOPIC);
    return template;
  }

  private Product newTestInstance() {
    return Product.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName(UUID.randomUUID().toString())
        .setPrice(new BigDecimal(new Random().nextDouble()).toString())
        .setReleaseDate(
            Timestamp.newBuilder().setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
        .setCreatedOn(
            Timestamp.newBuilder().setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
        .setLastModified(
            Timestamp.newBuilder().setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
        .build();
  }
}
