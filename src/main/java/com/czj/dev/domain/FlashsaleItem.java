package com.czj.dev.domain;

import java.util.Date;

public class FlashsaleItem extends Item {
	private Long id;
	private Long itemId;
	private double flashsalePrice;
	private Integer stockCount;
	private Date startDate;
	private Date endDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public double getFlashsalePrice() {
		return flashsalePrice;
	}

	public void setFlashsalePrice(double flashsalePrice) {
		this.flashsalePrice = flashsalePrice;
	}

	public Integer getStockCount() {
		return stockCount;
	}

	public void setStockCount(Integer stockCount) {
		this.stockCount = stockCount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "FlashsaleItem{" +
				"id=" + id +
				", itemId=" + itemId +
				", flashsalePrice=" + flashsalePrice +
				", stockCount=" + stockCount +
				", startDate=" + startDate +
				", endDate=" + endDate +
				'}';
	}
}

