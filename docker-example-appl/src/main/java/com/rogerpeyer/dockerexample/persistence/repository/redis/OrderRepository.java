package com.rogerpeyer.dockerexample.persistence.repository.redis;

import com.rogerpeyer.dockerexample.persistence.model.OrderPo;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderPo, String> {

}
