package com.czj.dev.access;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
	
	boolean needLogin() default true;

	// 该注解限制被修饰的方法在指定时间内最多访问几次
	// -1表示不限制
	int seconds() default -1;

	int maxCount() default -1;
}
