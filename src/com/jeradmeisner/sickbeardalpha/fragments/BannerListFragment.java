package com.jeradmeisner.sickbeardalpha.fragments;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockListFragment;

public class BannerListFragment extends SherlockListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDividerHeight(0);
    }
}
