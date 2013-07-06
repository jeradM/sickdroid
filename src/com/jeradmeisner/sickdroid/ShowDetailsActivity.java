package com.jeradmeisner.sickdroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.jeradmeisner.sickdroid.data.Show;
import com.jeradmeisner.sickdroid.utils.ArtworkDownloader;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;
import com.jeradmeisner.sickdroid.widgets.ObservableScrollView;

import java.io.IOException;


public class ShowDetailsActivity extends SherlockActivity implements ObservableScrollView.ScrollListener {

    private ObservableScrollView mScrollView;
    private TextView seriesOverview;
    private ImageView fanart;
    private ImageView header;
    private ImageView poster;
    private Drawable actionBarBackground;
    private BitmapDrawable imageDrawable;
    boolean isExpanded = false;

    private Show show;

    BannerCacheManager bcm = BannerCacheManager.getInstance(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_show_details);

        Intent i = getIntent();
        show = i.getParcelableExtra("show");
        fanart = (ImageView)findViewById(R.id.fanart_image);
        new SetFanartTask().execute(show.getTvdbid());

        //seriesOverview = (TextView)findViewById(R.id.series_overview);
        //seriesOverview.setText(show.getOverview());

        header = (ImageView)findViewById(R.id.transparent_header);
        poster = (ImageView)findViewById(R.id.show_poster);

        mScrollView = (ObservableScrollView)findViewById(R.id.scroll_view);
        mScrollView.setScrollListener(this);

        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged(null, 0, 0, 0, 0);
                    }

                });

        actionBarBackground = getResources().getDrawable(R.drawable.ab_bg);
        getSupportActionBar().setBackgroundDrawable(actionBarBackground);
        getSupportActionBar().setTitle(show.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        /*LinearLayout lin = (LinearLayout)findViewById(R.id.seasons_lists_layout);
        TextView tv = new TextView(this);
        tv.setText("This is also a test");
        lin.addView(tv);*/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onScrollChanged(View who, int l, int t, int oldl, int oldt)
    {
        if (poster != null) {
            poster.setTranslationY(Math.min(0, header.getTop() - (mScrollView.getScrollY() / 1.1f)));
        }
        fanart.setTranslationY(Math.min(0, header.getTop() - (mScrollView.getScrollY() / 5)));
        final int headerHeight = findViewById(R.id.fanart_image).getHeight() - getSupportActionBar().getHeight();
        final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
        final int newAlpha = (int) (ratio * 255);
        actionBarBackground.setAlpha(newAlpha);
    }

    private class SetFanartTask extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap b = bcm.get(params[0], BannerCacheManager.BitmapType.FANART);
            if (b == null) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int maxWidth = size.x;
                try {
                    b = ArtworkDownloader.fetchFanart(params[0], maxWidth);
                    bcm.addBitmap(params[0], b, BannerCacheManager.BitmapType.FANART);
                } catch (IOException e) {
                    Log.e("ShowDetails", "Unable to fetch fanart");
                }
            }
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageDrawable = new BitmapDrawable(getResources(), bitmap);
            fanart.setImageBitmap(bitmap);
            header.setImageBitmap(bitmap);
            poster.setImageBitmap(bcm.get(show.getTvdbid(), BannerCacheManager.BitmapType.POSTER));
        }
    }
}