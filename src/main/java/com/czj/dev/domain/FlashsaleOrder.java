package com.czj.dev.domain;

public class FlashsaleOrder {
	private Long id;
	private Long userId;
	private Long orderId;
	private Long itemId;

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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Override
	public String toString() {
		return "FlashsaleOrder{" +
				"id=" + id +
				", userId=" + userId +
				", orderId=" + orderId +
				", itemId=" + itemId +
				'}';
	}
}
