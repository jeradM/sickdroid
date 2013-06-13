package com.jeradmeisner.sickbeardalpha;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jeradmeisner.sickbeardalpha.fragments.BannerListFragment;

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


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);
        Drawable abBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar_background_light_green);
        getSupportActionBar().setBackgroundDrawable(abBackgroundDrawable);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bannerListFragment = new BannerListFragment();


        viewPager = (ViewPager)findViewById(R.id.shows_view_pager);
        List<Fragment> fragments = getFragments();
        pagerAdapter = new ShowPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);

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

}