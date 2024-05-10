package com.czj.dev.service;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.czj.dev.dao.FlashsaleItemMapper;
import com.czj.dev.dao.FlashsaleOrderMapper;
import com.czj.dev.dao.OrderMapper;
import com.czj.dev.domain.FlashsaleItem;
import com.czj.dev.domain.FlashsaleOrder;
import com.czj.dev.domain.Order;
import com.czj.dev.domain.User;
import com.czj.dev.redis.RedisUtil;
import com.czj.dev.redis.FlashsaleKey;
import com.czj.dev.redis.OrderKey;
import com.czj.dev.util.MD5Util;
import com.czj.dev.util.UUIDUtil;
import com.czj.dev.util.VercodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlashsaleService {
	
	private final RedisUtil redisUtil;
	private final FlashsaleItemMapper flashsaleItemMapper;
	private final OrderMapper orderMapper;
	private final FlashsaleOrderMapper flashsaleOrderMapper;

	public FlashsaleService(RedisUtil redisUtil, FlashsaleItemMapper flashsaleItemMapper, OrderMapper orderMapper,
			FlashsaleOrderMapper flashsaleOrderMapper) {
		this.redisUtil = redisUtil;
		this.flashsaleItemMapper = flashsaleItemMapper;
		this.orderMapper = orderMapper;
		this.flashsaleOrderMapper = flashsaleOrderMapper;
	}

	// 列出所有秒杀商品的方法
	public List<FlashsaleItem> listFlashsaleItem() {
		return flashsaleItemMapper.findAll();
	}

	// 根据商品ID获取秒杀商品的方法
	public FlashsaleItem getFlashsaleItemById(long itemId) {
		return flashsaleItemMapper.findById(itemId);
	}

	// 执行秒杀的方法
	@Transactional
	public Order flashsale(User user, FlashsaleItem item) {
		// 将秒杀商品的库存减1
		boolean success = reduceStock(item);
		if (success) {
			// 创建普通订单和秒杀订单
			return createOrder(user, item);
		} else {
			// 如果秒杀失败，将该商品的秒杀状态设为已结束
			redisUtil.set(FlashsaleKey.isItemOver, "" + item.getId(), true);
			return null;
		}
	}

	// 将秒杀商品的库存减1
	public boolean reduceStock(FlashsaleItem flashsaleItem) {
		int ret = flashsaleItemMapper.reduceStock(flashsaleItem);
		return ret > 0;
	}

	// 根据用户id和物品id返回秒杀订单id，
	// 如果没有秒杀成功，当秒杀结束时返回-1，秒杀未结束时返回0
	public long getFlashsaleResult(Long userId, long itemId) {
		// 根据用户ID和商品ID获取秒杀订单
		FlashsaleOrder order = getFlashsaleOrderByUserIdAndItemId(userId, itemId);
		// 如果秒杀订单不为null，返回订单ID
		if (order != null) {
			return order.getOrderId();
		} else {
			// 根据物品ID获取该商品的秒杀状态
			boolean isOver = redisUtil.exists(FlashsaleKey.isItemOver, "" + itemId);
			// 如果秒杀已经结束返回-1
			if (isOver) {
				return -1;
			}
			// 否则返回0
			else {
				return 0;
			}
		}
	}

	// 判断用户输入的秒杀地址是否正确
	public boolean checkPath(User user, long itemId, String path) {
		if (user == null || path == null) {
			return false;
		}
		// 获取Redis缓存的UUID字符串
		String pathOld = redisUtil.get(FlashsaleKey.flashsalePath, "" + user.getId() + "_" + itemId, String.class);
		// 拿用户输入的UUID字符串与Redis缓存的UUID字符串进行比较
		return path.equals(pathOld);
	}

	// 生成秒杀地址的方法
	public String createFlashsalePath(User user, long itemId) {
		if (user == null || itemId <= 0) {
			return null;
		}
		// 先生成UUID字符串，对UUID字符串进行MD5加密
		String str = MD5Util.md5(UUIDUtil.uuid());
		// 将动态生成的秒杀地址存入Redis中
		redisUtil.set(FlashsaleKey.flashsalePath, "" + user.getId() + "_" + itemId, str);
		return str;
	}

	// 生成秒杀图形验证码
	public BufferedImage createVerifyCode(User user, long itemId) {
		if (user == null || itemId <= 0) {
			return null;
		}
		Random rdm = new Random();
		String verifyCode = VercodeUtil.generateVerifyCode(rdm);
		int rnd = VercodeUtil.calc(verifyCode);
		// 将验证码的值存到Redis中
		redisUtil.set(FlashsaleKey.flashsaleVerifyCode, user.getId() + "," + itemId, rnd);
		// 返回生成的图片
		return VercodeUtil.createVerifyImage(verifyCode, rdm);
	}

	// 检查用户输入的秒杀验证码是否正确
	public boolean checkVerifyCode(User user, long itemId, int verifyCode) {
		if (user == null || itemId <= 0) {
			return false;
		}
		// 获取Redis中保存的验证码
		Integer codeOld = redisUtil.get(FlashsaleKey.flashsaleVerifyCode, user.getId() + "," + itemId, Integer.class);
		// 拿用户输入的验证码与Redis中保存的验证码进行比较
		if (codeOld == null || codeOld - verifyCode != 0) {
			return false;
		}
		// 删除Redis中保存的验证码
		redisUtil.delete(FlashsaleKey.flashsaleVerifyCode, user.getId() + "," + itemId);
		return true;
	}

	// 根据用户ID和商品ID获取秒杀订单
	public FlashsaleOrder getFlashsaleOrderByUserIdAndItemId(long userId, long itemId) {
		// 从Redis缓存读取订单
		return redisUtil.get(OrderKey.flashsaleOrderByUserIdAndItemId, "" + userId + "_" + itemId,
				FlashsaleOrder.class);
	}

	// 创建普通订单和秒杀订单
	@Transactional
	public Order createOrder(User user, FlashsaleItem item) {
		// 创建普通订单
		var order = new Order();
		// 设置订单信息
		order.setUserId(user.getId());
		order.setCreateDate(new Date());
		order.setOrderNum(1);
		order.setItemId(item.getItemId());
		order.setItemName(item.getItemName());
		order.setOrderPrice(item.getFlashsalePrice());
		order.setOrderChannel(1);
		// 设置订单状态，0代表未支付订单
		order.setStatus(0);
		// 保存普通订单
		orderMapper.save(order);
		
		// 创建秒杀订单
		var flashsaleOrder = new FlashsaleOrder();
		// 设置秒杀订单信息
		flashsaleOrder.setUserId(user.getId());
		flashsaleOrder.setItemId(item.getItemId());
		flashsaleOrder.setOrderId(order.getId());
		// 保存秒杀订单
		flashsaleOrderMapper.save(flashsaleOrder);
		// 将秒杀订单保存到Redis缓存中
		redisUtil.set(OrderKey.flashsaleOrderByUserIdAndItemId, "" + user.getId() + "_" + item.getItemId(),
				flashsaleOrder);
		return order;
	}

	// 根据订单ID和用户ID获取订单的方法
	public Order getOrderByIdAndOwnerId(long orderId, long userId) {
		return orderMapper.findByIdAndOwnerId(orderId, userId);
	}
}
