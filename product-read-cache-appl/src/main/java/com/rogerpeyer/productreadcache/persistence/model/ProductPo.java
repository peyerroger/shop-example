package com.rogerpeyer.productreadcache.persistence.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("product")
@Data
public class ProductPo {

  @Id private String id;

  @NotNull private String name;

  @NotNull private BigDecimal price;

  @NotNull private LocalDate releaseDate;

  private OffsetDateTime lastModified;

  private OffsetDateTime createdOn;
}
