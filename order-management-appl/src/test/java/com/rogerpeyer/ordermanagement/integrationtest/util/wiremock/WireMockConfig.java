package com.rogerpeyer.ordermanagement.integrationtest.util.wiremock;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.stereotype.Component;

@Component
public class WireMockConfig implements WireMockConfigurationCustomizer {

  @Override
  public void customize(WireMockConfiguration config) {
    config.extensions(OrderPricingTransformer.class);
  }
}
