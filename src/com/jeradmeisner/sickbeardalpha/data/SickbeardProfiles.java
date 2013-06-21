package com.jeradmeisner.sickbeardalpha.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/12/13
 * Time: 5:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SickbeardProfiles {

    private static SickbeardProfiles instance;

    public static final String PREFS_HOST = "host";
    public static final String PREFS_PORT = "port";
    public static final String PREFS_WEBROOT = "webroot";
    public static final String PREFS_APIKEY = "apikey";
    public static final String PREFS_USEHTTPS = "https";

    public static final String PREFS_CURRENT_PROFILE = "current_profile";
    public static final String PREFS_PROFILES = "profiles";

    private List<SickbeardProfile> profiles;

    public static SickbeardProfiles getInstance()
    {
        if (instance == null)
            instance = new SickbeardProfiles();

        return instance;
    }

    private SickbeardProfiles()
    {
    }

    public void loadProfiles(Context ctx)
    {
        ctx = ctx.getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String profileString = prefs.getString(PREFS_PROFILES, null);
        profiles = new ArrayList<SickbeardProfile>();

        if (profileString != null) {
            String[] profileArray = profileString.split(":");

            for (String name : profileArray) {
                SharedPreferences pref = ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
                SickbeardProfile profile = new SickbeardProfile(ctx, name, pref);
                profiles.add(profile);
            }
        }
    }

    public void writeProfiles(Context ctx)
    {
        ctx = ctx.getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        StringBuilder sb = new StringBuilder();
        for (SickbeardProfile profile : profiles) {
            sb.append(profile.getName() + ":");
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_PROFILES, sb.toString());
        editor.commit();
    }

    public void addProfile(Context ctx, String name, String host, String port,
                           String webroot, String apikey, boolean https)
    {
        SickbeardProfile profile = new SickbeardProfile(ctx, name, host, port, webroot, apikey, https);
        profiles.add(profile);
        writeProfiles(ctx);
    }

    public List<SickbeardProfile> getProfiles()
    {
        return profiles;
    }

    public SickbeardProfile findProfile(String name)
    {
        for (SickbeardProfile profile : profiles) {
            if (name.equals(profile.getName())) {
                return profile;
            }
        }

        return null;
    }


}
