package com.czj.dev.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.czj.dev.domain.FlashsaleItem;

@Mapper
public interface FlashsaleItemMapper {
	
	// 查询所有秒杀商品
	@Select("select it.*, fi.stock_count, fi.start_date, fi.end_date, fi.flashsale_price "
			+ "from flashsale_item fi left join item_inf it " + "on fi.item_id = it.item_id")
	@Results(id = "itemMapper", value = { @Result(property = "itemId", column = "item_id"),
			@Result(property = "itemName", column = "item_name"), @Result(property = "title", column = "title"),
			@Result(property = "itemImg", column = "item_img"),
			@Result(property = "itemDetail", column = "item_detail"),
			@Result(property = "itemPrice", column = "item_price"),
			@Result(property = "stockNum", column = "stock_num"),
			@Result(property = "flashsalePrice", column = "flashsale_price"),
			@Result(property = "stockCount", column = "stock_count"),
			@Result(property = "startDate", column = "start_date"),
			@Result(property = "endDate", column = "end_date") })
	List<FlashsaleItem> findAll();

	// 根据商品ID查询秒杀商品
	@Select("select it.*, fi.stock_count, fi.start_date, fi.end_date, fi.flashsale_price "
			+ "from flashsale_item fi left join item_inf it "
			+ "on fi.item_id = it.item_id where it.item_id = #{itemId}")
	@ResultMap("itemMapper")
	FlashsaleItem findById(@Param("itemId") long itemId);

	// 更新flashsale_item表中的记录
	@Update("update flashsale_item set stock_count = stock_count - 1" + " where item_id = #{itemId}")
	int reduceStock(FlashsaleItem flashsaleItem);
	
}
