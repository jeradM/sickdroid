package com.jeradmeisner.sickdroid.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.jeradmeisner.sickdroid.R;
import com.jeradmeisner.sickdroid.utils.ArtworkDownloader;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;

import java.io.IOException;

public class BannerImageView extends ImageView {

    private Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.banner);
    private BannerCacheManager bcm = BannerCacheManager.getInstance(getContext());

    public BannerImageView(Context context) {
        super(context);
    }

    public BannerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable pic = this.getDrawable();
        int height = pic.getIntrinsicHeight();
        int width = pic.getIntrinsicWidth();
        double aspect = (double)(width) / height;

        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)(Math.ceil( (double)(View.MeasureSpec.getSize(widthMeasureSpec)) / aspect )), View.MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setBannerImage(String tvdbid) {
        Bitmap b = bcm.getFromMemory(tvdbid, BannerCacheManager.BitmapType.BANNER);
        if (b != null)
            setImageBitmap(b);
        else {
            setImageBitmap(defaultImage);
            new SetBannerTask().execute(tvdbid);
        }
    }

    private class SetBannerTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap b =  bcm.getFromDisk(params[0], BannerCacheManager.BitmapType.BANNER);
            if (b == null) {
                String apiurl = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("apiurl", null);
                int width = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("max_width", 1080);
                if (apiurl != null) {
                    try {
                        b = ArtworkDownloader.fetchBanner(apiurl, params[0], width);
                        bcm.addBitmap(params[0], b, BannerCacheManager.BitmapType.BANNER);
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
