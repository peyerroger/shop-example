package com.rogerpeyer.orderservice.persistence.repository.redis;

import com.rogerpeyer.orderservice.persistence.model.ProductPo;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductPo, String> {}
