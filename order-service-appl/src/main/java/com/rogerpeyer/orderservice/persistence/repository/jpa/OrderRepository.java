package com.rogerpeyer.orderservice.persistence.repository.jpa;

import com.rogerpeyer.orderservice.persistence.model.OrderPo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderPo, Long> {}
