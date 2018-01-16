package com.kochuparet.sample.shoppingcart.orderprocessor.dto;

public class OrderItemDTO {
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	private String itemId;
	private Integer quantity;
	private Double rate;
	@Override
	public String toString() {
		return "OrderItemDTO [itemId=" + itemId + ", quantity=" + quantity + ", rate=" + rate + "]";
	}
}
