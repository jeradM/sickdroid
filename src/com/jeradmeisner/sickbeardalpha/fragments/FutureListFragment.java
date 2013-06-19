package com.jeradmeisner.sickbeardalpha.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.jeradmeisner.sickbeardalpha.*;
import com.jeradmeisner.sickbeardalpha.interfaces.FutureListItem;
import com.jeradmeisner.sickbeardalpha.task.LoadEpisodeDetailsTask;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;
import com.jeradmeisner.sickbeardalpha.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;
import com.jeradmeisner.sickbeardalpha.widgets.FutureSectionHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FutureListFragment extends SherlockListFragment {

    private static final String TAG = "FutureListFragment";

    private List<FutureListItem> items;
    private FutureAdapter adapter;
    private Shows shows;
    private String apiurl;

    BannerCacheManager bcm;

    public FutureListFragment(Shows shows, String apiurl)
    {
        super();
        this.shows = shows;
        this.apiurl = apiurl;
        items = new ArrayList<FutureListItem>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bcm = BannerCacheManager.getInstance(getSherlockActivity());
        refreshFuture();
    }

    public void refreshFuture()
    {
        new LoadFuturetask().execute(apiurl);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView list = getListView();
        list.setDividerHeight(0);

        adapter = new FutureAdapter(getSherlockActivity(), 0, items);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        HistoryItem item = (HistoryItem)l.getAdapter().getItem(position);
        new LoadEpisodeDetailsTask(apiurl, getSherlockActivity().getSupportFragmentManager()).execute(item);
    }

    public class LoadFuturetask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... params) {
            items.clear();
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(params[0], ApiCommands.FUTURE.toString());
            JSONObject data = SickbeardJsonUtils.parseObjectFromJson(obj, "data");
            JSONArray today = SickbeardJsonUtils.parseArrayFromJson(data, "today");
            JSONArray soon = SickbeardJsonUtils.parseArrayFromJson(data, "soon");
            JSONArray later = SickbeardJsonUtils.parseArrayFromJson(data, "later");
            JSONArray missed = SickbeardJsonUtils.parseArrayFromJson(data, "missed");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
            boolean showMissed = prefs.getBoolean("show_missed", true);

            try {
                if (showMissed && missed.length() > 0) {
                    items.add(new FutureSectionHeader("Missed"));
                    for (int i = 0; i < missed.length(); i++) {
                        JSONObject next = missed.getJSONObject(i);
                        addFutureItem(next);
                    }
                }
                publishProgress();
                items.add(new FutureSectionHeader("Today"));
                for (int i = 0; i < today.length(); i++) {
                    JSONObject next = today.getJSONObject(i);
                    addFutureItem(next);
                }
                publishProgress();
                items.add(new FutureSectionHeader("Soon"));
                for (int i = 0; i < soon.length(); i++) {
                    JSONObject next = soon.getJSONObject(i);
                    addFutureItem(next);
                }
                publishProgress();
                items.add(new FutureSectionHeader("Later"));
                for (int i = 0; i < later.length(); i++) {
                    JSONObject next = later.getJSONObject(i);
                    addFutureItem(next);
                }
            }
            catch (JSONException e) {
                Log.e(TAG, "Error fetching future");
            }

            return null;
        }

        private void addFutureItem(JSONObject next) throws JSONException
        {
            String tvdbid = next.getString("tvdbid");
            String season = String.valueOf(next.getInt("season"));
            String episode = String.valueOf(next.getInt("episode"));
            String airdate = next.getString("airdate");
            Show show = shows.findShow(tvdbid);
            items.add(new FutureItem(show, season, episode, airdate));
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
