package com.jeradmeisner.sickbeardalpha;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jeradmeisner.sickbeardalpha.fragments.BannerListFragment;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerad on 6/10/13.
 */
public class ShowsActivity extends SherlockFragmentActivity {

    private static final int NUM_PAGES = 2;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TitlePageIndicator indicator;
    private BannerListFragment bannerListFragment;
    private BannerAdapter bannerAdapter;
    private List<Show> showList;
    private Shows shows;

    private BannerCacheManager bcm;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);

        Intent i = getIntent();
        showList = i.getParcelableArrayListExtra("showlist");
        shows = new Shows(showList);

        bcm = BannerCacheManager.getInstance(this);

        Drawable abBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar_background_light_green);
        getSupportActionBar().setBackgroundDrawable(abBackgroundDrawable);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setUpBannerFragment();

        new LoadBannersTask().execute(null);

        viewPager = (ViewPager)findViewById(R.id.shows_view_pager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        indicator = (TitlePageIndicator)findViewById(R.id.title_page_indicator);

        List<Fragment> fragments = getFragments();
        pagerAdapter = new ShowPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);
        indicator.setFooterColor(getResources().getColor(R.color.white));

    }

    private void setUpBannerFragment()
    {
        bannerListFragment = new BannerListFragment();
        bannerAdapter = new BannerAdapter(this, R.layout.banner_list_item, showList);
        bannerListFragment.setListAdapter(bannerAdapter);
    }

    public List<Fragment> getFragments()
    {
        List<Fragment> frags = new ArrayList<Fragment>();
        frags.add(bannerListFragment);
        frags.add(new BannerListFragment());
        return frags;
    }

    public class ShowPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = {"Shows", "History"};
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

    public class LoadBannersTask extends AsyncTask<Object, Void, Void> {

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