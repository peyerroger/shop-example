package com.rogerpeyer.dockerexample.configuration.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:default.yml", factory = YamlPropertySourceFactory.class)
public class DefaultConfig {}
