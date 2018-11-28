package com.rogerpeyer.ordermanagement.persistence.repository.redis;

import com.rogerpeyer.ordermanagement.persistence.model.ProductPo;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductPo, String> {}
