package com.asu.smartshop;

import com.asu.jing.smartshop.Plan;
import com.asu.jing.smartshop.ShopItem;
import com.asu.jing.smartshop.Shopping;
import com.asu.jing.smartshop.SmartPlan;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.QueryResultList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
public class SmartShopServlet extends HttpServlet {
    private String api_key = null;
    Map<String, String> storeIDNameMap = null;

    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*FetchOptions fetchOptions = FetchOptions.Builder.withLimit(PAGE_SIZE);

		// If this servlet is passed a cursor parameter, let's use it.
		String startCursor = req.getParameter("cursor");
		if (startCursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
		}

		Query q = new Query("Person").addSort("name", SortDirection.ASCENDING);
		PreparedQuery pq = datastore.prepare(q);

		QueryResultList<Entity> results;
		try {
			results = pq.asQueryResultList(fetchOptions);
		} catch (IllegalArgumentException e) {
			// IllegalArgumentException happens when an invalid cursor is used.
			// A user could have manually entered a bad cursor in the URL or
			// there
			// may have been an internal implementation detail change in App
			// Engine.
			// Redirect to the page without the cursor parameter to show
			// something
			// rather than an error.
			resp.sendRedirect("/people");
			return;
		}

		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter w = resp.getWriter();
		w.println("<!DOCTYPE html>");
		w.println("<meta charset=\"utf-8\">");
		w.println("<title>Cloud Datastore Cursor Sample</title>");
		w.println("<ul>");
		for (Entity entity : results) {
			w.println("<li>" + entity.getProperty("name") + "</li>");
		}
		w.println("</ul>");

		String cursorString = results.getCursor().toWebSafeString();
*/
		// This servlet lives at '/people'.
		String api_key = getServletContext().getInitParameter("API_KEY");
		resp.getWriter().println("smart shop welcome " + api_key);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	   /*String content = req.getParameter("content");
	   if (content == null || content.isEmpty()) {
	      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing content");
	      return;
	   }*/
        api_key = getServletContext().getInitParameter("API_KEY");
	   DatastoreOperation dso = new DatastoreOperation(api_key);
	    double curLat = Double.parseDouble(req.getParameter("Latitude"));
        double curLng = Double.parseDouble(req.getParameter("Longitude"));
        double tcf = Double.parseDouble(req.getParameter("tcf"));
        int numProducts = Integer.parseInt(req.getParameter("NumOfProducts"));
        List<String> shoppingList = new ArrayList<>();
        List<JSONObject> shopResult = new ArrayList<>();
        JSONArray planArray = new JSONArray();
        for(int i = 0; i < numProducts; i++){
            shoppingList.add(req.getParameter("Product"+String.valueOf(i)));
        }
        QueryResultList<Entity> stores = dso.getStoreInRange(curLat, curLng ,10000);
        String[] spl = {"water"};
        Set<String> shopList = new HashSet<>(shoppingList);

        List<ShopItem> shopItems = dso.generateShopItemList(stores, shopList);
        System.out.println("number of shopitems " + shopItems.size());
        for(ShopItem si : shopItems){
            System.out.println(si.toString());
        }

        int numOfStores = dso.getNumOfStore(shopItems);
        System.out.println("number of stores: " + numOfStores);
        SmartPlan sPlan = new SmartPlan(shopItems, curLat, curLng, tcf);
        List<Plan> plans = sPlan.getSortedPlans(Math.min(3,Math.min(shopList.size(), numOfStores)));
        if(plans == null){
            System.out.println("plans is null");
            return;
        }
        System.out.println("number of plans: " + plans.size());
        JSONObject jsonData = sPlan.changeTimeCostFactor(tcf);
        jsonData.put("storeIDNameMap", dso.getStoreIDNameMap());

        /*jsonData.put("Latitude","100");
        jsonData.put("Longitude", "300");

        jsonData.put("plans", planArray);*/
	    resp.setContentType("application/json");
	    PrintWriter out = resp.getWriter();
	    out.println(jsonData);
		out.append("ddddd");
	    out.close();
	}
	private void  test(){
       // SmartPlan sPlan = new SmartPlan(, 0.00, 0.0, 0.5);
        Shopping s = new Shopping(0.00,0.00,0.0);
        s.addProduct("apple", "store1", 0.01, 0.00, 5.0);
        s.addProduct("apple", "store2", 0.02, 0.00, 4.0);
        s.addProduct("apple", "store3", 0.03, 0.00, 6.0);
        s.addProduct("apple", "store4", 0.04, 0.00, 7.0);

        s.addProduct("water", "store1", 0.01, 0.00, 3.0);
        s.addProduct("water", "store2", 0.02, 0.00, 3.0);
        s.addProduct("water", "store4", 0.04, 0.00, 4.0);
        s.addProduct("water", "store5", 0.05, 0.00, 2.0);

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
        for(Plan p : plansss)
        {
            for(String str: p.getPlan())
            {
                System.out.println(str);
            }
            System.out.println(p.toString());
            System.out.println("***********************");
        }

    }


}
