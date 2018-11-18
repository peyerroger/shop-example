package com.rogerpeyer.orderservice.configuration.persistence;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.rogerpeyer.orderservice.persistence.repository.jpa"})
public class JpaConfiguration {}
