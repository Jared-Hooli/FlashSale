package com.czj.dev.domain;

import java.util.Date;

public class Order {
	private Long id;
	private Long userId;
	private Long itemId;
	private String itemName;
	private Integer orderNum;
	private Double orderPrice;
	private Integer orderChannel;
	private Integer status;
	private Date createDate;
	private Date payDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public Double getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Double orderPrice) {
		this.orderPrice = orderPrice;
	}

	public Integer getOrderChannel() {
		return orderChannel;
	}

	public void setOrderChannel(Integer orderChannel) {
		this.orderChannel = orderChannel;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	@Override
	public String toString() {
		return "Order{" +
				"id=" + id +
				", userId=" + userId +
				", itemId=" + itemId +
				", itemName='" + itemName + '\'' +
				", orderNum=" + orderNum +
				", orderPrice=" + orderPrice +
				", orderChannel=" + orderChannel +
				", status=" + status +
				", createDate=" + createDate +
				", payDate=" + payDate +
				'}';
	}
}
