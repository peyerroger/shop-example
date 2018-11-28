package com.rogerpeyer.productreadcache.persistence.repository;

import com.rogerpeyer.productreadcache.persistence.model.ProductPo;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductPo, String> {}
