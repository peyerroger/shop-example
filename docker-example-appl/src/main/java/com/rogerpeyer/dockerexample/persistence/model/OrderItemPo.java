package com.rogerpeyer.dockerexample.persistence.model;

public class OrderItemPo {

  private Integer quantity;

  private Long productId;

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }
}
