package com.rogerpeyer.ordermanagement.persistence.repository.jpa;

import com.rogerpeyer.ordermanagement.persistence.model.OrderPo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderPo, Long> {}
