package com.jeradmeisner.sickdroid;

import android.content.*;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.jeradmeisner.sickdroid.data.Show;
import com.jeradmeisner.sickdroid.data.Shows;
import com.jeradmeisner.sickdroid.data.SickbeardProfiles;
import com.jeradmeisner.sickdroid.fragments.BannerListFragment;
import com.jeradmeisner.sickdroid.fragments.FutureListFragment;
import com.jeradmeisner.sickdroid.fragments.HistoryListFragment;
import com.jeradmeisner.sickdroid.utils.ShowComparator;
import com.jeradmeisner.sickdroid.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickdroid.utils.enumerations.ApiCommands;
import com.viewpagerindicator.TitlePageIndicator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class ShowsActivity extends SherlockFragmentActivity implements SickbeardProfiles.OnProfileChangedListener {

    private static final int REQUEST_CODE_PROFILES = 1;

    private static final int NUM_PAGES = 3;
    private static final String TAG = "ShowsActivity";

    private String apiUrl;

    private List<Show> showList;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TitlePageIndicator indicator;

    private BannerListFragment bannerListFragment;
    private HistoryListFragment historyListFragment;
    private FutureListFragment futureListFragment;

    private SearchView searchView;

    private Shows shows;

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    private SickbeardProfiles profiles;

    private SharedPreferences prefs;

    MenuItem searchItem;

    public interface SickFragment {
        public void update();
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);

        //BannerCacheManager.getInstance(this).clearCache();

        showList = new ArrayList<Show>();
        shows = new Shows(showList);

        profiles = SickbeardProfiles.getInstance();
        profiles.registerOnProfileChangedListener(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //prefs.edit().putString(SickbeardProfiles.PREFS_CURRENT_PROFILE, "NONE").commit();
        if (prefs.getString(SickbeardProfiles.PREFS_CURRENT_PROFILE, "NONE").equals("NONE")) {
            showProfilesActivity();
        }
        else {
            getApiurl();
            new FetchShowsTask().execute();
        }

        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);;



        viewPager = (ViewPager)findViewById(R.id.shows_view_pager);
        //viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        indicator = (TitlePageIndicator)findViewById(R.id.title_page_indicator);

        indicator.setFooterColor(getResources().getColor(R.color.white));

        intentFilter = new IntentFilter(ImageCacheService.IMAGES_UPDATES);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                for (Fragment frag : getFragments()) {
                    if (frag != null) {
                        ((SickFragment)frag).update();
                    }
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }

    public void getApiurl()
    {
        String host = prefs.getString(SickbeardProfiles.PREFS_HOST, "localhost");
        String port = prefs.getString(SickbeardProfiles.PREFS_PORT, "8080");
        String webroot = prefs.getString(SickbeardProfiles.PREFS_WEBROOT, "");
        String apiKey = prefs.getString(SickbeardProfiles.PREFS_APIKEY, "12345");
        Boolean useHttps = prefs.getBoolean(SickbeardProfiles.PREFS_USEHTTPS, false);

        String protocol;
        if (useHttps)
            protocol = "https";
        else
            protocol = "http";

        if (!webroot.equals(""))
            webroot += "/";

        if (!port.equals("")) {
            port = ":" + port;
        }

        apiUrl = String.format("%s://%s%s/%sapi/%s/", protocol, host, port, webroot, apiKey);

    }

    @Override
    public void onProfileChanged() {
        getApiurl();
        new FetchShowsTask().execute();
    }

    public void update()
    {
        new FetchShowsTask().execute();
    }

    public void showProfilesActivity()
    {
        Intent i = new Intent(this, ProfilesActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PROFILES) {
            if (resultCode == RESULT_OK) {
                getApiurl();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.show_menu, menu);
        searchItem = menu.findItem(R.id.search_shows);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                viewPager.setCurrentItem(0);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                bannerListFragment.clearFilter();
                return true;
            }
        });
        searchView = (SearchView)menu.findItem(R.id.search_shows).getActionView();
        //searchView.setOnQueryTextListener(bannerListFragment);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_profiles:
                Intent i = new Intent(this, ProfilesActivity.class);
                startActivity(i);
                return true;
            case R.id.add_show:
                Toast.makeText(this, "Add Show", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.refresh_shows:
                update();
                return true;
            case R.id.menu_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public List<Fragment> getFragments()
    {
        List<Fragment> frags = new ArrayList<Fragment>();
        frags.add(bannerListFragment);
        frags.add(futureListFragment);
        frags.add(historyListFragment);
        return frags;
    }

    public class FetchShowsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... strings) {
            String cmd = ApiCommands.SHOWS.toString();
            JSONObject main = SickbeardJsonUtils.getJsonFromUrl(apiUrl, cmd);
            if (main == null) {
                showProfilesActivity();
                cancel(true);
            }
            JSONObject data = SickbeardJsonUtils.parseObjectFromJson(main, "data");



            showList.clear();
            Iterator<?> itr = data.keys();
            while(itr.hasNext()) {
                try {
                    String id = itr.next().toString();
                    JSONObject nextObject = data.getJSONObject(id);
                    String title = nextObject.getString("show_name");
                    String network = nextObject.getString("network");
                    String quality = nextObject.getString("quality");
                    String status = nextObject.getString("status");
                    String language = nextObject.getString("language");
                    String nextEp = nextObject.getString("next_ep_airdate");

                    int airByDate = nextObject.getInt("air_by_date");
                    int paused = nextObject.getInt("paused");

                    JSONObject cache = nextObject.getJSONObject("cache");
                    int banner = cache.getInt("banner");
                    int poster = cache.getInt("poster");

                    showList.add(new Show(id, title, network, quality, status, language, nextEp, airByDate, banner, poster, paused));

                }
                catch (JSONException e) {
                    Log.e("Show Builder", "Error parsing JSON information");
                    showList.clear();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Collections.sort(showList, new ShowComparator());

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int maxWidth = size.x;
            int maxHeight = (int)(size.y / 8);

            if (bannerListFragment == null) {
                bannerListFragment = BannerListFragment.getInstance(showList, apiUrl);
                searchView.setOnQueryTextListener(bannerListFragment);
            }
            else {
                bannerListFragment.refreshBanners();
            }
            if (futureListFragment == null) {
                futureListFragment = FutureListFragment.getInstance(showList, apiUrl);
            }
            else {
                futureListFragment.refreshFuture(apiUrl);
            }
            if (historyListFragment == null) {
                historyListFragment = HistoryListFragment.getInstance(showList, apiUrl);
            }
            else {
                historyListFragment.refreshHistory(apiUrl);
            }
            List<Fragment> fragments = getFragments();
            if (pagerAdapter == null) {
                pagerAdapter = new ShowPagerAdapter(getSupportFragmentManager(), fragments);
                viewPager.setAdapter(pagerAdapter);
                indicator.setViewPager(viewPager);
            }

            Intent downloadImagesIntent = new Intent(ShowsActivity.this, ImageCacheService.class);
            downloadImagesIntent.putParcelableArrayListExtra("showlist", (ArrayList<Show>)showList);
            downloadImagesIntent.putExtra("apiurl", apiUrl);
            downloadImagesIntent.putExtra("maxWidth", maxWidth);
            downloadImagesIntent.putExtra("maxHeight", maxHeight);
            startService(downloadImagesIntent);


        }
    }

    public class ShowPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = {"Shows", "Future", "History"};
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

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private  float MIN_SCALE = 0.85f;
        private  float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                view.setAlpha(0);
            }
        }
    }

}