package com.jeradmeisner.sickbeardalpha.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.jeradmeisner.sickbeardalpha.Show;
import com.jeradmeisner.sickbeardalpha.ShowDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class BannerListFragment extends SherlockListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView list = getListView();
        list.setDividerHeight(0);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getSherlockActivity(), ShowDetailsActivity.class);
                Show show = (Show)adapterView.getItemAtPosition(i);
                ArrayList<Show> shows = new ArrayList<Show>();
                shows.add(show);
                intent.putParcelableArrayListExtra("show", shows);
                startActivity(intent);
            }
        });

    }
}
