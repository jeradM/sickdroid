package com.jeradmeisner.sickbeardalpha.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.jeradmeisner.sickbeardalpha.ProfilesActivity;
import com.jeradmeisner.sickbeardalpha.Shows;
import com.jeradmeisner.sickbeardalpha.SickbeardProfiles;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;


public class ShowLoaderService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private IBinder mBinder = new SickBinder();

    public static final int UPDATE_SHOWS = 1;
    public static final int UPDATE_BANNERS = 2;
    public static final int UPDATE_POSTERS = 3;
    public static final int UPDATE_FANART = 4;

    BannerCacheManager bcm = BannerCacheManager.getInstance(this);

    private String apiurl;
    private SharedPreferences prefs;


    public ShowLoaderListener mListener;

    public interface ShowLoaderListener {
        public void onServiceUpdate(int flag);
    }

    public void setShowLoaderListener(ShowLoaderListener listener)
    {
        this.mListener = listener;
    }

    private void updateListener(int flag)
    {
        mListener.onServiceUpdate(flag);
    }

    public class SickBinder extends Binder {
        public ShowLoaderService getService() {
            return ShowLoaderService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new SickBinder();
        }
        return mBinder;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        getPrefs();
    }

    // ---**--- Start Service Lifecycle ---**--- \\
    @Override
    public void onCreate() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        String currentProfile = prefs.getString(SickbeardProfiles.PREFS_CURRENT_PROFILE, "NONE");

        if (currentProfile.equals("NONE")) {
            Intent i = new Intent(this, ProfilesActivity.class);
            startActivity(i);
        }
        else {
            getPrefs();
        }
    }

    public void getPrefs()
    {
        String protocol;
        String host = prefs.getString(SickbeardProfiles.PREFS_HOST, "localhost");
        String port = prefs.getString(SickbeardProfiles.PREFS_PORT, "8080");
        String webroot = prefs.getString(SickbeardProfiles.PREFS_WEBROOT, "");
        String apiKey = prefs.getString(SickbeardProfiles.PREFS_APIKEY, "12345");
        Boolean useHttps = prefs.getBoolean(SickbeardProfiles.PREFS_USEHTTPS, false);

        if (useHttps)
            protocol = "https";
        else
            protocol = "http";

        if (!webroot.equals(""))
            webroot += "/";

        apiurl = String.format("%s://%s:%s/%sapi/%s/", protocol, host, port, webroot, apiKey);
    }
}
