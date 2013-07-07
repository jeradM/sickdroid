package com.jeradmeisner.sickdroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.jeradmeisner.sickdroid.adapters.SeasonListAdapter;
import com.jeradmeisner.sickdroid.data.Season;
import com.jeradmeisner.sickdroid.data.SeasonEpisode;
import com.jeradmeisner.sickdroid.data.Show;
import com.jeradmeisner.sickdroid.utils.ArtworkDownloader;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;
import com.jeradmeisner.sickdroid.utils.SeasonComparator;
import com.jeradmeisner.sickdroid.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickdroid.utils.enumerations.ApiCommands;
import com.jeradmeisner.sickdroid.widgets.ObservableScrollView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class ShowDetailsActivity extends SherlockActivity implements ObservableScrollView.ScrollListener {

    private static final String TAG = "ShowDetailsActivity";

    private ObservableScrollView mScrollView;
    private TextView seriesOverview;
    private ImageView fanart;
    private ImageView header;
    private ImageView poster;
    private Drawable actionBarBackground;
    private BitmapDrawable imageDrawable;
    boolean isExpanded = false;

    LinearLayout showStats;

    ExpandableListView expandList;
    SeasonListAdapter adapter;

    private Show show;

    private String apiurl;

    private List<Season> seasons;

    BannerCacheManager bcm = BannerCacheManager.getInstance(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_show_details);

        Intent i = getIntent();
        show = i.getParcelableExtra("show");
        fanart = (ImageView)findViewById(R.id.fanart_image);
        apiurl = i.getStringExtra("apiurl");


        seasons = new ArrayList<Season>();
        expandList = (ExpandableListView)findViewById(R.id.seasons_layout);
        adapter = new SeasonListAdapter(ShowDetailsActivity.this, seasons);
        new FetchSeasonsTask().execute();

        new SetFanartTask().execute(show.getTvdbid());

        //seriesOverview = (TextView)findViewById(R.id.series_overview);
        //seriesOverview.setText(show.getOverview());


        showStats = (LinearLayout)findViewById(R.id.show_stats_bg);

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

    private class FetchSeasonsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String cmd = String.format(ApiCommands.SEASONS.toString(), show.getTvdbid());
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(apiurl, cmd);
            JSONObject data = SickbeardJsonUtils.parseObjectFromJson(obj, "data");

            Iterator<?> dataItr = data.keys();
            while(dataItr.hasNext()) {
                String seasonNumber = dataItr.next().toString();
                int seasonInt = Integer.parseInt(seasonNumber);
                JSONObject nextSeason = SickbeardJsonUtils.parseObjectFromJson(data, seasonNumber);

                Season season = new Season(seasonInt);

                Iterator<?> epItr = nextSeason.keys();
                while (epItr.hasNext()) {
                    String epNum = epItr.next().toString();
                    int epInt = Integer.parseInt(epNum);
                    JSONObject episode = SickbeardJsonUtils.parseObjectFromJson(nextSeason, epNum);

                    try {
                        String airdate = episode.getString("airdate");
                        String name = episode.getString("name");
                        String quality = episode.getString("quality");
                        String status = episode.getString("status");
                        season.addEpisode(new SeasonEpisode(show, name, seasonInt, epInt, airdate, status, quality));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing seasons");
                    }
                }

                seasons.add(season);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Collections.sort(seasons, new SeasonComparator());
            /*for (Season season : seasons) {
                TextView tv = new TextView(ShowDetailsActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 4;
                params.bottomMargin = 4;
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setLayoutParams(params);
                tv.setTextSize(20);
                tv.setText(season.toString());
                seasonLayout.addView(tv);
            }*/


            expandList.setAdapter(adapter);

        }
    }

    private class SetFanartTask extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap b = bcm.get(params[0], BannerCacheManager.BitmapType.FANART);
            publishProgress();
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
        protected void onProgressUpdate(Void... values) {
            poster.setImageBitmap(bcm.get(show.getTvdbid(), BannerCacheManager.BitmapType.POSTER));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageDrawable = new BitmapDrawable(getResources(), bitmap);
            fanart.setImageBitmap(bitmap);
            header.setImageBitmap(bitmap);
        }
    }
}