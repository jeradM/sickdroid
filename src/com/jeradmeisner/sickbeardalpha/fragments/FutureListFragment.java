package com.jeradmeisner.sickbeardalpha.fragments;

import android.os.Bundle;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;


public class FutureListFragment extends SherlockListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView list = getListView();
        list.setDividerHeight(0);
    }


}
