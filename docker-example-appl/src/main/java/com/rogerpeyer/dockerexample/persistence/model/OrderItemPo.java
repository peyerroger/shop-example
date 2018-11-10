package com.rogerpeyer.dockerexample.persistence.model;

import javax.persistence.Embeddable;

@Embeddable
public class OrderItemPo {

  private Integer quantity;

  private String productId;

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }
}
