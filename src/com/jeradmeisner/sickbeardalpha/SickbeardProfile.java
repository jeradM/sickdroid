package com.jeradmeisner.sickbeardalpha;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SickbeardProfile {

    private String name;
    private String host;
    private String port;
    private String webroot;
    private String apikey;
    private boolean useHttps;

    private Context ctx;

    public SickbeardProfile(Context ctx, String name, String host, String port, String webroot, String apikey, boolean useHttps)
    {
        this.name = name;
        this.host = host;
        this.port = port;
        this.webroot = webroot;
        this.apikey = apikey;
        this.useHttps = useHttps;

        this.ctx = ctx.getApplicationContext();

        savePrefs();
    }

    public SickbeardProfile(Context ctx, String name, SharedPreferences prefs)
    {
        this.name = name;
        host = prefs.getString(SickbeardProfiles.PREFS_HOST, "localhost");
        port = prefs.getString(SickbeardProfiles.PREFS_PORT, "8080");
        webroot = prefs.getString(SickbeardProfiles.PREFS_WEBROOT, "");
        apikey = prefs.getString(SickbeardProfiles.PREFS_APIKEY, "12345");
        useHttps = prefs.getBoolean(SickbeardProfiles.PREFS_USEHTTPS, false);

        this.ctx = ctx;
    }

    public void setProfile()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SickbeardProfiles.PREFS_HOST, host);
        editor.putString(SickbeardProfiles.PREFS_PORT, port);
        editor.putString(SickbeardProfiles.PREFS_WEBROOT, webroot);
        editor.putString(SickbeardProfiles.PREFS_APIKEY, apikey);
        editor.putBoolean(SickbeardProfiles.PREFS_USEHTTPS, useHttps);
        editor.putString(SickbeardProfiles.PREFS_CURRENT_PROFILE, name);
        editor.commit();
    }

    public void savePrefs()
    {
        SharedPreferences prefs =  ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SickbeardProfiles.PREFS_HOST, host);
        editor.putString(SickbeardProfiles.PREFS_PORT, port);
        editor.putString(SickbeardProfiles.PREFS_WEBROOT, webroot);
        editor.putString(SickbeardProfiles.PREFS_APIKEY, apikey);
        editor.putBoolean(SickbeardProfiles.PREFS_USEHTTPS, useHttps);
        editor.commit();
    }

    public void deletePrefs()
    {
        ctx.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return name;
    }
}
