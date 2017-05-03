package com.asu.smartshop;


public class GoogleMapApi {
	public final static int NEARBY_PLACE = 0;
	//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=33.414691,-111.9437592&radius=40000&type=supermarket&keyword=safeway&key=AIzaSyBeTB21xGaef3bvrlWinTe6GdvSZHO8iMU";
	public static String GMapService(int service, String lat, String lng, int radius, String type, String keyword,  String API_KEY){
		String res = null;
		
		switch(service){
			case NEARBY_PLACE:
				String url = generateNearyURL(lat, lng, radius, type, keyword, API_KEY);
				res = SendUrl.executeGet(url);
				break;
			default:
				return null;
		
		}
		
		return res;
	}
	
	public static String generateNearyURL(String lat, String lng, int radius, String type, String keyword, String API_KEY){
		
		String url_root = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
		String location = "location="+lat+","+lng;
		String radiusStr = "radius="+String.valueOf(radius);
		String typeStr= "type="+type;
		String keywordStr = "keyword="+keyword;
		String apikey = "key="+API_KEY;
		return url_root+location + "&" + radiusStr + "&" + typeStr + "&" + 
				keywordStr +  "&" + apikey;
	}
}
