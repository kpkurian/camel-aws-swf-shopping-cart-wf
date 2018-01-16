package com.kochuparet.sample.shoppingcart.orderprocessor.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {
	private String orderNumber;
	private String state;
	private String status;
	private String errorCode;
	private String errorMessage;
	private String orderDate;
	private String custId;
	private String emailId;
	private boolean orderVerified;
	private List<OrderItemDTO> orderItems;
	private Double orderTotal;
	
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getCustId() {
		return custId;
	}
	@Override
	public String toString() {
		return "OrderDTO [orderNumber=" + orderNumber + ", state=" + state + ", status=" + status + ", errorCode="
				+ errorCode + ", errorMessage=" + errorMessage + ", orderDate=" + orderDate + ", custId=" + custId
				+ ", emailId=" + emailId + ", orderVerified=" + orderVerified + ", orderItems=" + orderItems
				+ ", orderTotal=" + orderTotal + "]";
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public List<OrderItemDTO> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItemDTO> orderItems) {
		this.orderItems = orderItems;
	}
	public Double getOrderTotal() {
		return orderTotal;
	}
	public void setOrderTotal(Double orderTotal) {
		this.orderTotal = orderTotal;
	}
	public boolean isOrderVerified() {
		return orderVerified;
	}
	public void setOrderVerified(boolean orderVerified) {
		this.orderVerified = orderVerified;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	

}
