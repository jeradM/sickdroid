package com.jeradmeisner.sickdroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import com.jeradmeisner.sickdroid.data.Season;
import com.jeradmeisner.sickdroid.data.SeasonEpisode;
import com.jeradmeisner.sickdroid.data.Show;
import com.jeradmeisner.sickdroid.task.LoadEpisodeDetailsTask;
import com.jeradmeisner.sickdroid.utils.*;
import com.jeradmeisner.sickdroid.utils.enumerations.ApiCommands;
import com.jeradmeisner.sickdroid.widgets.ObservableScrollView;

import android.support.v4.app.FragmentManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class ShowDetailsActivity extends SherlockFragmentActivity implements ObservableScrollView.ScrollListener {

    private static final String TAG = "ShowDetailsActivity";

    private ObservableScrollView mScrollView;

    private TextView airsTextView;
    private TextView qualityTextView;
    private TextView langTextView;
    private TextView statusTextView;
    private TextView seriesOverview;

    private ImageView fanart;
    private ImageView header;
    private ImageView poster;

    private FragmentManager fm;

    private Drawable actionBarBackground;
    private BitmapDrawable imageDrawable;

    LinearLayout seasonsLayout;

    boolean isExpanded = false;

    private Show show;

    private String apiurl;

    private List<Season> seasons;

    BannerCacheManager bcm = BannerCacheManager.getInstance(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_show_details);

        airsTextView = (TextView)findViewById(R.id.airs);
        qualityTextView = (TextView)findViewById(R.id.quality);
        langTextView = (TextView)findViewById(R.id.language);
        statusTextView = (TextView)findViewById(R.id.status);
        seriesOverview = (TextView)findViewById(R.id.series_overview);

        seasonsLayout = (LinearLayout)findViewById(R.id.seasons_layout);

        Intent i = getIntent();
        show = i.getParcelableExtra("show");
        fanart = (ImageView)findViewById(R.id.fanart_image);
        apiurl = i.getStringExtra("apiurl");

        seasons = new ArrayList<Season>();
        new GetAirsStringTask().execute();
        new FetchSeasonsTask().execute();
        new SetFanartTask().execute(show.getTvdbid());

        header = (ImageView)findViewById(R.id.transparent_header);
        poster = (ImageView)findViewById(R.id.show_poster);

        mScrollView = (ObservableScrollView)findViewById(R.id.scroll_view);
        mScrollView.setScrollListener(this);

        /*mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged(null, 0, 0, 0, 0);
                    }

                });*/

        actionBarBackground = getResources().getDrawable(R.drawable.ab_bg);
        actionBarBackground.setAlpha(0);
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
        if (poster != null) {
            poster.setTranslationY(Math.min(0, header.getTop() - (mScrollView.getScrollY() / 1.05f)));
        }
        fanart.setTranslationY(Math.min(0, header.getTop() - (mScrollView.getScrollY() / 3)));
        final int headerHeight = findViewById(R.id.fanart_image).getHeight() - getSupportActionBar().getHeight();
        final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
        final int newAlpha = (int) (ratio * 255);
        actionBarBackground.setAlpha(newAlpha);
    }

    private class GetAirsStringTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String cmd = String.format(ApiCommands.SHOW.toString(), show.getTvdbid());
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(apiurl, cmd);
            JSONObject data = SickbeardJsonUtils.parseObjectFromJson(obj, "data");

            String airs;
            try {
                airs = data.getString("airs");
            } catch (JSONException e) {
                airs = "Unknown";
            }

            return airs;
        }

        @Override
        protected void onPostExecute(String s) {
            airsTextView.setText("Airs: " + s);
            qualityTextView.setText("Quality: " + show.getQuality());
            langTextView.setText("Language: " + show.getLanguage());
            statusTextView.setText("Status: " + show.getStatus());
            seriesOverview.setText(show.getOverview());
        }
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
                Collections.sort(season.getEpisodes(), new EpisodeComparator());
                seasons.add(season);

            }
            Collections.sort(seasons, new SeasonComparator());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (Season season : seasons) {
                TextView tv = new TextView(ShowDetailsActivity.this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tv.setText(season.toString());
                tv.setTextSize(16);
                tv.setTypeface(Typeface.DEFAULT_BOLD);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setBackground(getResources().getDrawable(R.drawable.show_stats_rounded));
                tv.setPadding(5, 8, 8, 5);

                final LinearLayout episodes = new LinearLayout(ShowDetailsActivity.this);
                LinearLayout.LayoutParams epParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                episodes.setLayoutParams(epParams);
                episodes.setOrientation(LinearLayout.VERTICAL);

                for (final SeasonEpisode episode : season.getEpisodes()) {
                    TextView epView = new TextView(ShowDetailsActivity.this);
                    epView.setLayoutParams(params);
                    epView.setText(episode.getEpisode() + " " + episode.getTitle());
                    epView.setPadding(5, 2, 5, 2);
                    epView.setTextSize(13);

                    epView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new LoadEpisodeDetailsTask(ShowDetailsActivity.this, apiurl,
                                    ShowDetailsActivity.this.getSupportFragmentManager()).execute(episode);
                        }
                    });
                    episodes.addView(epView);
                }

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int vis = episodes.getVisibility();
                        episodes.setVisibility(vis == View.GONE ? View.VISIBLE : View.GONE);
                    }
                });

                seasonsLayout.addView(tv);
                seasonsLayout.addView(episodes);
                episodes.setVisibility(View.GONE);
            }
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