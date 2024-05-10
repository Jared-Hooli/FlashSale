package com.czj.dev.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import com.czj.dev.util.CookieUtil;
import com.czj.dev.domain.User;
import com.czj.dev.exception.FlashSaleException;
import com.czj.dev.redis.RedisUtil;
import com.czj.dev.redis.UserKey;
import com.czj.dev.result.CodeMsg;
import com.czj.dev.result.Result;
import com.czj.dev.service.UserService;
import com.czj.dev.util.MD5Util;
import com.czj.dev.vo.LoginVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;
	private final RedisUtil redisUtil;

	public UserController(UserService userService, RedisUtil redisUtil) {
		this.userService = userService;
		this.redisUtil = redisUtil;
	}

	// 该方法使用Redis缓存实现分布式Session
	// 该方法将Session信息保存在Redis缓存中，SessionID以Cookie写入浏览器
	private void addSession(HttpServletResponse response, String token, User user) {
		// 以Redis缓存保存分布式Session信息
		redisUtil.set(UserKey.token, token, user);
		// 使用Cookie存放分布式Session的ID
		CookieUtil.addSessionId(response, token);
	}

	// 该方法用于根据分布式SessionID读取对应的User
	public User getByToken(HttpServletResponse response, String token) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		// 根据分布式SessionID读取对应的User
		User user = redisUtil.get(UserKey.token, token, User.class);
		// 延长有效期，保证有效期总是最后一次访问时间再加上Session过期时间
		if (user != null) {
			// 重新往缓存中设置token，并生成新的cookie，这样就达到了延长有效期的目的
			addSession(response, token, user);
		}
		return user;
	}

	@GetMapping("/login")
	public String toLogin() {
		return "login";
	}

	@GetMapping(value = "/verifyCode")
	@ResponseBody
	public void getLoginVerifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 从Cookie中读取分布式Session ID
		String token = CookieUtil.getSessionId(request, response);
		// 创建验证码图片
		BufferedImage image = userService.createVerifyCode(token);
		OutputStream out = response.getOutputStream();
		// 输出验证码
		ImageIO.write(image, "JPEG", out);
		out.flush();
		out.close();
	}

	@PostMapping("/proLogin")
	@ResponseBody
	public Result<Boolean> proLogin(HttpServletRequest request, HttpServletResponse response, LoginVo loginVo) {
		// 通过Cookie获取分布式SessionID
		String token = CookieUtil.getCookieValue(request, UserKey.COOKIE_NAME_TOKEN);
		// 如果代表分布式SessionID的Cookie存在
		if (token != null) {
			// 如果输入的验证码不匹配
			if (!userService.checkVerifyCode(token, loginVo.getVercode())) {
				System.out.println("==========> 输入的验证码不匹配");
				return Result.error(CodeMsg.REQUEST_ILLEGAL);
			}
			// 从分布式Session中读取用户信息
			User user = getByToken(response, token);
			// 判断从Session中读取的信息与登录信息是否匹配
			if (user != null && user.getId().toString().equals(loginVo.getMobile())
					&& MD5Util.passToDbPass(loginVo.getPassword(), user.getSalt()).equals(user.getPassword())) {
				return Result.success(true); // ①
			}
		}
		try {
			// 处理登录，返回符合条件的用户
			User user = userService.login(loginVo); // ②
			// 使用分布式Session保存登录用户的信息
			addSession(response, token, user);
			return Result.success(true);
		} catch (FlashSaleException e) {
			return Result.error(e.getCodeMsg());
		}
	}

	@GetMapping("/info")
	@ResponseBody
	public Result<User> info(User user) {
		return Result.success(user);
	}
}
