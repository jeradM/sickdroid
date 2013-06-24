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
import com.actionbarsherlock.widget.SearchView;
import com.jeradmeisner.sickbeardalpha.*;
import com.jeradmeisner.sickbeardalpha.adapters.BannerAdapter;
import com.jeradmeisner.sickbeardalpha.data.Show;
import com.jeradmeisner.sickbeardalpha.data.Shows;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;

public class BannerListFragment extends SherlockListFragment implements SearchView.OnQueryTextListener {

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
        //bcm = BannerCacheManager.getInstance(getSherlockActivity());
        refreshBanners();
    }

    public void refreshBanners()
    {
        if (adapter != null)
            adapter.notifyDataSetInvalidated();

        bcm = BannerCacheManager.getInstance(getSherlockActivity());
        new LoadImagesTask().execute();
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
                intent.putExtra("show", show);
                startActivity(intent);
            }
        });

       // int resource = getSherlockActivity().getResources().getInteger(R.layout.banner_list_item);
        adapter = new BannerAdapter(getSherlockActivity(), R.layout.banner_list_item, shows.getShowList());
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setTextFilterEnabled(true);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            adapter.getFilter().filter(newText);
            return true;
        }
        else {
            adapter.getFilter().filter(null);
            return true;
        }
    }

    public void clearFilter()
    {
        adapter.getFilter().filter(null);
    }

    public class LoadImagesTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params)
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
