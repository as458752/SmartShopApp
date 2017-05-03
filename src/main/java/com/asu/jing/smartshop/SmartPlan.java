package com.asu.jing.smartshop;

import java.util.ArrayList;
import java.util.List;
public class SmartPlan {
	private Shopping shopping = null;
	private static final int LEVEL_NUMBER = 10;
	public SmartPlan(List<ShopItem> shopItems, double curLat, double curLng, double tcf){
		shopping = new Shopping(curLng, curLat, tcf);	
		for(ShopItem sItem : shopItems){
			shopping.addProduct(sItem.storeName, sItem.productName, sItem.longitude, sItem.latitude, sItem.price);
		}
	}
	
	public ArrayList<Plan> getSortedPlans(int levelOfCalculation){
		return shopping.getSortedPlans(levelOfCalculation);
	}
	public ArrayList<Plan> getSortedPlans(){
		return shopping.getSortedPlans(LEVEL_NUMBER);
	}
	public ArrayList<Plan> changeTimeCostFactor(double tcf){		
		return shopping.changeTimeCostFactor(tcf);
	}

	
}
