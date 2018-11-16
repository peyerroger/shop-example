package com.rogerpeyer.dockerexample.configuration.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

public class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource)
      throws IOException {
    try {
      YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
      yamlPropertiesFactoryBean.setResources(resource.getResource());
      yamlPropertiesFactoryBean.afterPropertiesSet();
      Properties properties =
          Optional.ofNullable(yamlPropertiesFactoryBean.getObject()).orElse(new Properties());
      String sourceName = name != null ? name : resource.getResource().getFilename();
      return new PropertiesPropertySource(sourceName, properties);
    } catch (Throwable throwable) {
      Throwable cause = throwable.getCause();
      if (cause instanceof FileNotFoundException) {
        throw (FileNotFoundException) throwable.getCause();
      }
      throw throwable;
    }
  }
}
