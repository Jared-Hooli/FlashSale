package com.czj.dev.vo;

import com.czj.dev.domain.FlashsaleItem;
import com.czj.dev.domain.Order;
import com.czj.dev.domain.User;

public class OrderDetailVo {
	
	private FlashsaleItem flashsaleItem;
	private Order order;
	private User user;

	public FlashsaleItem getFlashsaleItem() {
		return flashsaleItem;
	}

	public void setFlashsaleItem(FlashsaleItem flashsaleItem) {
		this.flashsaleItem = flashsaleItem;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "OrderDetailVo{" +
				"flashsaleItem=" + flashsaleItem +
				", order=" + order +
				", user=" + user +
				'}';
	}
}