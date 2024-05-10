package com.czj.dev.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.czj.dev.domain.FlashsaleOrder;

@Mapper
public interface FlashsaleOrderMapper {
	
	// 根据用户ID和商品ID获取秒杀订单
	@Select("select flashsale_order_id as id, user_id as userId, order_id as "
			+ "orderId, item_id as itemId from flashsale_order " 
			+ "where user_id=#{userId} and item_id=#{itemId}")
	FlashsaleOrder findByUserIdItemId(@Param("userId") long userId, @Param("itemId") long itemId);

	// 插入秒杀订单
	@Insert("insert into flashsale_order(user_id, item_id, order_id) values " 
	+ "(#{userId}, #{itemId}, #{orderId})")
	int save(FlashsaleOrder flashsaleOrder);
	
}
