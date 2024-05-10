package com.czj.dev.access;

import com.czj.dev.domain.User;

public class UserContext {
	
	private static final ThreadLocal<User> userHolder = new ThreadLocal<>();

	public static void setUser(User user) {
		userHolder.set(user);
	}

	public static User getUser() {
		return userHolder.get();
	}
}
