package com.jeradmeisner.sickdroid.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.jeradmeisner.sickdroid.R;
import com.jeradmeisner.sickdroid.utils.ArtworkDownloader;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;

import java.io.IOException;

public class PosterImageView extends ImageView {

    private Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.banner);
    private BannerCacheManager bcm = BannerCacheManager.getInstance(getContext());

    public PosterImageView(Context context) {
        super(context);
    }

    public PosterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PosterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPosterImage(String tvdbid) {
        Bitmap b = bcm.getFromMemory(tvdbid, BannerCacheManager.BitmapType.POSTER);
        if (b != null)
            setImageBitmap(b);
        else {
            setImageBitmap(defaultImage);
            new SetPosterTask().execute(tvdbid);
        }
    }

    private class SetPosterTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap b =  bcm.getFromDisk(params[0], BannerCacheManager.BitmapType.POSTER);
            if (b == null) {
                String apiurl = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("apiurl", null);
                int width = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("max_height", 240);
                if (apiurl != null) {
                    try {
                        b = ArtworkDownloader.fetchPoster(apiurl, params[0], width);
                        bcm.addBitmap(params[0], b, BannerCacheManager.BitmapType.POSTER);
                    } catch (IOException e) {
                        b = null;
                    }
                }
            }

            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null)
                setImageBitmap(bitmap);
        }
    }
}

