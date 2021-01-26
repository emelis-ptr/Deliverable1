package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

	 private JsonUtils() {
		    throw new IllegalStateException("Utility class");
		  }
	 
	/**
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	public static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	   }

   /**
 * @param url
 * @return
 * @throws IOException
 * @throws JSONException
 */
public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
   try (InputStream inputStream = new URL(url).openStream()){
	     BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
       String jsonText = readAll(rd);
       return new JSONArray(jsonText);
     } 
   }

   /**
 * @param url
 * @return
 * @throws IOException
 * @throws JSONException
 */
public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    try (InputStream inputStream = new URL(url).openStream()){
       BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
       String jsonText = readAll(rd);
       return new JSONObject(jsonText);
     } 
   }
}
