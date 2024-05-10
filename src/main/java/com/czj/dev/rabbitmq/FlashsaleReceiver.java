package com.czj.dev.rabbitmq;

import com.czj.dev.domain.FlashsaleItem;
import com.czj.dev.domain.FlashsaleOrder;
import com.czj.dev.domain.User;
import com.czj.dev.redis.RedisUtil;
import com.czj.dev.service.FlashsaleService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class FlashsaleReceiver {
	
	private final FlashsaleService flashsaleService;

	public FlashsaleReceiver(FlashsaleService flashsaleService) {
		this.flashsaleService = flashsaleService;
	}

	@RabbitListener(queues = MQConfig.FLASHSALE_QUEUE)
	public void receive(String message) throws JsonProcessingException {
		// 将字符串类型的消息转换成FlashsaleMessage对象
		FlashsaleMessage flashsaleMessage = RedisUtil.stringToBean(message, FlashsaleMessage.class);
		// 获取秒杀用户
		User user = flashsaleMessage.getUser();
		// 获取秒杀商品的ID
		long itemId = flashsaleMessage.getItemId();
		// 获取秒杀商品
		FlashsaleItem item = flashsaleService.getFlashsaleItemById(itemId);
		int stock = item.getStockCount();
		// 如果秒杀商品的库存小于0，无法继续秒杀，直接返回
		if (stock <= 0) {
			return;
		}
		// 从Redis缓存根据用户ID和商品ID读取秒杀订单
		FlashsaleOrder flashsaleOrder = flashsaleService.getFlashsaleOrderByUserIdAndItemId(user.getId(), itemId);
		// 如果秒杀订单存在，说明用户正尝试重复秒杀，无需处理，因此直接返回
		if (flashsaleOrder != null) {
			return;
		}
		// 调用FlashsaleService的flashsale()方法执行秒杀
		flashsaleService.flashsale(user, item);
	}
}
