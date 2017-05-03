package com.asu.jing.smartshop;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jing
 *
 */
public class Path {
	private ArrayList<Store> stores;
	private double dis;

	public Path() {
		this.stores = null;
		this.dis = 0.0;
	}
	
	public Path(List<Store> order, double dis) {
		this.stores = (ArrayList<Store>) order;
		this.dis = dis;
	}
	
	public ArrayList<Store> getStores() {
		return stores;
	}

	public void setStores(ArrayList<Store> stores) {
		this.stores = stores;
	}

	public double getDis() {
		return dis;
	}

	public void setDis(double dis) {
		this.dis = dis;
	}

	public void setPath(List<Store> order, double dis) {
		this.stores = (ArrayList<Store>) order;
		this.dis = dis;
	}
}
