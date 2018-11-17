package com.rogerpeyer.dockerexample.persistence.repository.jpa;

import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderPo, Long> {}
