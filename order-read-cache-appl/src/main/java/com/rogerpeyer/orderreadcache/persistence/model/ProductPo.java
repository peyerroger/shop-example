package com.rogerpeyer.orderreadcache.persistence.model;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("product")
@Data
public class ProductPo {

  @Id private String id;

  @NotNull private BigDecimal price;
}
