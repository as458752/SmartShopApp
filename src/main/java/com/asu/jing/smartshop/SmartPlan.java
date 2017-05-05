package com.asu.jing.smartshop;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class SmartPlan {
	private Shopping shopping = null;
	private static final int LEVEL_NUMBER = 10;
	public SmartPlan(List<ShopItem> shopItems, double curLat, double curLng, double rcf){
		shopping = new Shopping(curLng, curLat, rcf);
		for(ShopItem sItem : shopItems){
			shopping.addProduct( sItem.productName, sItem.storeName, sItem.longitude, sItem.latitude, sItem.price);
		}
	}
	
	public ArrayList<Plan> getSortedPlans(int levelOfCalculation ){
		return shopping.getSortedPlans(levelOfCalculation);
	}
	public ArrayList<Plan> getSortedPlans(int levelOfCalculation, double tcf ){
		return shopping.getSortedPlans(levelOfCalculation);
	}
	public ArrayList<Plan> getSortedPlans(){
		return shopping.getSortedPlans(LEVEL_NUMBER);
	}
	public JSONObject changeTimeCostFactor(double tcf){
		ArrayList<Plan> planss = shopping.changeTimeCostFactor(tcf);
		if(planss == null){
			return null;
		}
		JSONObject json = new JSONObject();
		List<JSONObject> plansJson = new JSONArray();
		int i = 0;
		for(Plan p : planss) {
			i++;
			if(i > 10)
				break;
			System.out.println(p.toString());
			plansJson.add(p.getPlanJson());
		}
		json.put("plans", plansJson);
		return json;
	}

	
}
