package com.jeradmeisner.sickbeardalpha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;
import com.jeradmeisner.sickbeardalpha.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SplashScreenActivity extends SherlockActivity {

    private String host;
    private String port;
    private String webroot;
    private String apiKey;
    private String protocol;
    SharedPreferences prefs;

    private static final String TAG = "SplashScreen";
    private static final int REQUEST_CODE_PROFILES = 1;

    String apiurl = "http://192.168.1.151:8081/sickbeard/api/1871f40ea3a3f1b55182d6033ae7062a/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().clear().commit();


        String currentProfile = prefs.getString(SickbeardProfiles.PREFS_CURRENT_PROFILE, "NONE");

        if (currentProfile.equals("NONE")) {
            Intent i = new Intent(this, ProfilesActivity.class);
            startActivityForResult(i, REQUEST_CODE_PROFILES);
        }
        else {
            setupPrefsAndFetchShows();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PROFILES) {
            if (resultCode == RESULT_OK) {
                setupPrefsAndFetchShows();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void setupPrefsAndFetchShows()
    {
        host = prefs.getString(SickbeardProfiles.PREFS_HOST, "localhost");
        port = prefs.getString(SickbeardProfiles.PREFS_PORT, "8080");
        webroot = prefs.getString(SickbeardProfiles.PREFS_WEBROOT, "");
        apiKey = prefs.getString(SickbeardProfiles.PREFS_APIKEY, "12345");
        Boolean useHttps = prefs.getBoolean(SickbeardProfiles.PREFS_USEHTTPS, false);

        if (useHttps)
            protocol = "https";
        else
            protocol = "http";

        if (!webroot.equals(""))
            webroot += "/";

        String url = String.format("%s://%s:%s/%sapi/%s/", protocol, host, port, webroot, apiKey);

        new BuildShowsListTask().execute(new Object());
    }

    public void launchMainActivity(ArrayList<Show> shows)
    {
        Intent i = new Intent(this, ShowsActivity.class);
        i.putParcelableArrayListExtra("showlist", shows);
        startActivity(i);
    }

    private class BuildShowsListTask extends AsyncTask<Object, Void, Shows> {


        @Override
        protected Shows doInBackground(Object... objects) {
            BannerCacheManager cacheManager = BannerCacheManager.getInstance(SplashScreenActivity.this);
            cacheManager.clearCache();
            JSONObject mainJson = SickbeardJsonUtils.getJsonFromUrl(apiurl, ApiCommands.SHOWS);
            JSONObject dataJson = SickbeardJsonUtils.parseObjectFromJson(mainJson, "data");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            if (dataJson == null)
                return null;

            Shows shows = new Shows(dataJson);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int maxWidth = size.x;

            try {
                for (Show show : shows.getShowList()) {
                    if (!cacheManager.contains(show.getTvdbid(), BannerCacheManager.BitmapType.BANNER)) {
                        URL url = new URL(apiurl + "?cmd=show.getbanner&tvdbid=" + show.getTvdbid());
                        HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
                        urlConn.setDoInput(true);
                        urlConn.connect();
                        InputStream is = urlConn.getInputStream();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(is, null, options);
                        int width = options.outWidth;
                        final int widthRatio = Math.round((float) width / (float) maxWidth);
                        options.inSampleSize = widthRatio;
                        options.inJustDecodeBounds = false;
                        urlConn = (HttpURLConnection)url.openConnection();
                        urlConn.setDoInput(true);
                        urlConn.connect();
                        is = urlConn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                        cacheManager.addBitmap(show.getTvdbid(), bitmap, BannerCacheManager.BitmapType.BANNER);
                        Log.i(TAG, "Downloaded banner for " + show.getTitle());
                    }
                }
            }
            catch (IOException e) {
                Log.e(TAG, "Error fetching banner");

            }

            return shows;
        }

        @Override
        protected void onPostExecute(Shows shows) {
            if (shows.getShowList() == null) {
                Log.e(TAG, "Null show list");
            }
            launchMainActivity((ArrayList<Show>)shows.getShowList());
            super.onPostExecute(shows);
        }
    }


}
