package com.jeradmeisner.sickdroid;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.jeradmeisner.sickdroid.data.Show;
import com.jeradmeisner.sickdroid.utils.ArtworkDownloader;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/24/13
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageCacheService extends Service {

    public static final String IMAGES_UPDATES = "Updated";
    public static final String UPDATE_TYPE = "Type";
    public static final int BANNERS_UPDATED = 1;
    public static final int POSTERS_UPDATED = 2;
    public static final int FANART_UPDATED = 2;

    private static final String TAG = "ImageCacheService";

    private List<Show> showList;
    private String apiurl;
    BannerCacheManager bcm;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showList = intent.getParcelableArrayListExtra("showlist");
        apiurl = intent.getStringExtra("apiurl");
        int width = intent.getIntExtra("maxWidth", -1);
        int height = intent.getIntExtra("maxHeight", -1);

        if (width == -1 || height == -1) {
            Log.e(TAG, "Unable to fetch images. Cannot determine screen size");
            stopSelf();
        }

        bcm = BannerCacheManager.getInstance(this);

        new FetchBannersTask().execute(width, height);


        return START_REDELIVER_INTENT;

    }

    private class FetchBannersTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            int maxWidth = params[0];
            int maxHeight = params[1];

            Intent bi = new Intent(IMAGES_UPDATES);
            bi.putExtra(UPDATE_TYPE, BANNERS_UPDATED);
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(ImageCacheService.this);

            for (Show show : showList) {
                fetchBanner(show, apiurl, maxWidth);
                fetchPoster(show, apiurl, maxHeight);
                sendBroadcast(bi);
            }

            return maxHeight;
        }

        @Override
        protected void onPostExecute(Integer i) {
            //new FetchPostersTask().execute(i);
        }
    }

    private class FetchPostersTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            int maxHeight = params[0];

            Intent bi = new Intent(IMAGES_UPDATES);
            bi.putExtra(UPDATE_TYPE, POSTERS_UPDATED);
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(ImageCacheService.this);

            for (Show show : showList) {
                fetchPoster(show, apiurl, maxHeight);
                sendBroadcast(bi);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            stopSelf();
        }
    }




    private void fetchBanner(Show show, String urlstring, int maxWidth)
    {
        try {
            if (!bcm.contains(show.getTvdbid(), BannerCacheManager.BitmapType.BANNER)) {
                Bitmap bitmap = ArtworkDownloader.fetchBanner(urlstring, show, maxWidth);
                bcm.addBitmap(show.getTvdbid(), bitmap, BannerCacheManager.BitmapType.BANNER);
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
            if (!bcm.contains(show.getTvdbid(), BannerCacheManager.BitmapType.POSTER)) {
                Bitmap bitmap = ArtworkDownloader.fetchPoster(urlstring, show, maxHeight);
                bcm.addBitmap(show.getTvdbid(), bitmap, BannerCacheManager.BitmapType.POSTER);
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
            if (!bcm.contains(show.getTvdbid(), BannerCacheManager.BitmapType.FANART)) {
                Bitmap bitmap = ArtworkDownloader.fetchFanart(show.getTvdbid(), maxWidth);
                bcm.addBitmap(show.getTvdbid(), bitmap, BannerCacheManager.BitmapType.FANART);
            }
        }
        catch (IOException e) {
            Log.e(TAG, "Error fetching fanart");
        }

    }
}
