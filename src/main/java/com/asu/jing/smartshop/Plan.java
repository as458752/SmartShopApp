package com.asu.jing.smartshop;

import org.json.simple.JSONObject;

import java.util.*;

/**
 * @author Jing
 *
 *         This class describe the plan to buy a list of products
 *
 */
public class Plan implements Comparable<Plan> {
	private ArrayList<PlanItem> planItems;
	private Set<Store> stores;
	private Location currentLoc;
	private double totalDis;
	private double totalPri;
	private double totalCos;
	private Path shortestPath;
	private static double GAS_PRICE = 2.0;
	private static double TIMECOST = 10.0;
	private static double SPEED = 40.0;
	private double timeCostFactor = 0.0;

	public Plan(Location currentLoc, double tcf) {
		planItems = new ArrayList<PlanItem>();
		stores = new HashSet<Store>();
		this.currentLoc = currentLoc;
		this.timeCostFactor = tcf;
	}

	public void addPlanItem(PlanItem pi) {
		this.planItems.add(pi);
		this.stores.add(pi.getStore());
	}

	public void addPlanItem(Store store, String product, double price) {
		PlanItem pi = new PlanItem(store, product, price);
		this.planItems.add(pi);
		this.stores.add(pi.getStore());
	}

	public void computeTotalPrice() {
		double totalPrice = 0.0;
		for (PlanItem pi : planItems) {
			totalPrice += pi.getPrice();
		}
		this.totalPri = totalPrice;
	}

	public double getTotalPrice() {
		return this.totalPri;
	}

	public double getTotalCost() {
		return this.totalCos;
	}

	public double getTimeCostFactor() {
		return timeCostFactor;
	}

	public void setTimeCostFactor(double timeCostFactor) {
		this.timeCostFactor = timeCostFactor;
		double time = this.totalDis / SPEED;
		this.totalCos = this.totalPri + time * TIMECOST * timeCostFactor + this.totalDis * GAS_PRICE;
	}

	public void organize() {
		computeShortestPath();
		this.totalDis = this.shortestPath.getDis();
		computeTotalPrice();
		double time = this.totalDis / SPEED;
		this.totalCos = this.totalPri + time * TIMECOST * timeCostFactor + this.totalDis * GAS_PRICE;
		for(PlanItem pi : planItems)
		{
			pi.setOrder(this.shortestPath.getStores().indexOf(pi.getStore()));
		}
		Collections.sort(this.planItems);
	}

	public ArrayList<PlanItem> getPlanItems() {
		return planItems;
	}

	public double getTotalDistance() {
		return this.totalDis;
	}

	public void computeShortestPath() {
		Path shortest = new Path();
		List<Store> listStores = new ArrayList<Store>(this.stores);
		List<List<Integer>> listP = new ArrayList<List<Integer>>();
		List<Integer> list = new ArrayList<Integer>();
		for(int i=0; i<this.stores.size(); i++)	list.add(i);
		permute(list, 0, listP);
		double disO = 0.0;
		List<Integer> listS = null;
		for(List<Integer> l:listP)
		{
			double disN = GetTotalDistance(l,listStores);
			if(disN < disO || listS == null)
			{
				disO = disN;
				listS = l;
			}
		}
		shortest.setDis(disO);
		ArrayList<Store> shortStore = new ArrayList<Store>();
		for(Integer i : listS)	shortStore.add(listStores.get(i));
		shortest.setStores(shortStore);
		this.shortestPath = shortest;
	}

	public Path getShortestPath() {
		return this.shortestPath;
	}

	private static void permute(List<Integer> arr, int k, List<List<Integer>> allPermutation) {
		for (int i = k; i < arr.size(); i++) {
			Collections.swap(arr, i, k);
			permute(arr, k + 1, allPermutation);
			Collections.swap(arr, k, i);
		}
		if (k == arr.size() - 1) {
			allPermutation.add(new ArrayList<Integer>(arr));
		}
	}

	private double GetTotalDistance(List<Integer> arr, List<Store> listS) {
		double dis = this.currentLoc.distanceTo(listS.get(arr.get(0)).getLoc());
		for (int i = 1; i < arr.size(); i++) {
			dis += listS.get(arr.get(i-1)).getLoc().distanceTo(listS.get(arr.get(i)).getLoc());
		}
		dis += listS.get(arr.get(arr.size() - 1)).getLoc().distanceTo(this.currentLoc);
		return dis;
	}

	public int compareTo(Plan comparePlan) {
		double compareCost = comparePlan.getTotalCost();
		return new Double(this.totalCos - compareCost).intValue();
	}

	public String toString() {
		String output = "";
		output += "Total Cost:" + this.totalCos + "\n" + "Total Distance:" + this.totalDis + "\n" + "Total Price:"
				+ this.totalPri + "\n";
		String storeName = "";
		for(PlanItem pi: this.planItems)
		{
			if(!pi.getStore().getName().equals(storeName))
			{
				storeName = pi.getStore().getName();
				output += storeName + ":\n";
			}
			output += pi.getProduct() + "," + pi.getPrice() + "\n";
		}
		return output;
	}

	public ArrayList<String> getPlan()
	{
		ArrayList<String> strPlan = new ArrayList<String>();
		String eachStore = "";
		String storeName = "";
		for(PlanItem pi: this.planItems)
		{
			if(!pi.getStore().getName().equals(storeName))
			{
				if(!eachStore.equals(""))	strPlan.add(eachStore);
				storeName = pi.getStore().getName();
				eachStore = storeName;
			}
			eachStore += "," + pi.getProduct() + "," + pi.getPrice();
		}
		if(!eachStore.equals(""))	strPlan.add(eachStore);
		return strPlan;
	}
	public JSONObject getPlanJson() {
		JSONObject jo = new JSONObject();
		jo.put("total_cost", String.valueOf(this.totalCos));
		jo.put("total_distance", String.valueOf(this.totalDis));
		jo.put("total_price", String.valueOf(this.totalPri));
		String storeName = "";
		List<JSONObject> items = new ArrayList<>();
		List<JSONObject> products = new ArrayList<>();
		for(PlanItem pi: this.planItems)
		{
			JSONObject storeJson = null;
			if(!pi.getStore().getName().equals(storeName))
			{
				storeJson = new JSONObject();
				items.add(storeJson);
				storeName = pi.getStore().getName();
				storeJson.put("store_name", storeName);
				storeJson.put("latitude", String.valueOf(pi.getStore().getLoc().getLatitude()));
				storeJson.put("longitude", String.valueOf(pi.getStore().getLoc().getLongitude()));
				products = new ArrayList<>();
				storeJson.put("products", products);
				items.add(storeJson);
			}
			JSONObject prod = new JSONObject();
			prod.put("product_name", pi.getProduct());
			prod.put("price", String.valueOf(pi.getPrice()));
			products.add(prod);
		}
		jo.put("plan_items", items);
		return jo;
	}
}