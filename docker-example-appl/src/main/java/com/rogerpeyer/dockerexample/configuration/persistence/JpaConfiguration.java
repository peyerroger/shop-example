package com.rogerpeyer.dockerexample.configuration.persistence;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = { "com.rogerpeyer.dockerexample.persistence.repository.jpa" }
)
public class JpaConfiguration {

}
