package com.jeradmeisner.sickdroid;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.jeradmeisner.sickdroid.utils.ArtworkDownloader;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;

import java.io.IOException;


public class GetNewShowBannerService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String apiurl = intent.getStringExtra("apiurl");
        final String tvdbid = intent.getStringExtra("tvdbid");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                BannerCacheManager bcm = BannerCacheManager.getInstance(getApplicationContext());
                int width = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("max_width", 1080);
                int height = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("max_height", 240);

                try {
                    Thread.sleep(10000);
                    Bitmap banner = ArtworkDownloader.fetchBanner(apiurl, tvdbid, width);
                    Bitmap poster = ArtworkDownloader.fetchPoster(apiurl, tvdbid, height);
                    bcm.addBitmap(tvdbid, banner, BannerCacheManager.BitmapType.BANNER);
                    bcm.addBitmap(tvdbid, poster, BannerCacheManager.BitmapType.POSTER);
                } catch (Exception e) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                stopSelf();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return Service.START_NOT_STICKY;
    }

}
