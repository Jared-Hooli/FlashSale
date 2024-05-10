package com.czj.dev.access;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import com.czj.dev.util.CookieUtil;
import com.czj.dev.controller.UserController;
import com.czj.dev.domain.User;
import com.czj.dev.redis.AccessKey;
import com.czj.dev.redis.RedisUtil;
import com.czj.dev.redis.UserKey;
import com.czj.dev.result.CodeMsg;
import com.czj.dev.result.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AccessInterceptor implements HandlerInterceptor {
	private final UserController userController;
	private final RedisUtil redisUtil;

	public AccessInterceptor(UserController userController, RedisUtil redisUtil) {
		this.userController = userController;
		this.redisUtil = redisUtil;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 获取或创建分布式Session的ID
		CookieUtil.getSessionId(request, response);
		User user = getUser(request, response);
		// 将读取到的User信息存入UserContext的ThreadLocal容器中
		UserContext.setUser(user);
		
		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;
			// 获取被调用方法上的@AccessLimit注解
			AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
			// 如果没有@AccessLimit注解，直接返回true（放行）
			if (accessLimit == null) {
				return true;
			}
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			boolean needLogin = accessLimit.needLogin();
			String key = request.getRequestURI();
			// 如果needLogin为true，表明需要登录才能调用该方法
			if (needLogin) {
				// 如果user为null，表明还为登录，直接拒绝调用
				if (user == null) {
					render(response, CodeMsg.SESSION_ERROR);
					return false;
				}
			}
			// 如果设置了seconds和maxCount两个属性，
			// 表明要限制在指定时间内指定方法只能被调用几次
			if (seconds > 0 && maxCount > 0) {
				key += "_" + user.getId();
				AccessKey ak = AccessKey.withExpire(seconds);
				// 以ak为前缀、加上用户手机号作为真正的key来获取访问次数
				Integer count = redisUtil.get(ak, key, Integer.class);
				// 如果count为null，表明之前不曾访问过
				if (count == null) {
					redisUtil.set(ak, key, 1);
				}
				// 如果访问次数还未达到最大次数，则可继续访问
				else if (count < maxCount) {
					// 访问次数加1
					redisUtil.incr(ak, key);
				}
				// 如果访问次数达到限制
				else {
					// 生成错误提示
					render(response, CodeMsg.ACCESS_LIMIT_REACHED);
					return false;
				}
			}
		}
		return true;
	}

	// 该方法用于根据CodeMsg生成错误响应
	private void render(HttpServletResponse response, CodeMsg cm) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		// 将CodeMsg包装成Result对象，再将它转换成字符串
		String str = redisUtil.beanToString(Result.error(cm));
		// 输出响应字符串
		out.write(str.getBytes(StandardCharsets.UTF_8));
		out.flush();
		out.close();
	}

	private User getUser(HttpServletRequest request, HttpServletResponse response) {
		// 获取名为token的请求参数
		String paramToken = request.getParameter(UserKey.COOKIE_NAME_TOKEN);
		// 获取名为token的Cookie的值
		String cookieToken = CookieUtil.getCookieValue(request, UserKey.COOKIE_NAME_TOKEN);
		if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		// 优先使用paramToken作为分布式Session的ID
		String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
		// 根据分布式Session ID获取Session对象
		return userController.getByToken(response, token);
	}
}
