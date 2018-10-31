package com.rogerpeyer.dockerexample.persistence.repository;

import com.rogerpeyer.dockerexample.persistence.model.ProductPo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductPo, Long> {

}

