package com.rogerpeyer.productservice.configuration.properties;

import com.rogerpeyer.productservice.configuration.properties.factory.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(
    value = {"classpath:application.yml", "classpath:secret.yml"},
    factory = YamlPropertySourceFactory.class)
public class OverrideConfig {

  @Configuration
  @Import(DefaultConfig.class)
  static class InnerConfiguration {}
}
