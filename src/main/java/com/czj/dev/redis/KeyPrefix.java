package com.czj.dev.redis;

public interface KeyPrefix {
	int expireSeconds();
	String getPrefix();
}
