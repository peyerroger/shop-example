package com.rogerpeyer.ordermanagement.configuration;

import com.rogerpeyer.orderpricing.client.ApiClient;
import com.rogerpeyer.orderpricing.client.api.OrderPricingApi;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.build();
  }

  @Bean
  public OrderPricingApi orderPricingApi(ApiClient apiClient) {
    return new OrderPricingApi(apiClient);
  }

  @Bean
  public ApiClient apiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath("http://localhost");

    return apiClient;
  }
}
