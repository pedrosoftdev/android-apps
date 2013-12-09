package br.com.phsd.migalhatracker.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class Utils {
	
	public static String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

	public static String formatDateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		return sdf.format(date);
	}
	
	public static Date formatStringToDateTime(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		try {
			return sdf.parse(date);
		} catch (Exception e) {
			Log.e("Parse Date", "Erro ao converter texto para data", e);
			return null;
		}
	}
	
	public static double calcularDistancia(LatLng latLngA, LatLng latLngB) {
	    
		double lat_a = latLngA.latitude;
		double lng_a = latLngA.longitude;
		double lat_b = latLngB.latitude;
		double lng_b = latLngB.longitude;
		
		double earthRadius = 3958.75;
		
	    double latDiff = Math.toRadians(lat_b-lat_a);
	    double lngDiff = Math.toRadians(lng_b-lng_a);
	    
	    double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
	    Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
	    Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
	    
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    
	    double distance = earthRadius * c;

	    int meterConversion = 1609;

	    return Math.abs(distance * meterConversion);
	    
	}
	
}
