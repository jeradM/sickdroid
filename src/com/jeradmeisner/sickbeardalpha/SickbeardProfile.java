package com.jeradmeisner.sickbeardalpha;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SickbeardProfile {

    public static final String PREFS_HOST = "host";
    public static final String PREFS_PORT = "port";
    public static final String PREFS_WEBROOT = "webroot";
    public static final String PREFS_APIKEY = "apikey";
    public static final String PREFS_USEHTTPS = "https";

    private String host;
    private String port;
    private String webroot;
    private String apikey;
    private boolean useHttps;

    public SickbeardProfile(String host, String port, String webroot, String apikey, boolean useHttps)
    {
        this.host = host;
        this.port = port;
        this.webroot = webroot;
        this.apikey = apikey;
        this.useHttps = useHttps;
    }

    public SickbeardProfile(SharedPreferences prefs)
    {
        host = prefs.getString(PREFS_HOST, "127.0.0.1");
        port = prefs.getString(PREFS_PORT, "8080");
        webroot = prefs.getString(PREFS_WEBROOT, "");
        apikey = prefs.getString(PREFS_APIKEY, "12345");
        useHttps = prefs.getBoolean(PREFS_USEHTTPS, false);
    }

    public void loadProfile(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_HOST, host);
        editor.putString(PREFS_PORT, port);
        editor.putString(PREFS_WEBROOT, webroot);
        editor.putString(PREFS_APIKEY, apikey);
        editor.putBoolean(PREFS_USEHTTPS, useHttps);
        editor.commit();
    }
}
