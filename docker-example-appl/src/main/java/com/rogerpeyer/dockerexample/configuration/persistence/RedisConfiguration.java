package com.rogerpeyer.dockerexample.configuration.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(
    basePackages = { "com.rogerpeyer.dockerexample.persistence.repository.redis" }
)
public class RedisConfiguration {

}
