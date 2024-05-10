package com.czj.dev.service;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Random;

import com.czj.dev.dao.UserMapper;
import com.czj.dev.domain.User;
import com.czj.dev.exception.FlashSaleException;
import com.czj.dev.redis.RedisUtil;
import com.czj.dev.redis.UserKey;
import com.czj.dev.result.CodeMsg;
import com.czj.dev.util.MD5Util;
import com.czj.dev.util.VercodeUtil;
import com.czj.dev.vo.LoginVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	
	private final UserMapper userMapper;
	private final RedisUtil redisUtil;

	public UserService(UserMapper userMapper, RedisUtil redisUtil) {
		this.userMapper = userMapper;
		this.redisUtil = redisUtil;
	}

	// 创建图形验证码
	public BufferedImage createVerifyCode(String token) {
		if (token == null) {
			return null;
		}
		Random rdm = new Random();
		// 调用VercodeUtil的generateVerifyCode生成图形验证码
		String verifyCode = VercodeUtil.generateVerifyCode(rdm);
		// 计算图形验证码的值
		int rnd = VercodeUtil.calc(verifyCode);
		// 将验证码的值存到Redis中
		redisUtil.set(UserKey.verifyCode, token, rnd);
		// 返回生成的图片
		return VercodeUtil.createVerifyImage(verifyCode, rdm);
	}

	// 检查图形验证码是否正确
	public boolean checkVerifyCode(String token, int verifyCode) {
		if (token == null) {
			return false;
		}
		// 从Redis中读取服务端保存的验证码
		Integer codeOld = redisUtil.get(UserKey.verifyCode, token, Integer.class);
		// 如果codeOld为空或codeOld与verifyCode不同，则返回false
		if (codeOld == null || codeOld - verifyCode != 0) {
			return false;
		}
		// 清除服务端保存的图形验证码
		redisUtil.delete(UserKey.verifyCode, token);
		return true;
	}

	// 处理用户登录的方法
	@Transactional
	public User login(LoginVo loginVo) {
		if (loginVo == null) {
			throw new FlashSaleException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		// 根据手机号获取对应的用户
		User user = getById(Long.parseLong(mobile)); // ①
		// 如果user为null，说明该用户不存在
		if (user == null) {
			throw new FlashSaleException(CodeMsg.MOBILE_NOT_EXIST);
		}
		// 获取数据库中保存的密码
		String dbPass = user.getPassword();
		// 计算加盐加密后的密码
		String calcPass = MD5Util.passToDbPass(loginVo.getPassword(), user.getSalt());
		// 如果加盐加密后的密码与数据库中保存的密码不相等，登录失败
		if (!calcPass.equals(dbPass)) {
			throw new FlashSaleException(CodeMsg.PASSWORD_ERROR);
		}
		// 增加登录次数
		user.setLoginCount(user.getLoginCount() + 1);
		// 更新最后的登录时间
		user.setLastLoginDate(new Date());
		// 更新用户信息
		userMapper.update(user);
		return user;
	}

	private User getById(long id) {
		// 先从Redis缓存中根据ID读取用户
		User user = redisUtil.get(UserKey.getById, "" + id, User.class);
		if (user != null) {
			return user;
		}
		// 如果Redis缓存中没有读到用户，从数据库中根据ID读取用户
		user = userMapper.findById(id);
		if (user != null) {
			// 将读取的用户存入Redis缓存
			redisUtil.set(UserKey.getById, "" + id, user);
		}
		return user;
	}
}
