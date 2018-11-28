package com.rogerpeyer.orderreadcache.configuration.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(
    basePackages = {"com.rogerpeyer.orderreadcache.persistence.repository.redis"})
public class RedisConfiguration {}
