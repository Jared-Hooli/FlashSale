package com.czj.dev.rabbitmq;

import com.czj.dev.redis.RedisUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class FlashsaleSender {
	
	private final AmqpTemplate amqpTemplate;

	public FlashsaleSender(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void sendFlashsaleMessage(FlashsaleMessage flashsaleMessage) throws JsonProcessingException {
		// 将FlashsaleMessage转换成字符串
		String msg = RedisUtil.beanToString(flashsaleMessage);
		// 发送消息
		amqpTemplate.convertAndSend(MQConfig.FLASHSALE_QUEUE, msg);
	}
}
