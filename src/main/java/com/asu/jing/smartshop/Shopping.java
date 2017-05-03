package com.asu.jing.smartshop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @author Jing
 *
 */
public class Shopping {
	private Table<Store, String, Double> priceTable;
	private Location currentLoc;
	private double timeCostFactor = 0.0;
	private ArrayList<Plan> previousPlans;

	public Shopping(Location currentLoc, double tcf) {
		this.priceTable = HashBasedTable.create();
		this.currentLoc = currentLoc;
		this.timeCostFactor = tcf;
	}
	
	public Shopping(double currentLon, double currentLat, double tcf) {
		this.priceTable = HashBasedTable.create();
		this.currentLoc = new Location(currentLon, currentLat);
		this.timeCostFactor = tcf;
	}
	
	public void addProduct(String productName, String storeName, double longitude, double latitude, double price)
	{
		Store store = getStore(storeName,longitude,latitude);
		this.priceTable.put(store, productName, price);
	}
	
	public void addProduct(String productName, Store store, double price)
	{
		this.priceTable.put(store, productName, price);
	}
	
	public Store getStore(String storeName, double longitude, double latitude)
	{
		Set<Store> stores = getStoreList();
		for(Store s : stores)
		{
			if(s.getName().equals(storeName))	return s;
		}
		Store store = new Store(storeName, longitude, latitude);
		return store;
	}
	
	public ArrayList<Plan> getSortedPlans(int levelOfCalculation)
	{
		if(levelOfCalculation > numOfStores() || levelOfCalculation <= 0){ 
			System.out.println("numOfStores is "+numOfStores() + " levelOfCalculation is "+levelOfCalculation);
			return null;
		}
		else
		{
			ArrayList<Plan> plans = getOneStorePlan();
			for(int i=2; i<=levelOfCalculation; i++)
			{
				plans.addAll(getMultipleStorePlan(i));
			}
			Collections.sort(plans);
			this.previousPlans = plans;
			return plans;
		}
	}
	
	public ArrayList<Plan> changeTimeCostFactor(double tcf)
	{
		if(previousPlans == null) return null;
		this.timeCostFactor = tcf;
		for(Plan p: previousPlans)
		{
			p.setTimeCostFactor(tcf);
		}
		Collections.sort(previousPlans);
		return previousPlans;
	}
	
	public ArrayList<Plan> getMultipleStorePlan(int i)
	{
		ArrayList<Plan> plans = new ArrayList<Plan>();
		
		Set<Store> sList = getStoreList();
		Set<String> pList = getProductList();
		Store[] sArray = sList.toArray(new Store[sList.size()]);
		
		int numOfStores = numOfStores();
		
		ArrayList<Integer[]> list = getAllCombination(numOfStores, i);
		for(Integer[] indexs : list)
		{
			CombinedStore cStore = new CombinedStore(pList, this.currentLoc, this.timeCostFactor);
			for(Integer index :indexs)
			{
				Map<String, Double> pMap = this.priceTable.row(sArray[index]);
				cStore.addStoreItem(sArray[index], pMap);
			}
			Plan p = cStore.organize();
			if(p!=null)
			{
				p.organize();
				plans.add(p);
			}
		}
		return plans;
	}
	
	public ArrayList<Integer[]> getAllCombination(int max, int num)
	{
		Integer[] first = new Integer[num];
		for(int i=0; i<num; i++)
		{
			first[i] = i;
		}
		ArrayList<Integer[]> list = new ArrayList<Integer[]>();
		list.add(first);
		addToList(list,max,num);
		return list;
	}
	
	private void addToList(ArrayList<Integer[]> list, int max, int num)
	{
		Integer[] last = list.get(list.size()-1);
		for(int i=1; i<=num; i++)
		{
			if(last[num-i] < max-i)
			{
				Integer[] next = last.clone();
				next[num-i] ++;
				for(int y=num-i+1; y< num; y++)
				{
					next[y] = next[y-1] +1;
				}
				list.add(next);
				addToList(list,max,num);
				return;
			}
		}
	}
	
	public ArrayList<Plan> getOneStorePlan()
	{
		ArrayList<Plan> plans = new ArrayList<Plan>();
		int numOfProducts = numOfProducts();
		Set<Store> sList = getStoreList();
		for(Store s: sList)
		{
			Map<String, Double> pMap = this.priceTable.row(s);
			if (numOfProducts == pMap.size())
			{
				Plan p = new Plan(this.currentLoc, this.timeCostFactor);
				for(Map.Entry<String, Double> entry : pMap.entrySet())
				{
					p.addPlanItem(s, entry.getKey(), entry.getValue());
				}
				p.organize();
				plans.add(p);
			}
		}
		return plans;
	}
	
	public Set<String> getProductList()
	{
		return this.priceTable.columnKeySet();
	}
	
	public int numOfProducts()
	{
		return getProductList().size();
	}
	
	public Set<Store> getStoreList()
	{
		return this.priceTable.rowKeySet();
	}
	
	public int numOfStores()
	{
		return getStoreList().size();
	}
}
