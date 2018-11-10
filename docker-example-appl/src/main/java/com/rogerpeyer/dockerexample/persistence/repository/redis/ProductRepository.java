package com.rogerpeyer.dockerexample.persistence.repository.redis;

import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductPo, String> {}
