package com.czj.dev.redis;

public class OrderKey extends AbstractPrefix {
	
	public OrderKey(String prefix) {
		super(prefix);
	}

	// 代表永不过期
	public static OrderKey flashsaleOrderByUserIdAndItemId = new OrderKey("fOrderUserItem");
}
