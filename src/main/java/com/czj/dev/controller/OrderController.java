package com.czj.dev.controller;

import com.czj.dev.access.AccessLimit;
import com.czj.dev.domain.FlashsaleItem;
import com.czj.dev.domain.Order;
import com.czj.dev.domain.User;
import com.czj.dev.result.CodeMsg;
import com.czj.dev.result.Result;
import com.czj.dev.service.FlashsaleService;
import com.czj.dev.vo.OrderDetailVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	private final FlashsaleService flashsaleService;

	public OrderController(FlashsaleService flashsaleService) {
		this.flashsaleService = flashsaleService;
	}

	@GetMapping("/detail")
	@ResponseBody
	@AccessLimit // 限制该方法必须登录才能访问
	public Result<OrderDetailVo> detail(User user, @RequestParam("orderId") long orderId) {
		// 根据订单ID和用户ID获取订单
		Order order = flashsaleService.getOrderByIdAndOwnerId(orderId, user.getId());
		// 如果订单为null，表明还没有订单
		if (order == null) {
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
		long itemId = order.getItemId();
		FlashsaleItem item = flashsaleService.getFlashsaleItemById(itemId);
		// 用OrderDetailVo封装订单、订单关联的商品和订单对应的用户
		var orderDetailVo = new OrderDetailVo();
		orderDetailVo.setOrder(order);
		orderDetailVo.setFlashsaleItem(item);
		orderDetailVo.setUser(user);
		// 返回OrderDetailVo
		return Result.success(orderDetailVo);
	}
}
