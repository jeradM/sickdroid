package com.jeradmeisner.sickbeardalpha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class StartActivity extends SherlockActivity {

    private static final String TAG = "StartActivity";
    String apiurl = "http://192.168.1.151:8081/sickbeard/api/1871f40ea3a3f1b55182d6033ae7062a/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Drawable abBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar_background_light_green);
        getSupportActionBar().setBackgroundDrawable(abBackgroundDrawable);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start);


        new BuildShowsListTask().execute(new Object());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
            BannerCacheManager cacheManager = BannerCacheManager.getInstance(StartActivity.this);
            cacheManager.clearCache();
            JSONObject mainJson = SickbeardJsonUtils.getJsonFromUrl(apiurl, ApiCommands.SHOWS);
            JSONObject dataJson = SickbeardJsonUtils.parseObjectFromJson(mainJson, "data");

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
