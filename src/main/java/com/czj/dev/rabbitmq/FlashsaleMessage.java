package com.czj.dev.rabbitmq;

import com.czj.dev.domain.User;

public class FlashsaleMessage {
	
	private User user;
	private long itemId;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
}
