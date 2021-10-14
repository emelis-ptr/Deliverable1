package utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonUtils {

    private JsonUtils() {
    }

    /**
     * @param rd:
     * @return :
     * @throws IOException:
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
     * @param url:
     * @return :
     * @throws IOException:
     * @throws JSONException:
     */
    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        try (InputStream inputStream = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        }
    }

    /**
     * @param url:
     * @return :
     */
    public static JSONObject readJsonFromUrl(String url) {
        String jsonText = "";

        try (InputStream inputStream = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            jsonText = readAll(rd);
         } catch (IOException e) {  LogFile.errorLog("Errore nella lettura json da url!"); }

        return new JSONObject(jsonText);
    }

}
