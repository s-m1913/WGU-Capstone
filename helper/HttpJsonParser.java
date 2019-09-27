package com.example.thegreatmugwump.taskmanager.helper;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpJsonParser {

    static InputStream inputStream = null;
    static JSONObject jObj = null;
    static String json = "";
    HttpURLConnection urlConnection = null;
    private String url = "http://174.31.239.5/";

    // function get json from url
    public JSONObject makeHttpRequest(String subURL, String query) {

        try {
            Uri.Builder builder = new Uri.Builder();
            URL urlObj;
            String encodedParams = "";
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put("query", query);

            for (Map.Entry<String, String> entry : httpParams.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

            encodedParams =  builder.build().getEncodedQuery();

            urlObj = new URL(url+subURL);
            urlConnection = (HttpURLConnection) urlObj.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(encodedParams.getBytes().length));
            urlConnection.getOutputStream().write(encodedParams.getBytes());
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            inputStream.close();

            json = stringBuilder.toString();
            jObj = new JSONObject(json);
            Log.e("Query>>>>>>>",query);
            Log.e("Result>>>>>>",json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return JSON String
        return jObj;

    }
}
