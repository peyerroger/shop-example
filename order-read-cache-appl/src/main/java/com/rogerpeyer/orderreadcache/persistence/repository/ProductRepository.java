package com.rogerpeyer.orderreadcache.persistence.repository;

import com.rogerpeyer.orderreadcache.persistence.model.ProductPo;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductPo, String> {}
