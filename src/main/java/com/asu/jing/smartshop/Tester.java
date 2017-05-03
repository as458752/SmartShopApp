package com.asu.jing.smartshop;

import java.util.ArrayList;

/**
 * @author Jing
 *
 */
public class Tester {
	
	public static void main(String[] args)
	{
		Shopping s = new Shopping(1.00,1.00,0.0);
		s.addProduct("apple", "store1", 1.01, 1.01, 5.0);
		s.addProduct("apple", "store2", 1.02, 1.02, 4.0);
		s.addProduct("apple", "store3", 1.03, 1.03, 6.0);
		s.addProduct("apple", "store4", 1.04, 1.04, 7.0);      
		
		s.addProduct("water", "store1", 1.01, 1.01, 3.0);
		s.addProduct("water", "store2", 1.02, 1.02, 3.0);
		s.addProduct("water", "store4", 1.04, 1.04, 4.0);
		s.addProduct("water", "store5", 1.05, 1.05, 2.0);
		
		s.addProduct("orange", "store2", 1.02, 1.02, 7.0);
		s.addProduct("orange", "store3", 1.03, 1.03, 4.0);
		s.addProduct("orange", "store4", 1.04, 1.04, 8.0);
		
		s.addProduct("rice", "store1", 1.01, 1.01, 2.0);
		s.addProduct("rice", "store2", 1.02, 1.02, 5.0);
		s.addProduct("rice", "store3", 1.03, 1.03, 4.0);
		s.addProduct("rice", "store4", 1.04, 1.04, 7.0);
		
		
		ArrayList<Plan> planss = s.getSortedPlans(2);
		for(Plan p : planss)
		{
			System.out.println(p.getTotalPrice());
			System.out.println(p.getTotalDistance());
			p.organize();
			System.out.println(p.getTotalCost());
			System.out.println("***********************");
		}
		
		System.out.println("-----------------------------");
		
		ArrayList<Plan> plansss = s.changeTimeCostFactor(0.5);
		for(Plan p : plansss)
		{
			System.out.println(p.getTotalPrice());
			System.out.println(p.getTotalDistance());
			p.organize();
			System.out.println(p.getTotalCost());
			System.out.println("***********************");
		}
	}
}
