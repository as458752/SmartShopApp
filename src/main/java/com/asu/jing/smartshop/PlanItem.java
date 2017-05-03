package com.asu.jing.smartshop;

/**
 * @author Jing
 *
 *	This class describes where to buy a specified item, including the product name, store and the price 
 */
public class PlanItem {
	private Store store;
	private String product;
	private double price;

	public PlanItem(Store store, String product, double price) {
		super();
		this.store = store;
		this.product = product;
		this.price = price;
	} 

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
