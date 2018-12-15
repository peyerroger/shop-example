package com.rogerpeyer.orderpricing.configuration.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ServletContextRequestLoggingFilter;

@Configuration
public class LoggingConfig {
  /**
   * Intercept servlet context requests.
   *
   * @return ServletContextRequestLoggingFilter
   */
  @Bean
  public ServletContextRequestLoggingFilter logFilter() {
    ServletContextRequestLoggingFilter filter = new ServletContextRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(10000);
    filter.setIncludeHeaders(true);
    filter.setAfterMessagePrefix("Incoming API request: ");
    return filter;
  }
}
