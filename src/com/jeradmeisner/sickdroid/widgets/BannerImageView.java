package com.jeradmeisner.sickdroid.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;

public class BannerImageView extends ImageView {

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
        new SetBannerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvdbid);
    }

    private class SetBannerTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BannerCacheManager.getInstance(getContext()).get(params[0], BannerCacheManager.BitmapType.BANNER);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            setImageBitmap(bitmap);
        }
    }
}
