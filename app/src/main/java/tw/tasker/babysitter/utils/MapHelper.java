package tw.tasker.babysitter.utils;


import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.parse.ParseGeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.model.GMapAddress;

public class MapHelper {

	public static String parseResource(Context context, int resource) throws IOException {
		InputStream is = context.getResources().openRawResource(resource);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();
		}

		return writer.toString();
	}

	public static ParseGeoPoint getLocationFromGoogleMap(String address) {
		ParseGeoPoint parseGeoPoint = new ParseGeoPoint(0.0, 0.0);
		try {
			String googleMapServiceUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
					+ address + "&sensor=false&language=zh-tw";
			URL url = new URL(googleMapServiceUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection
							.getInputStream()));

			Gson gson = new Gson();
			GMapAddress gMapAddress = gson.fromJson(reader, GMapAddress.class);

			System.out.println("address=" + address + " ,status=" + gMapAddress.getStatus());

			if (gMapAddress.getStatus().equals("OK")) {

				Double lat = gMapAddress.getResults().get(0).getGeometry().getLocation().getLat();
				Double lng = gMapAddress.getResults().get(0).getGeometry().getLocation().getLng();
				parseGeoPoint.setLatitude(lat);
				parseGeoPoint.setLongitude(lng);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		System.out.println("(lat, lng) = (" + parseGeoPoint.getLatitude() + ", " + parseGeoPoint.getLongitude() + ")");
		return parseGeoPoint;
	}

	@DebugLog
	public static String getStaticMapUrl(String addr) {
		String staticMapUrl = new Uri.Builder()
				.scheme("http")
				.authority("maps.googleapis.com")
				.appendPath("maps")
				.appendPath("api")
				.appendPath("staticmap")
				.appendQueryParameter("language", "tw")
				.appendQueryParameter("autoscale", "2")
				.appendQueryParameter("size", "400x300")
				.appendQueryParameter("zoom", "16")
				.appendQueryParameter("maptype", "roadmap")
				.appendQueryParameter("format", "png")
				.appendQueryParameter("visual_refresh", "true")
				.appendQueryParameter("center", addr)
				.build()
				.toString();

		//String staticMapUrl = "http://maps.googleapis.com/maps/api/staticmap?&scale=2&language=tw&center=%E9%AB%98%E9%9B%84%E5%B8%82%E9%B3%B3%E5%B1%B1%E5%8D%80%E5%85%89%E5%BE%A9%E8%B7%AF&zoom=16&scale=false&size=400x300&maptype=roadmap&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:A%7C%E9%AB%98%E9%9B%84%E5%B8%82%E9%B3%B3%E5%B1%B1%E5%8D%80%E5%85%89%E5%BE%A9%E8%B7%AF";
		return staticMapUrl;
	}


/*
    private String getDistance(Bundle bundle) {

		mSlat = bundle.getString("slat");
		mSlng = bundle.getString("slng");
		mDlat = bundle.getString("dlat");
		mDlng = bundle.getString("dlng");

		mTargetLat = mSlat;
		mTargetLng = mSlng;

		double distance = 0;
		Location locationA = new Location("A");
		locationA.setLatitude(Double.valueOf(mSlat).doubleValue());
		locationA.setLongitude(Double.valueOf(mSlng).doubleValue());
		Location locationB = new Location("B");
		locationB.setLatitude(Double.valueOf(mDlat).doubleValue());
		locationB.setLongitude(Double.valueOf(mDlng).doubleValue());
		distance = locationA.distanceTo(locationB);

		return Double.toString(distance);
	}
*/
}
