package com.rogerpeyer.orderpricing.persistence.repository;

import com.rogerpeyer.orderpricing.persistence.model.ProductPo;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductPo, String> {}
