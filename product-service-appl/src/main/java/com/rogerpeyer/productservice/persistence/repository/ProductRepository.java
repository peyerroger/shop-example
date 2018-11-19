package com.rogerpeyer.productservice.persistence.repository;

import com.rogerpeyer.productservice.persistence.model.ProductPo;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductPo, String> {}
