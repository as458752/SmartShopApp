package com.asu.jing.smartshop;


public class ShopItem {
	public String productName;
	public String storeName;
	public double longitude;
	public double latitude;
	public double price;
	public ShopItem (String productName, String storeName,  double latitude, double longitude, double price){
		this.productName = productName;
		this.storeName = storeName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.price = price;
	}
	@Override
	public String toString(){
		
		return "product name: " + productName +
				"\tstore name: " + storeName +
				"\tlongitude" + longitude +
				"\tlatitude" + latitude +
				"\tprice" + price;
	}
}
