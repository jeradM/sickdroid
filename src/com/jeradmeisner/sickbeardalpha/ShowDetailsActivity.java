package com.jeradmeisner.sickbeardalpha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.jeradmeisner.sickbeardalpha.utils.ArtworkDownloader;
import com.jeradmeisner.sickbeardalpha.widgets.ObservableScrollView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/16/13
 * Time: 1:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ShowDetailsActivity extends SherlockActivity implements ObservableScrollView.ScrollListener {

    private ObservableScrollView mScrollView;
    private ImageView fanart;
    private ImageView header;
    private Drawable actionBarBackground;
    private BitmapDrawable imageDrawable;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_show_details);

        Intent i = getIntent();
        ArrayList<Show> showList = i.getParcelableArrayListExtra("show");
        Show show = showList.get(0);

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

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int maxWidth = size.x;

            try {
                Bitmap bitmap = ArtworkDownloader.fetchFanart(params[0], maxWidth);
                return bitmap;
            }
            catch (IOException e) {
                Log.e("ShowDetailsActivity", "Unable to fetch fanart");
                return BitmapFactory.decodeResource(getResources(), R.id.transparent_header);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageDrawable = new BitmapDrawable(getResources(), bitmap);
            fanart.setBackground(imageDrawable);
        }
    }
}