package com.rogerpeyer.ordermanagement.configuration.persistence;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.rogerpeyer.ordermanagement.persistence.repository.jpa"})
public class JpaConfiguration {}
