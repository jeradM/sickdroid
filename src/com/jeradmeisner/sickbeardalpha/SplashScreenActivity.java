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

import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.jeradmeisner.sickbeardalpha.utils.*;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplashScreenActivity extends SherlockActivity { //implements SharedPreferences.OnSharedPreferenceChangeListener{

    private String host;
    private String port;
    private String webroot;
    private String apiKey;
    private String protocol;
    SharedPreferences prefs;

    private String apiUrl;

    BannerCacheManager cacheManager;

    private static final String TAG = "SplashScreen";
    private static final int REQUEST_CODE_PROFILES = 1;

    //String apiurl = "http://192.168.1.151:8081/sickbeard/api/1871f40ea3a3f1b55182d6033ae7062a/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        cacheManager = BannerCacheManager.getInstance(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().remove(SickbeardProfiles.PREFS_CURRENT_PROFILE).commit();


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

        apiUrl = String.format("%s://%s:%s/%sapi/%s/", protocol, host, port, webroot, apiKey);

        new BuildShowsListTask().execute(apiUrl);
    }

    public void launchMainActivity(ArrayList<Show> shows)
    {
        Intent i = new Intent(this, ShowsActivity.class);
        i.putParcelableArrayListExtra("showlist", shows);
        i.putExtra("apiUrl", apiUrl);
        startActivity(i);
        finish();

    }

    private class BuildShowsListTask extends AsyncTask<String, Void, Shows> {


        @Override
        protected Shows doInBackground(String... urls) {

            //cacheManager.clearCache();
            JSONObject mainJson = SickbeardJsonUtils.getJsonFromUrl(urls[0], ApiCommands.SHOWS.toString());
            JSONObject dataJson = SickbeardJsonUtils.parseObjectFromJson(mainJson, "data");

            if (dataJson == null) {
                Intent i = new Intent(SplashScreenActivity.this, ProfilesActivity.class);
                i.putExtra("failed", true);
                startActivityForResult(i, REQUEST_CODE_PROFILES);
                return null;
            }

            Shows shows = new Shows(dataJson);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int maxWidth = size.x;
            int maxHeight = (int)(size.y / 16);

            for (Show show : shows.getShowList()) {
                fetchBanner(show, urls[0], maxWidth);
                fetchPoster(show, urls[0], maxHeight);
                fetchFanart(show, maxWidth);
                fetchSeriesOverview(show);
            }

            return shows;
        }

        @Override
        protected void onPostExecute(Shows shows) {
            if (shows == null) {
                Log.e(TAG, "Null show list");
            }
            else {
                ArrayList<Show> showList = (ArrayList<Show>)shows.getShowList();
                Collections.sort(showList, new ShowComparator());
                launchMainActivity(showList);
                super.onPostExecute(shows);
            }
        }
    }

    private void fetchBanner(Show show, String urlstring, int maxWidth)
    {
        try {
            if (!cacheManager.contains(show.getTvdbid(), BannerCacheManager.BitmapType.BANNER)) {
                Bitmap bitmap = ArtworkDownloader.fetchBanner(urlstring, show, maxWidth);
                cacheManager.addBitmap(show.getTvdbid(), bitmap, BannerCacheManager.BitmapType.BANNER);
                Log.i(TAG, "Downloaded banner for " + show.getTitle());
            }
        }
        catch (IOException e) {
            Log.e(TAG, "Error fetching banner");

        }
    }

    private void fetchPoster(Show show, String urlstring, int maxHeight)
    {
        try {
            if (!cacheManager.contains(show.getTvdbid(), BannerCacheManager.BitmapType.POSTER)) {
                Bitmap bitmap = ArtworkDownloader.fetchPoster(urlstring, show, maxHeight);
                cacheManager.addBitmap(show.getTvdbid(), bitmap, BannerCacheManager.BitmapType.POSTER);
                Log.i(TAG, "Downloaded banner for " + show.getTitle());
            }
        }
        catch (IOException e) {
            Log.e(TAG, "Error fetching poster");

        }
    }

    private void fetchFanart(Show show, int maxWidth)
    {
        try {
            if (!cacheManager.contains(show.getTvdbid(), BannerCacheManager.BitmapType.FANART)) {
                Bitmap bitmap = ArtworkDownloader.fetchFanart(show.getTvdbid(), maxWidth);
                cacheManager.addBitmap(show.getTvdbid(), bitmap, BannerCacheManager.BitmapType.FANART);
            }
        }
        catch (IOException e) {
            Log.e(TAG, "Error fetching fanart");
        }

    }

    private void fetchSeriesOverview(Show show)
    {
        show.setOverview(TVDBApi.getSeriesOverview(show));
    }

    private InputStream getInputStream(URL url)  throws IOException
    {
        HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
        urlConn.setDoInput(true);
        urlConn.connect();
        InputStream is = urlConn.getInputStream();
        return is;
    }


}
