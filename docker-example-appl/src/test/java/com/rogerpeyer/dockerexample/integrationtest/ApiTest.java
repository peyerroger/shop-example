package com.rogerpeyer.dockerexample.integrationtest;

import com.rogerpeyer.dockerexample.integrationtest.redis.EmbeddedRedis;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApiTest {

  @Autowired
  protected TestRestTemplate testRestTemplate;

  @Autowired
  private EmbeddedRedis embeddedRedis;

}
