package com.jeradmeisner.sickbeardalpha;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.jeradmeisner.sickbeardalpha.fragments.BannerListFragment;
import com.jeradmeisner.sickbeardalpha.fragments.FutureListFragment;
import com.jeradmeisner.sickbeardalpha.fragments.HistoryListFragment;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;
import com.jeradmeisner.sickbeardalpha.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;
import com.jeradmeisner.sickbeardalpha.widgets.FutureSectionHeader;
import com.viewpagerindicator.TitlePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerad on 6/10/13.
 */
public class ShowsActivity extends SherlockFragmentActivity implements SearchView.OnQueryTextListener {

    private static final int NUM_PAGES = 3;
    private static final String TAG = "ShowsActivity";

    private String apiUrl;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TitlePageIndicator indicator;

    private BannerListFragment bannerListFragment;
    private BannerAdapter bannerAdapter;
    private List<Show> showList;
    private Shows shows;

    private HistoryListFragment historyListFragment;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyItems;

    private FutureListFragment futureListFragment;
    private List<FutureItem> futureItems;
    private FutureAdapter futureAdapter;


    private SearchView searchView;

    MenuItem searchItem;

    private BannerCacheManager bcm;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);

        Intent i = getIntent();
        showList = i.getParcelableArrayListExtra("showlist");
        shows = new Shows(showList);
        apiUrl = i.getStringExtra("apiUrl");

        bcm = BannerCacheManager.getInstance(this);

        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        historyItems = new ArrayList<HistoryItem>();
        futureItems = new ArrayList<FutureItem>();

        setUpBannerFragment();
        setUpHistoryFragment();
        setUpFutureFragment();

        new LoadImagesTask().execute(null);
        new LoadHistoryTask().execute(apiUrl);

        viewPager = (ViewPager)findViewById(R.id.shows_view_pager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        indicator = (TitlePageIndicator)findViewById(R.id.title_page_indicator);

        List<Fragment> fragments = getFragments();
        pagerAdapter = new ShowPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);
        indicator.setFooterColor(getResources().getColor(R.color.white));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.show_menu, menu);
        searchItem = menu.findItem(R.id.search_shows);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ShowsActivity.this.onClose();
                return true;
            }
        });
        searchView = (SearchView)menu.findItem(R.id.search_shows).getActionView();
        searchView.setOnQueryTextListener(ShowsActivity.this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_profiles:
                Intent i = new Intent(this, ProfilesActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpBannerFragment()
    {
        bannerListFragment = new BannerListFragment();
        bannerAdapter = new BannerAdapter(this, R.layout.banner_list_item, showList);
        bannerListFragment.setListAdapter(bannerAdapter);
    }

    private void setUpHistoryFragment()
    {
        historyListFragment = new HistoryListFragment(apiUrl);
        historyAdapter = new HistoryAdapter(this, R.layout.history_list_item, historyItems);
        historyListFragment.setListAdapter(historyAdapter);
    }

    private void setUpFutureFragment()
    {
        futureListFragment = new FutureListFragment();
        futureAdapter = new FutureAdapter(this, 0, futureItems);
        futureListFragment.setListAdapter(futureAdapter);
    }

    public List<Fragment> getFragments()
    {
        List<Fragment> frags = new ArrayList<Fragment>();
        frags.add(bannerListFragment);
        frags.add(historyListFragment);
        frags.add(futureListFragment);
        return frags;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            bannerAdapter.getFilter().filter(newText);
            return true;
        }
        else {
            bannerAdapter.getFilter().filter(null);
            return true;
        }
    }

    public boolean onClose()
    {
        bannerAdapter.getFilter().filter(null);
        return true;
    }



    public class ShowPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = {"Shows", "History", "Future"};
        List<Fragment> frags;

        public ShowPagerAdapter(FragmentManager fm, List<Fragment> frags)
        {
            super(fm);
            this.frags = frags;
        }

        @Override
        public Fragment getItem(int i) {
            return this.frags.get(i);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position % TITLES.length];
        }
    }

    public class LoadImagesTask extends AsyncTask<Object, Void, Void> {

        protected Void doInBackground(Object... params)
        {
            for (Show show : showList) {
                show.setBannerImage(bcm.get(show.getTvdbid(), BannerCacheManager.BitmapType.BANNER));
                publishProgress();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            bannerAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            bannerAdapter.notifyDataSetChanged();
        }
    }

    public class LoadHistoryTask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... params) {
            String historyString = ApiCommands.HISTORY.toString();
            String historyCmd = String.format(historyString, "40", "downloaded");
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(params[0], historyCmd);
            JSONArray array = SickbeardJsonUtils.parseArrayFromJson(obj, "data");

            try {
                for(int i = 0; i < array.length(); i++) {
                    JSONObject nextItem = array.getJSONObject(i);
                    String date = nextItem.get("date").toString();
                    String episode = nextItem.get("episode").toString();
                    String season = nextItem.get("season").toString();
                    String id = nextItem.get("tvdbid").toString();

                    Show newShow = shows.findShow(id);

                    if (newShow != null) {
                        newShow.setPosterImage(bcm.get(newShow.getTvdbid(), BannerCacheManager.BitmapType.POSTER));
                        historyItems.add(new HistoryItem(newShow, season, episode, date));
                    }

                }
            } catch (JSONException e) {
                Log.e(TAG, "Error loading history");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            historyAdapter.notifyDataSetChanged();
        }
    }

    public class LoadFuturetask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... params) {
            futureItems.clear();
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(params[0], ApiCommands.FUTURE.toString());
            JSONObject data = SickbeardJsonUtils.parseObjectFromJson(obj, "data");
            JSONArray today = SickbeardJsonUtils.parseArrayFromJson(data, "today");
            JSONArray soon = SickbeardJsonUtils.parseArrayFromJson(data, "soon");
            JSONArray later = SickbeardJsonUtils.parseArrayFromJson(data, "later");
            JSONArray missed = SickbeardJsonUtils.parseArrayFromJson(data, "missed");

            try {
                futureItems.add(new FutureSectionHeader("Today"));
                for (int t = 0; t < today.length(); t++) {
                    JSONObject next = today.getJSONObject(t);
                    addFutureItem(next);
                }
                futureItems.add(new FutureSectionHeader("Soon"));
                for (int i = 0; i < soon.length(); i++) {
                    JSONObject next = soon.getJSONObject(i);
                    addFutureItem(next);
                }
                futureItems.add(new FutureSectionHeader("Later"));
                for (int i = 0; i < later.length(); i++) {
                    JSONObject next = later.getJSONObject(i);
                    addFutureItem(next);
                }
            }
            catch (JSONException e) {
                Log.e(TAG, "Error fetching future");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            futureAdapter.notifyDataSetChanged();
        }
    }

    private void addFutureItem(JSONObject next) throws JSONException
    {
        String tvdbid = next.getString("tvdbid");
        int season = next.getInt("season");
        int episode = next.getInt("episode");
        String airdate = next.getString("airdate");
        Show show = shows.findShow(tvdbid);
        futureItems.add(new FutureItem(show, season, episode, airdate));
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private  float MIN_SCALE = 0.85f;
        private  float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}