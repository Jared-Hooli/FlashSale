package com.czj.dev.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
	
	public static final String FLASHSALE_QUEUE = "flashsale.queue";

	// 配置Queue，对应于消息队列
	@Bean
	public Queue queue() {
		return new Queue(FLASHSALE_QUEUE, true);
	}
}
