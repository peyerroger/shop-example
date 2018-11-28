package com.rogerpeyer.ordermanagement.persistence.model;

import java.time.OffsetDateTime;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import lombok.Data;

@Entity
@Data
public class OrderPo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version private Long version;

  private OffsetDateTime lastModified;
  private OffsetDateTime createdOn;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "ORDER_ITEM", joinColumns = @JoinColumn(name = "ORDER_ID"))
  private List<OrderItemPo> items;

  /** Pre persist. */
  @PrePersist
  public void prePersist() {
    createdOn = OffsetDateTime.now();
    lastModified = OffsetDateTime.now();
  }

  /** Pre update. */
  @PreUpdate
  public void preUpdate() {
    lastModified = OffsetDateTime.now();
  }
}
