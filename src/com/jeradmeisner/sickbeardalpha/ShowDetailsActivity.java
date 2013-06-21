package com.jeradmeisner.sickbeardalpha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.jeradmeisner.sickbeardalpha.data.Show;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;
import com.jeradmeisner.sickbeardalpha.widgets.ObservableScrollView;


public class ShowDetailsActivity extends SherlockActivity implements ObservableScrollView.ScrollListener {

    private ObservableScrollView mScrollView;
    private TextView seriesOverview;
    private ImageView fanart;
    private ImageView header;
    private Drawable actionBarBackground;
    private BitmapDrawable imageDrawable;

    BannerCacheManager bcm = BannerCacheManager.getInstance(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_show_details);

        Intent i = getIntent();
        Show show = i.getParcelableExtra("show");

        seriesOverview = (TextView)findViewById(R.id.series_overview);
        //Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        seriesOverview.setText(show.getOverview());
        //seriesOverview.setTypeface(face);

        fanart = (ImageView)findViewById(R.id.fanart_image);
        new SetFanartTask().execute(show.getTvdbid());

        header = (ImageView)findViewById(R.id.transparent_header);

        mScrollView = (ObservableScrollView)findViewById(R.id.scroll_view);
        mScrollView.setScrollListener(this);

        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged(null, 0, 0, 0, 0);
                    }

                });

        actionBarBackground = getResources().getDrawable(R.drawable.show_details_actionbar);
        getSupportActionBar().setBackgroundDrawable(actionBarBackground);
        getSupportActionBar().setTitle(show.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);


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
        fanart.setTranslationY(Math.min(0, header.getTop() - (mScrollView.getScrollY() / 3)));
        final int headerHeight = findViewById(R.id.fanart_image).getHeight() - getSupportActionBar().getHeight();
        final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
        final int newAlpha = (int) (ratio * 255);
        actionBarBackground.setAlpha(newAlpha);
    }

    private class SetFanartTask extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... params) {
            return bcm.get(params[0], BannerCacheManager.BitmapType.FANART);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageDrawable = new BitmapDrawable(getResources(), bitmap);
            fanart.setImageBitmap(bitmap);
            header.setImageBitmap(bitmap);
        }
    }
}