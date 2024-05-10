package com.czj.dev.redis;

public class FlashsaleKey extends AbstractPrefix {
	
	public FlashsaleKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	public static FlashsaleKey flashsaleVerifyCode = new FlashsaleKey(300, "vc");
	// 动态的秒杀路径每60秒就会过期
	public static FlashsaleKey flashsalePath = new FlashsaleKey(60, "mp");
	// 0代表永不过期
	public static FlashsaleKey isItemOver = new FlashsaleKey(0, "over");
}
