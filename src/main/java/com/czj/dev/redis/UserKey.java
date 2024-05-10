package com.czj.dev.redis;

public class UserKey extends AbstractPrefix {
	public static final String COOKIE_NAME_TOKEN = "token";
	public static final int TOKEN_EXPIRE = 1800;

	public UserKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	// 定义用于保存分布式Session ID的key
	public static UserKey token = new UserKey(TOKEN_EXPIRE, COOKIE_NAME_TOKEN);
	
	// 0代表永不过期
	public static UserKey getById = new UserKey(0, "id");
	
	// 用于保存验证码的key
	public static UserKey verifyCode = new UserKey(300, "vc");
}
