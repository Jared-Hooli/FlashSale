package com.czj.dev.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import com.czj.dev.access.AccessLimit;
import com.czj.dev.domain.FlashsaleItem;
import com.czj.dev.domain.User;
import com.czj.dev.redis.RedisUtil;
import com.czj.dev.redis.ItemKey;
import com.czj.dev.result.Result;
import com.czj.dev.service.FlashsaleService;
import com.czj.dev.vo.ItemDetailVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@Controller
@RequestMapping("/item")
public class ItemController {
	
	private final FlashsaleService flashsaleService;
	private final RedisUtil redisUtil;

	// 定义ThymeleafViewResolver用于解析Thymeleaf页面模板
	private final ThymeleafViewResolver thymeleafViewResolver;

	public ItemController(FlashsaleService flashsaleService, RedisUtil redisUtil,
			ThymeleafViewResolver thymeleafViewResolver) {
		this.flashsaleService = flashsaleService;
		this.redisUtil = redisUtil;
		this.thymeleafViewResolver = thymeleafViewResolver;
	}

	@GetMapping("/list")
	@ResponseBody
	@AccessLimit // 限制该方法必须登录才能访问
	public String list(HttpServletRequest request, HttpServletResponse response, User user) {
		// 从Redis缓存中取数据
		String html = redisUtil.get(ItemKey.itemList, "", String.class);
		// 如果缓存中有HTML页面，直接返回HTML页面
		if (!StringUtils.isEmpty(html)) {
			return html;
		}
		// 如果缓存中没有HTML页面才会去执行查询
		// 查询秒杀商品列表
		List<FlashsaleItem> itemList = flashsaleService.listFlashsaleItem(); // ①
		IWebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(),
				Map.of("user", user, "itemList", itemList));
		// 以item_list.html文件作为页面模板, 渲染静态的HTML内容
		html = thymeleafViewResolver.getTemplateEngine().process("item_list", ctx);
		// 将静态HTML内容存入缓存
		if (!StringUtils.isEmpty(html)) {
			redisUtil.set(ItemKey.itemList, "", html); // ②
		}
		return html;
	}

	@GetMapping(value = "/detail/{itemId}")
	@ResponseBody
	@AccessLimit // 限制该方法必须登录才能访问
	public Result<ItemDetailVo> detail(User user, @PathVariable("itemId") long itemId) {
		FlashsaleItem item = flashsaleService.getFlashsaleItemById(itemId);
		// 获取秒杀开始时间
		long startAt = item.getStartDate().getTime();
		// 获取秒杀的结束时间
		long endAt = item.getEndDate().getTime();
		long now = System.currentTimeMillis();
		// 定义距离开始秒杀还有多久的变量
		int remainSeconds;
		if (now < startAt) {
			// 秒杀还没开始
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) {
			// 秒杀已结束
			remainSeconds = -1;
		} else {
			// 秒杀进行中
			remainSeconds = 0;
		}
		// 定义秒杀还剩多久结束的变量
		var leftSeconds = (int) ((endAt - now) / 1000);
		// 创建ItemDetailVo，用于封装秒杀商品详情
		ItemDetailVo itemDetailVo = new ItemDetailVo();
		itemDetailVo.setFlashsaleItem(item);
		itemDetailVo.setUser(user);
		itemDetailVo.setRemainSeconds(remainSeconds);
		itemDetailVo.setLeftSeconds(leftSeconds);
		return Result.success(itemDetailVo);
	}
}
