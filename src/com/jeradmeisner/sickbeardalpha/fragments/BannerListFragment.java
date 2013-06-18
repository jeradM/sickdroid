package com.jeradmeisner.sickbeardalpha.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.jeradmeisner.sickbeardalpha.*;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;

import java.util.ArrayList;
import java.util.List;

public class BannerListFragment extends SherlockListFragment {

    private Shows shows;
    private BannerCacheManager bcm;
    private BannerAdapter adapter;

    public BannerListFragment(Shows shows)
    {
        super();
        this.shows = shows;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bcm = BannerCacheManager.getInstance(getSherlockActivity());
    }

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

       // int resource = getSherlockActivity().getResources().getInteger(R.layout.banner_list_item);
        adapter = new BannerAdapter(getSherlockActivity(), R.layout.banner_list_item, shows.getShowList());
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new LoadImagesTask().execute(null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setTextFilterEnabled(true);
    }

    public class LoadImagesTask extends AsyncTask<Object, Void, Void> {

        protected Void doInBackground(Object... params)
        {
            for (Show show : shows.getShowList()) {
                show.setBannerImage(bcm.get(show.getTvdbid(), BannerCacheManager.BitmapType.BANNER));
                publishProgress();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }
}
