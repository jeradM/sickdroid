package com.jeradmeisner.sickbeardalpha.fragments;

import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.SherlockListFragment;

public class BannerListFragment extends SherlockListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDividerHeight(0);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setTextFilterEnabled(true);
    }
}
