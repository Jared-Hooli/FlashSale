package com.czj.dev.vo;

import com.czj.dev.domain.FlashsaleItem;
import com.czj.dev.domain.User;

public class ItemDetailVo {
	
	private int remainSeconds = 0;
	private int leftSeconds = 0;
	private FlashsaleItem flashsaleItem;
	private User user;

	public int getRemainSeconds() {
		return remainSeconds;
	}

	public void setRemainSeconds(int remainSeconds) {
		this.remainSeconds = remainSeconds;
	}

	public int getLeftSeconds() {
		return leftSeconds;
	}

	public void setLeftSeconds(int leftSeconds) {
		this.leftSeconds = leftSeconds;
	}

	public FlashsaleItem getFlashsaleItem() {
		return flashsaleItem;
	}

	public void setFlashsaleItem(FlashsaleItem flashsaleItem) {
		this.flashsaleItem = flashsaleItem;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "ItemDetailVo{" +
				"remainSeconds=" + remainSeconds +
				", leftSeconds=" + leftSeconds +
				", flashsaleItem=" + flashsaleItem +
				", user=" + user +
				'}';
	}
}
