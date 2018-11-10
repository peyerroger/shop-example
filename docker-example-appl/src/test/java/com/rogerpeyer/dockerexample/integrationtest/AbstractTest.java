package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.controller.product.ProductEventController;
import com.rogerpeyer.dockerexample.integrationtest.redis.EmbeddedRedis;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
    partitions = 1,
    topics = {ProductEventController.TOPIC},
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "auto.create.topics.enable=true"})
public abstract class AbstractTest {

  @Autowired TestRestTemplate testRestTemplate;

  @Autowired private EmbeddedRedis embeddedRedis;

  @Autowired private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @Autowired EmbeddedKafkaBroker embeddedKafkaBroker;

  private static boolean isKafkaInitialized = false;

  /**
   * Sets up the Test.
   *
   * @throws Exception the exception
   */
  @Before
  public final void setUpBase() throws Exception {

    if (!isKafkaInitialized) {

      for (MessageListenerContainer messageListenerContainer :
          kafkaListenerEndpointRegistry.getListenerContainers()) {
        ContainerTestUtils.waitForAssignment(
            messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
      }
      isKafkaInitialized = true;
    }
  }
}
