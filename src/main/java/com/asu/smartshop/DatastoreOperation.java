package com.asu.smartshop;

import com.asu.jing.smartshop.Plan;
import com.asu.jing.smartshop.ShopItem;
import com.asu.jing.smartshop.SmartPlan;
import com.google.appengine.api.datastore.*;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by Jianan on 5/4/2017.
 */
public class DatastoreOperation {
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
    private String api_key = null;
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";
    private static final String[] PRODUCT_NAME = { "water", "tv", "comuter", "book", "iphone", "apple", "banana",
            "pizza", "pork", "fork", "beef", "pant", "pen", "pencil", "gas", "mask", "camera", "watch", "beer", "coke",
            "salt", "pie", "juice", "orange", "mirro", "limb" };
    private static final String[] STORES_NAME = { "safeway", "walmart", "costco", "target" };

    Map<String, String> storeIDNameMap = null;
    public DatastoreOperation(String api_key){
        datastore = DatastoreServiceFactory.getDatastoreService();
        this.api_key = api_key;

    }
    public Map<String, String> getStoreIDNameMap() {
        return storeIDNameMap;
    }
    public QueryResultList<Entity> getStoreInRange(double lat, double lng, double radius) {
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(Integer.MAX_VALUE);//or page size
        double lat_meter = 0.00000901612 * radius;
        double lng_meter = 0.00001075526 * radius;
        storeIDNameMap = new HashMap<>();
        Query.CompositeFilter cf = new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.<Query.Filter>asList(
                new Query.FilterPredicate(LATITUDE, Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                        String.valueOf(lat - lat_meter)),
                new Query.FilterPredicate(LATITUDE, Query.FilterOperator.LESS_THAN_OR_EQUAL,
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

    private JSONObject getResult(){
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
        for(Plan p : plans) {
            System.out.println(p.getTotalPrice());
            System.out.println(p.getTotalDistance());
            p.organize();
            System.out.println(p.getTotalCost());
            System.out.println("***********************");
        }
        //}
        return null;
    }

    public int getNumOfStore(List<ShopItem> shopItems ){
        int counter = 0;
        Set<String> set = new HashSet<>();
        for(ShopItem shopItem : shopItems){
            //System.out.println(shopItem.toString());
            set.add(shopItem.storeName);
        }

        return set.size();
    }
    public List<ShopItem> generateShopItemList(QueryResultList<Entity> stores, Set<String> shopList){
        List<ShopItem> shopItemList = new ArrayList<>();

        for (Entity store : stores) {
            FetchOptions fetchOptions = FetchOptions.Builder.withLimit(Integer.MAX_VALUE);
            String storeName = String.valueOf(store.getProperty(PLACE_ID));
            double latitude = Double.parseDouble((String)store.getProperty(LATITUDE));
            double longitude = Double.parseDouble((String)store.getProperty(LONGITUDE));

            Key key = store.getKey();
            Query qProd = new Query(PRODUCT_ENTITY).setFilter(new Query.FilterPredicate("key", Query.FilterOperator.EQUAL, key));
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
