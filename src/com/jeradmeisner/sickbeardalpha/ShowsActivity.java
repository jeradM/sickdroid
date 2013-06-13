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
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jeradmeisner.sickbeardalpha.fragments.BannerListFragment;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerad on 6/10/13.
 */
public class ShowsActivity extends SherlockFragmentActivity {

    private static final int NUM_PAGES = 1;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
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
        List<Fragment> fragments = getFragments();
        pagerAdapter = new ShowPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);

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
        return frags;
    }

    public class ShowPagerAdapter extends FragmentPagerAdapter {
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

}