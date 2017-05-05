package com.asu.jing.smartshop;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jing
 *
 */
public class Tester {

	public static void main(String[] args)
	{
		Shopping s = new Shopping(0.00,0.00,0.0);
		s.addProduct("mouse", "store1", 0.01, 0.00, 5.0);
		s.addProduct("mouse", "store2", 0.02, 0.00, 4.0);
		s.addProduct("mouse", "store3", 0.03, 0.00, 6.0);
		s.addProduct("mouse", "store4", 0.04, 0.00, 7.0);

		s.addProduct("keyboard", "store1", 0.01, 0.00, 3.0);
		s.addProduct("keyboard", "store2", 0.02, 0.00, 3.0);
		s.addProduct("keyboard", "store4", 0.04, 0.00, 4.0);
		s.addProduct("keyboard", "store5", 0.05, 0.00, 2.0);

		s.addProduct("orange", "store2", 0.02, 0.00, 7.0);
		s.addProduct("orange", "store3", 0.03, 0.00, 4.0);
		s.addProduct("orange", "store4", 0.04, 0.00, 8.0);

		s.addProduct("rice", "store1", 0.01, 0.00, 2.0);
		s.addProduct("rice", "store2", 0.02, 0.00, 5.0);
		s.addProduct("rice", "store3", 0.03, 0.00, 4.0);
		s.addProduct("rice", "store5", 0.05, 0.00, 1.0);

		ArrayList<Plan> planss = s.getSortedPlans(3);
		for(Plan p : planss)
		{
			for(String str: p.getPlan())
			{
				System.out.println(str);
			}
			System.out.println(p.toString());
			System.out.println("***********************");
		}


		System.out.println("----------------------------------------");

		Shopping fromSession = s;

		ArrayList<Plan> plansss = fromSession.changeTimeCostFactor(0.5);
		JSONObject json = new JSONObject();
		List<JSONObject> plansJson = new JSONArray();
		for(Plan p : plansss) {

			System.out.println(p.toString());
			plansJson.add(p.getPlanJson());
			/*JSONObject jo = p.getPlanJson();
			double total_cost = (double) jo.get("total_cost");
			double total_distance = (double) jo.get("total_distance");
			double total_price = (double) jo.get("total_price");
			JSONArray items = (JSONArray) jo.get("plan_items");
			for(int i = 0; i < items.size(); i++) {
				JSONObject storeJson = (JSONObject) items.get(i);
				String store_name = (String)storeJson.get("store_name");
				double lat = Double.parseDouble((String)storeJson.get("latitude"));
				double lng = Double.parseDouble((String)storeJson.get("longitude"));
				JSONArray products = (JSONArray) storeJson.get("products");
				for(int j = 0; j < products.size(); j++) {
					JSONObject prodJson = (JSONObject) products.get(j);
					String product_name = (String)prodJson.get("product_name");
					String price = (String)prodJson.get("price");
				}


			}*/
			System.out.println("***********************");
		}
		json.put("plans", plansJson);
	}


}