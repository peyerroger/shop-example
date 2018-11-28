package com.rogerpeyer.orderreadcache.persistence.repository.redis;

import com.rogerpeyer.orderreadcache.persistence.model.OrderPo;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderPo, String> {}
