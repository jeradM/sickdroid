package com.jeradmeisner.sickbeardalpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.jeradmeisner.sickbeardalpha.data.Show;
import com.jeradmeisner.sickbeardalpha.data.Shows;
import com.jeradmeisner.sickbeardalpha.fragments.BannerListFragment;
import com.jeradmeisner.sickbeardalpha.fragments.FutureListFragment;
import com.jeradmeisner.sickbeardalpha.fragments.HistoryListFragment;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;


public class ShowsActivity extends SherlockFragmentActivity {

    private static final int NUM_PAGES = 3;
    private static final String TAG = "ShowsActivity";

    private String apiUrl;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TitlePageIndicator indicator;

    private BannerListFragment bannerListFragment;
    private List<Show> showList;
    private Shows shows;

    private HistoryListFragment historyListFragment;

    private FutureListFragment futureListFragment;


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
        getSupportActionBar().setDisplayShowTitleEnabled(false);;

        bannerListFragment = new BannerListFragment(shows);
        futureListFragment = new FutureListFragment(shows, apiUrl);
        historyListFragment = new HistoryListFragment(shows, apiUrl);

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
        searchView.setOnQueryTextListener(bannerListFragment);

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

    public List<Fragment> getFragments()
    {
        List<Fragment> frags = new ArrayList<Fragment>();
        frags.add(bannerListFragment);
        frags.add(futureListFragment);
        frags.add(historyListFragment);
        return frags;
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