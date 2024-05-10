package com.czj.dev.redis;

public class AccessKey extends AbstractPrefix {
	public AccessKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}
}
