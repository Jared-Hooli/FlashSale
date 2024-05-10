package com.czj.dev.domain;

public class Item {
	private Long id;
	private String itemName;
	private String category;
	private String itemImg;
	private String itemDetail;
	private Double itemPrice;
	private Integer stockNum;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getItemImg() {
		return itemImg;
	}

	public void setItemImg(String itemImg) {
		this.itemImg = itemImg;
	}

	public String getItemDetail() {
		return itemDetail;
	}

	public void setItemDetail(String itemDetail) {
		this.itemDetail = itemDetail;
	}

	public Double getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Double itemPrice) {
		this.itemPrice = itemPrice;
	}

	public Integer getStockNum() {
		return stockNum;
	}

	public void setStockNum(Integer stockNum) {
		this.stockNum = stockNum;
	}

	@Override
	public String toString() {
		return "Item{" +
				"id=" + id +
				", itemName='" + itemName + '\'' +
				", category='" + category + '\'' +
				", itemImg='" + itemImg + '\'' +
				", itemDetail='" + itemDetail + '\'' +
				", itemPrice=" + itemPrice +
				", stockNum=" + stockNum +
				'}';
	}
}
