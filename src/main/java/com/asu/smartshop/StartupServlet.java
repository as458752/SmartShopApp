package com.asu.smartshop;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.mail.internet.NewsAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.asu.jing.smartshop.Path;
import com.asu.jing.smartshop.Plan;
import com.asu.jing.smartshop.ShopItem;
import com.asu.jing.smartshop.SmartPlan;
import com.asu.jing.smartshop.Store;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.search.query.QueryParser.query_return;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.common.collect.ImmutableList;

public class StartupServlet extends HttpServlet {
	private DatastoreService datastore = null;
	private static final int PAGE_SIZE = 50;
	static final String IS_POPULATED_ENTITY = "IsPopulatedStore";
	static final String IS_POPULATED_KEY_NAME = "is-populated-store";
	static final String NEARBYPLACE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=33.414691,-111.9437592&radius=40000&type=supermarket&keyword=safeway&key=AIzaSyBeTB21xGaef3bvrlWinTe6GdvSZHO8iMU";
	private static final String STORE_ENTITY = "Store";
	private static final String PRODUCT_ENTITY = "Product";
	private static final String PRODUCT = "product";
	private static final String NAME_PROPERTY = "name";
	private static final String PLACE_ID = "place_id";

	private static final String LATITUDE = "lat";
	private static final String LONGITUDE = "lng";
	private static final String[] PRODUCT_NAME = { "water", "tv", "comuter", "book", "iphone", "apple", "banana",
			"pizza", "pork", "fork", "beef", "pant", "pen", "pencil", "gas", "mask", "camera", "watch", "beer", "coke",
			"salt", "pie", "juice", "orange", "mirro", "limb" };
	private static final String[] STORES_NAME = { "safeway", "walmart", "costco", "target" };
	Map<String, String> storeIDNameMap = null;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		datastore = DatastoreServiceFactory.getDatastoreService();
		Key isPopulatedKey = KeyFactory.createKey(IS_POPULATED_ENTITY, IS_POPULATED_KEY_NAME);
		String api_key = getServletContext().getInitParameter("API_KEY");

		boolean isAlreadyPopulated;
		try {
			datastore.get(isPopulatedKey);
			isAlreadyPopulated = true;
		} catch (EntityNotFoundException expected) {
			isAlreadyPopulated = false;
		}
		if (isAlreadyPopulated) {
			// resp.getWriter().println("ok");
			System.out.println("\n\n\n\n\n after all list");
			List<Plan> plans = testSmartShop();
			resp.getWriter().println("ok");
			//listAll(req, resp);
			listPlans(req, resp, plans);
			return;
		}
		for (String storename : STORES_NAME) {
			// String reString = SendUrl.executeGet(NEARBYPLACE);
			String reString = GoogleMapApi.GMapService(GoogleMapApi.NEARBY_PLACE, String.valueOf(33.414691),
					String.valueOf(-111.9437592), 50000, "supermarket", storename, api_key);
			System.out.println("name is " + storename + reString);
			JSONParser parser = new JSONParser();
			JSONObject json;
			try {
				json = (JSONObject) parser.parse(reString);
				JSONArray results = (JSONArray) json.get("results");
				addStore(datastore, results);
				datastore.put(new Entity(isPopulatedKey));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		//listAll(req, resp);
		// resp.getWriter().println("ok");
		
	}

	private void addStore(DatastoreService datastore, JSONArray results) {
		Random random = new Random();
		Iterator iterator = results.iterator();
		ImmutableList.Builder<Entity> stores = ImmutableList.builder();
		ImmutableList.Builder<Entity> products = ImmutableList.builder();

		while (iterator.hasNext()) {
			JSONObject result = (JSONObject) iterator.next();
			if (result == null)
				break;
			String name = (String) result.get("name");
			String place_id = (String) result.get("place_id");
			JSONObject geometry = (JSONObject) result.get("geometry");
			JSONObject location = (JSONObject) geometry.get("location");
			Double lat = (Double) location.get("lat");
			Double lng = (Double) location.get("lng");

			Entity store = new Entity(STORE_ENTITY);

			store.setProperty(NAME_PROPERTY, name);
			store.setProperty(PLACE_ID, place_id);
			store.setProperty(LATITUDE, String.valueOf(lat));
			store.setProperty(LONGITUDE, String.valueOf(lng));

			datastore.put(store);
			Key store_key = store.getKey();

			for (String str : PRODUCT_NAME) {
				int c = random.nextInt(PRODUCT_NAME.length);
				int d = random.nextInt(PRODUCT_NAME.length);
				if (c > d)
					continue;
				Entity productEntity = new Entity(PRODUCT_ENTITY);
				int price = random.nextInt(100);
				productEntity.setProperty(NAME_PROPERTY, str);
				productEntity.setProperty("price", price);
				productEntity.setProperty("key", store_key);
				datastore.put(productEntity);
			}
			System.out.println("name: " + name + "lat: " + lat + "lng: " + lng);
		}
	}

	private void listAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(Integer.MAX_VALUE);//or page size

		// If this servlet is passed a cursor parameter, let's use it.
		String startCursor = req.getParameter("cursor");
		if (startCursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
		}

		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter w = resp.getWriter();
		w.println("<!DOCTYPE html>");
		w.println("<meta charset=\"utf-8\">");
		w.println("<title>Cloud Datastore Cursor Sample</title>");
		w.println("<ul>");
		QueryResultList<Entity> results = getStoreInRange( 33.43146914322 , -111.943759242211,10000);
		w.println("<li>" + results.size() + "</li>");

		for (Entity entity : results) {
			Key key = entity.getKey();
			Query qProd = new Query(PRODUCT_ENTITY).setFilter(new FilterPredicate("key", FilterOperator.EQUAL, key));

			PreparedQuery pqProd = datastore.prepare(qProd);
			QueryResultList<Entity> resultProd = pqProd.asQueryResultList(fetchOptions);
			w.println("<li>" + entity.getProperty("name") + resultProd.size() + "latitude"
					+ entity.getProperty(LATITUDE) + "longitude" + entity.getProperty(LONGITUDE) + "</li>");
			/*
			 * for (Entity ent : resultProd) { w.println("<li>" +
			 * entity.getProperty("name") + ent.getProperty("name") +
			 * ent.getProperty("price") + "</li>"); }
			 */
		}
		w.println("</ul>");

		String cursorString = results.getCursor().toWebSafeString();

		// This servlet lives at '/people'.
		w.println("<a href='/ssstartup?cursor=" + cursorString + "'>Next page</a>");
	}

	private void listPlans(HttpServletRequest req, HttpServletResponse resp, List<Plan> plans) throws IOException {
		
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter w = resp.getWriter();
		w.println("<!DOCTYPE html>");
		w.println("<meta charset=\"utf-8\">");
		w.println("<title>Cloud Datastore Cursor Sample</title>");
		w.println("<ul>");

		for (Plan p: plans) {
			String totalPrice = String.valueOf(p.getTotalPrice());
			String totalDis = String.valueOf(p.getTotalDistance());
			p.organize();
			String totalCost = String.valueOf(p.getTotalCost());
			System.out.println("***********************");
			w.println("<li>" + "total price is " + totalPrice
							+ "total distance is " + totalDis
							+ "total cost is " + totalCost +"</li>");
			StringBuilder sbBuilder = new StringBuilder();
			List<Store> stores = p.getShortestPath().getStores();
			for(Store store : stores){
				sbBuilder.append(store.getName() + ", " + store.getLoc().getLatitude() + ", " + store.getLoc().getLongitude());
			}
			w.println("<li>" + sbBuilder.toString() +"</li>");

		}
		w.println("</ul>");

		
	}
	
	public QueryResultList<Entity> getStoreInRange(double lat, double lng, double radius) {
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(Integer.MAX_VALUE);//or page size
		double lat_meter = 0.00000901612 * radius;
		double lng_meter = 0.00001075526 * radius;
		storeIDNameMap = new HashMap<>();
		CompositeFilter cf = new CompositeFilter(CompositeFilterOperator.AND, Arrays.<Filter>asList(
						new FilterPredicate(LATITUDE, FilterOperator.GREATER_THAN_OR_EQUAL,
								String.valueOf(lat - lat_meter)),
						new FilterPredicate(LATITUDE, FilterOperator.LESS_THAN_OR_EQUAL,
								String.valueOf(lat + lat_meter))
			    	)
				);
		Query q = new Query(STORE_ENTITY).setFilter(cf);
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
			// resp.sendRedirect("/people");
			//resp.getWriter().println(e.getMessage());
			return null;
		}
		for(Entity entity:results){
			double enLng = Double.parseDouble((String) entity.getProperty(LONGITUDE));
			if(enLng<(lng - lng_meter) || enLng>(lng + lng_meter)){
				results.remove(entity);
			}else{
				storeIDNameMap.put(String.valueOf(entity.getProperty(PLACE_ID)),String.valueOf(entity.getProperty(NAME_PROPERTY)));
			}
		}

		return results;
	}
	
	private List<Plan> testSmartShop(){
		double curLat = 33.43146914322;
		double curLng = -111.943759242211;
		double tcf = 1.0;
		QueryResultList<Entity> stores = getStoreInRange(curLat, curLng ,10000);
		String[] spl = {"water"};
		Set<String> shopList = new HashSet<>(Arrays.asList(spl));
		
		List<ShopItem> shopItems = generateShopItemList(stores, shopList);
		System.out.println("number of shopitems " + shopItems.size());

		int numOfStores = getNumOfStore(shopItems);
		System.out.println(numOfStores);
		SmartPlan sPlan = new SmartPlan(shopItems, curLat, curLng, tcf);
		//for(int i = 0; i< 10 ; i++){
		List<Plan> plans =	sPlan.getSortedPlans(1);
			for(Plan p : plans)
			{
				System.out.println(p.getTotalPrice());
				System.out.println(p.getTotalDistance());
				p.organize();
				System.out.println(p.getTotalCost());
				System.out.println("***********************");
			}
		//}
			return plans;
	}
	
	private int getNumOfStore(List<ShopItem> shopItems ){
		int counter = 0;
		Set<String> set = new HashSet<>();
		for(ShopItem shopItem : shopItems){
			//System.out.println(shopItem.toString());
			set.add(shopItem.storeName);
		}
		
		return set.size();
	}
	private List<ShopItem> generateShopItemList(QueryResultList<Entity> stores, Set<String> shopList){
		List<ShopItem> shopItemList = new ArrayList<>();
		
		for (Entity store : stores) {
			FetchOptions fetchOptions = FetchOptions.Builder.withLimit(Integer.MAX_VALUE);
			String storeName = String.valueOf(store.getProperty(PLACE_ID));
			double latitude = Double.parseDouble((String)store.getProperty(LATITUDE));
			double longitude = Double.parseDouble((String)store.getProperty(LONGITUDE));

			Key key = store.getKey();
			Query qProd = new Query(PRODUCT_ENTITY).setFilter(new FilterPredicate("key", FilterOperator.EQUAL, key));
			store.getProperty(PLACE_ID);
			PreparedQuery pqProd = datastore.prepare(qProd);
			QueryResultList<Entity> products = pqProd.asQueryResultList(fetchOptions);
						
			 for (Entity product : products) { 
				if(shopList.contains(product.getProperty(NAME_PROPERTY))){
					String productName = String.valueOf(product.getProperty(NAME_PROPERTY)); 
					double price = Double.parseDouble(String.valueOf(product.getProperty("price")));
					ShopItem shopItem = new ShopItem(productName, storeName, latitude, longitude, price);
					shopItemList.add(shopItem);
				}

			 }			 
		}
		
		return shopItemList;
	}

}




























