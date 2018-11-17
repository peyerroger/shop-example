package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.eventpublisher.order.OrderEventPublisher;
import com.rogerpeyer.dockerexample.eventsubscribers.product.ProductEventSubscriber;
import com.rogerpeyer.dockerexample.integrationtest.redis.EmbeddedRedis;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
    partitions = 1,
    topics = {ProductEventSubscriber.TOPIC, OrderEventPublisher.TOPIC},
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "auto.create.topics.enable=true"})
public abstract class AbstractTest {

  private static boolean isKafkaInitialized = false;
  @Autowired TestRestTemplate testRestTemplate;
  @Autowired EmbeddedKafkaBroker embeddedKafkaBroker;
  @Autowired private EmbeddedRedis embeddedRedis;
  @Autowired private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  /**
   * Sets up the Test.
   *
   * @throws Exception the exception
   */
  @Before
  public final void setUpBase() throws Exception {

    if (!isKafkaInitialized) {

      kafkaListenerEndpointRegistry
          .getListenerContainers()
          .forEach(
              listenerContainer -> {
                try {
                  ContainerTestUtils.waitForAssignment(
                      listenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
                } catch (Exception e) {
                  e.printStackTrace();
                }
              });

      isKafkaInitialized = true;
    }
  }
}
