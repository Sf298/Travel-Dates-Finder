/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googlelocationparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author demon
 */
public class Viewer {
    
    private static File locationFile = new File("C:\\Users\\demon\\Desktop/Location History.json");
    
    public static void main(String[] args) {
        if(locationFile == null) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setFileFilter(new FileNameExtensionFilter("JSON", "json"));
            jfc.showOpenDialog(null);
            locationFile = jfc.getSelectedFile();
        }
        
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(locationFile));

            JSONArray locations = (JSONArray) jsonObject.get("locations");
            ArrayList<String[]> flights = getFlights(locations);
            for (String[] flight : flights) {
                System.out.println(flight[0] + ": " + flight[1] +" ----> "+flight[2] + " "+flight[3]);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static ArrayList<String[]> getFlights(JSONArray arr) {
        // get first ts of day 
        // get last timestamp of day
        // compare locations
        ArrayList<String[]> out = new ArrayList<>();
        for(int i=0; i<arr.size(); i++) {
            int[] pair = getDayPair(arr, i);
            i = pair[1];
            
            double dist = distance(
                    getLat(arr, pair[1]),
                    getLat(arr, pair[0]),
                    getLon(arr, pair[1]),
                    getLon(arr, pair[0])
            );
            if(dist > 200000) {
                out.add(pairToCountry(arr, pair));
            }
        }
        return out;
    }
    
    /**
     * first timestamp of the day
     * @param iter
     * @return 
     */
    private static int[] getDayPair(JSONArray arr, int dayFirstIndex) {
        int[] out = new int[2];
        out[0] = dayFirstIndex;
        
        int dayDate = getCal((JSONObject) arr.get(out[0])).get(Calendar.DAY_OF_MONTH);
        int i = dayFirstIndex;
        for(; i < arr.size(); i++) {
            JSONObject ob;
            if(i+1 >= arr.size()) {
                ob = (JSONObject) arr.get(arr.size()-1);
            } else {
                ob = (JSONObject) arr.get(i+1);
            }
            int testDate = getCal(ob).get(Calendar.DAY_OF_MONTH);
            if(testDate != dayDate) {
                out[1] = i;
                return out;
            }
        }
        out[1] = i-1;
        return out;
    }
    
    private static Calendar getCal(JSONArray arr, int index) {
        return getCal((JSONObject) arr.get(index));
    }
    private static Calendar getCal(JSONObject ob) {
        String time = (String) ob.get("timestampMs");
        long timestampLong = Long.parseLong(time);
        Date d = new Date(timestampLong);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }
    
    private static String pairToStr(JSONArray arr, int[] pair) {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String print = df.format(getCal((JSONObject) arr.get(pair[0])).getTime())
                    + " [" + getLat(arr, pair[1]) + ", " + getLon(arr, pair[1])
                    + "] [" + getLat(arr, pair[0]) + ", " + getLon(arr, pair[0])
                    + "] = " + distance(
                            getLat(arr, pair[1]),
                            getLat(arr, pair[0]),
                            getLon(arr, pair[1]),
                            getLon(arr, pair[0])
                    );
        return print;
    }
    
    private static String[] pairToCountry(JSONArray arr, int[] pair) {
        String[] out = new String[4];
        
        double lat0 = getLat(arr, pair[0]);
        double lon0 = getLon(arr, pair[0]);
        double lat1 = getLat(arr, pair[1]);
        double lon1 = getLon(arr, pair[1]);
        
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        out[0] = df.format(getCal(arr, pair[0]).getTime());
        out[2] = coordToCountry(lat0, lon0);
        out[1] = coordToCountry(lat1, lon1);
        out[3] = distance(lat0, lat1, lon0, lon1) + "";
        
        return out;
    }
    private static final String API_KEY = "AIzaSyDkFEAXxwuN8y-VoYhnX_SxeJPrsTh3GP0";
    private static String coordToCountry(double lat, double lon) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s";
        // https://maps.googleapis.com/maps/api/geocode/json?latlng=34.3050044,35.7072063&key=AIzaSyB8y8AbE568hM2CFNPDtn-Js3xXL8QkVTM
        url = String.format(url, lat, lon, API_KEY);
        try {
            URL oracle = new URL(url);
            JSONParser p = new JSONParser();
            JSONObject ob = (JSONObject) p.parse(new InputStreamReader(oracle.openStream()));
            
            JSONArray results = (JSONArray) ob.get("results");
            JSONObject results0 = (JSONObject) results.get(0);
            JSONArray addressComp = (JSONArray) results0.get("address_components");
            JSONObject addressComp3 = (JSONObject) addressComp.get(3);
            String country = (String) addressComp3.get("long_name");
            return country;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static double getLat(JSONArray arr, int index) {
        return getLat((JSONObject) arr.get(index));
    } 
    private static double getLat(JSONObject ob) {
        long l = (long) ob.get("latitudeE7");
        double d = l / 10000000.0;
        return d;
    }
    
    private static double getLon(JSONArray arr, int index) {
        return getLon((JSONObject) arr.get(index));
    } 
    private static double getLon(JSONObject ob) {
        long l = (long) ob.get("longitudeE7");
        double d = l / 10000000.0;
        return d;
    }
    
    public static double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }
}
