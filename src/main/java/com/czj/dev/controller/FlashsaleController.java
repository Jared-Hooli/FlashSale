package com.czj.dev.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import com.czj.dev.access.AccessLimit;
import com.czj.dev.domain.FlashsaleItem;
import com.czj.dev.domain.FlashsaleOrder;
import com.czj.dev.domain.User;
import com.czj.dev.rabbitmq.FlashsaleMessage;
import com.czj.dev.rabbitmq.FlashsaleSender;
import com.czj.dev.redis.RedisUtil;
import com.czj.dev.redis.ItemKey;
import com.czj.dev.result.CodeMsg;
import com.czj.dev.result.Result;
import com.czj.dev.service.FlashsaleService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping("/flashsale")
public class FlashsaleController implements InitializingBean {
	
	private final FlashsaleService flashsaleService;
	private final RedisUtil redisUtil;
	private final FlashsaleSender mqSender;
	// 存放ItemId与秒杀是否结束的对应关系
	private final Map<Long, Boolean> localOverMap = Collections.synchronizedMap(new HashMap<>());

	public FlashsaleController(FlashsaleService flashsaleService, RedisUtil redisUtil, FlashsaleSender mqSender) {
		this.flashsaleService = flashsaleService;
		this.redisUtil = redisUtil;
		this.mqSender = mqSender;
	}

	@Override
	public void afterPropertiesSet() {
		// 获取所有物品列表
		List<FlashsaleItem> itemList = flashsaleService.listFlashsaleItem();
		if (itemList == null) {
			return;
		}
		for (FlashsaleItem item : itemList) {
			// 将所有物品及其对应库存放入Redis缓存
			redisUtil.set(ItemKey.flashsaleItemStock, "" + item.getItemId(), item.getStockCount());
			localOverMap.put(item.getId(), false);
		}
	}

	@GetMapping(value = "/verifyCode")
	@ResponseBody
	@AccessLimit // 限制该方法必须登录才能访问
	public void getFlashsaleVerifyCode(HttpServletResponse response, User user, @RequestParam("itemId") long itemId)
			throws IOException {
		// 生成验证码
		BufferedImage image = flashsaleService.createVerifyCode(user, itemId);
		OutputStream out = response.getOutputStream();
		// 将验证码输出到客户端
		ImageIO.write(image, "JPEG", out);
		out.flush();
		out.close();
	}

	@GetMapping(value = "/path")
	@ResponseBody
	// 限制该方法必须登录才能访问，且每5秒内只能调用5次
	@AccessLimit(seconds = 5, maxCount = 5)
	public Result<String> getFlashsalePath(User user, @RequestParam("itemId") long itemId,
			@RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {
		// 如果输入的验证码不匹配
		if (!flashsaleService.checkVerifyCode(user, itemId, verifyCode)) // ①
		{
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		String path = flashsaleService.createFlashsalePath(user, itemId);
		return Result.success(path);
	}

	@PostMapping("/{path}/proFlashsale")
	@ResponseBody
	@AccessLimit // 限制该方法必须登录才能访问
	public Result<Integer> proFlashsale(Model model, User user, @RequestParam("itemId") long itemId,
			@PathVariable("path") String path) throws JsonProcessingException {
		model.addAttribute("user", user);
		// 验证动态的秒杀地址是否正确
		boolean check = flashsaleService.checkPath(user, itemId, path); // ②
		if (!check) {
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		
		// 通过内存快速获取该商品是否秒杀结束
		Boolean over = localOverMap.get(itemId);
		// 如果秒杀已经结束
		if (over != null && over) // ③
		{
			return Result.error(CodeMsg.FLASHSALE_OVER);
		}
		
		// 预减库存
		long stock = redisUtil.decr(ItemKey.flashsaleItemStock, "" + itemId);
		// 如果库存小于0，在内存中记录该商品秒杀结束，并返回秒杀结束的提示
		if (stock < 0) {
			localOverMap.put(itemId, true);
			return Result.error(CodeMsg.FLASHSALE_OVER);
		}
		
		// 根据用户ID和商品ID获取秒杀订单
		FlashsaleOrder flashsaleOrder = flashsaleService.getFlashsaleOrderByUserIdAndItemId(user.getId(), itemId); // ④
		// 如果该用户已有对该商品的秒杀订单，判断为重复秒杀
		if (flashsaleOrder != null) {
			return Result.error(CodeMsg.REPEATE_FLASHSALE);
		}
		
		// 发送消息给RabbitMQ消息队列
		var flashsaleMessage = new FlashsaleMessage();
		flashsaleMessage.setUser(user);
		flashsaleMessage.setItemId(itemId);
		// 让秒杀消息进入队列
		mqSender.sendFlashsaleMessage(flashsaleMessage); // ⑤
		return Result.success(0);
	}

	/**
	 * 获取订单状态
	 * 返回orderId：成功
	 * 返回-1：秒杀失败
	 * 返回0： 排队中
	 */
	@GetMapping(value = "/result")
	@ResponseBody
	@AccessLimit // 限制该方法必须登录才能访问
	public Result<Long> flashsaleResult(Model model, User user, @RequestParam("itemId") long itemId) {
		model.addAttribute("user", user);
		// 调用FlashsaleService的getFlashsaleResult()方法来获取秒杀结果
		long result = flashsaleService.getFlashsaleResult(user.getId(), itemId);
		return Result.success(result);
	}
}
