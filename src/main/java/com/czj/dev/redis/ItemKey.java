package com.czj.dev.redis;

public class ItemKey extends AbstractPrefix {
	
	public ItemKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	// 缓存秒杀商品列表页面的key前缀
	public static ItemKey itemList = new ItemKey(120, "list");
	// 缓存秒杀商品库存的key前缀
	public static ItemKey flashsaleItemStock = new ItemKey(0, "stock");
}
