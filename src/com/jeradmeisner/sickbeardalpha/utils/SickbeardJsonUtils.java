package com.jeradmeisner.sickbeardalpha.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;

import android.util.Log;

public class SickbeardJsonUtils {

    private static final String LOG_NAME = "SickbeardJsonUtils";

    /**
     * Retrieves a JSONObject from the specified URL and command
     *
     * @param  hostUrl  the fully qualified url to the host including port,
     * 					webroot and API key ( "http://hostname:port/webroot/api/apikey/" )
     * @param  cmd      the API command to execute
     */
    public static JSONObject getJsonFromUrl(String hostUrl, String cmd)
    {
        JSONObject json;
        StringBuilder sb = new StringBuilder();
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 2000);
        HttpConnectionParams.setSoTimeout(params, 2000);
        HttpClient client = new DefaultHttpClient(params);

        String showUrl = hostUrl + "?cmd=" + cmd;
        HttpGet get = new HttpGet(showUrl);

        try {
            HttpResponse response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                json = new JSONObject(sb.toString());
                String stat = json.getString("result");
                if (!stat.equals("success")) {
                    throw new IOException("Incorrect API key");
                }
                return json;
            }
            else {
                Log.e(LOG_NAME, "Unable to contact sickbeard host");
                return null;
            }
        }
        catch (ClientProtocolException e) {
            Log.e(LOG_NAME, "Client Protocol Error");
            return null;
        }
        catch (IOException e) {
            Log.e(LOG_NAME, "IO Error: " + e.getMessage());
            return null;
        }
        catch (JSONException e) {
            Log.e(LOG_NAME, "JSON Error");
            return null;
        }
    }

    /**
     * Parses a given json object for given key and returns value as JSONObject
     *
     * @param  json  the object to parse
     * @param  key   the key to search for
     *
     * @return the target value as a JSONObject or null if not found
     */
    public static JSONObject parseObjectFromJson(JSONObject json, String key)
    {
        if (json == null) {
            return null;
        }

        try {
            JSONObject tmp = json.getJSONObject(key);
            return tmp;
        }
        catch (JSONException e) {
            Log.e(LOG_NAME, "Error parsing JSONObject");
            return null;
        }
    }

    /**
     * Parses a given json object for given key and returns value as JSONArray
     *
     * @param  json  the object to parse
     * @param  key   the key to search for
     *
     * @return the target value as a JSONArray or null if not found
     */
    public static JSONArray parseArrayFromJson(JSONObject json, String key)
    {
        try {
            JSONArray array = json.getJSONArray(key);
            return array;
        }
        catch (JSONException e) {
            Log.e(LOG_NAME, "Error parsing JSONObject");
            return null;
        }
    }

    /**
     * Checks to see if the result of a command was successful from returned JSONObject
     */
    public static boolean commandWasSuccessful(JSONObject json)
    {
        try {
            String msg = json.getString("result");
            return "success".equals(msg);
        }
        catch (JSONException e) {
            Log.e(LOG_NAME, "Error parsing JSON data");
            return false;
        }
    }
}