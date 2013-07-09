package com.jeradmeisner.sickdroid.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.jeradmeisner.sickdroid.ShowsActivity;
import com.jeradmeisner.sickdroid.adapters.FutureAdapter;
import com.jeradmeisner.sickdroid.data.*;
import com.jeradmeisner.sickdroid.interfaces.FutureListItem;
import com.jeradmeisner.sickdroid.task.LoadEpisodeDetailsTask;
import com.jeradmeisner.sickdroid.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickdroid.utils.enumerations.ApiCommands;
import com.jeradmeisner.sickdroid.widgets.FutureSectionHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FutureListFragment extends SherlockListFragment implements ShowsActivity.SickFragment {

    private static final String TAG = "FutureListFragment";

    private List<FutureListItem> items;
    private FutureAdapter adapter;
    private List<Show> shows;
    private String apiurl;

    public static FutureListFragment getInstance(List<Show> shows, String apiurl) {
        FutureListFragment frag = new FutureListFragment();
        Bundle b = new Bundle(2);
        b.putParcelableArrayList("shows", (ArrayList)shows);
        b.putString("apiurl", apiurl);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        shows = b.getParcelableArrayList("shows");
        apiurl = b.getString("apiurl");
        refreshFuture(apiurl);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView list = getListView();
        list.setDividerHeight(0);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Episode item = (Episode)l.getAdapter().getItem(position);
        new GetStatusTask().execute(item);
    }

    public void refreshFuture(String apiurl)
    {
        if (items == null) {
            items = new ArrayList<FutureListItem>();
        }

        if (adapter == null) {
            adapter = new FutureAdapter(getSherlockActivity(), 0, items);
            setListAdapter(adapter);
        }
        this.apiurl = apiurl;
        items.clear();
        adapter.notifyDataSetInvalidated();
        new LoadFuturetask().execute(apiurl);
    }

    public void update()
    {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void showDetails(Episode e)
    {
        new LoadEpisodeDetailsTask(getSherlockActivity(), apiurl, getSherlockActivity().getSupportFragmentManager()).execute(e);
    }

    public class LoadFuturetask extends AsyncTask<String, Void, Integer>
    {

        @Override
        protected Integer doInBackground(String... params) {
            items.clear();
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(params[0], ApiCommands.FUTURE.toString());

            if (obj == null) {
                return 1;
            }

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
                if (today.length() > 0) {
                    items.add(new FutureSectionHeader("Today"));
                    for (int i = 0; i < today.length(); i++) {
                        JSONObject next = today.getJSONObject(i);
                        addFutureItem(next);
                    }
                }
                publishProgress();
                if (soon.length() > 0) {
                    items.add(new FutureSectionHeader("Soon"));
                    for (int i = 0; i < soon.length(); i++) {
                        JSONObject next = soon.getJSONObject(i);
                        addFutureItem(next);
                    }
                }
                publishProgress();
                if (later.length() > 0) {
                    items.add(new FutureSectionHeader("Later"));
                    for (int i = 0; i < later.length(); i++) {
                        JSONObject next = later.getJSONObject(i);
                        addFutureItem(next);
                    }
                }
            }
            catch (JSONException e) {
                Log.e(TAG, "Error fetching future");
            }

            return 0;
        }

        private void addFutureItem(JSONObject next) throws JSONException
        {
            String tvdbid = next.getString("tvdbid");
            String title = next.getString("ep_name");
            int season = next.getInt("season");
            int episode = next.getInt("episode");
            String airdate = next.getString("airdate");

            Show show = null;
            for (Show s : shows) {
                if (s.getTvdbid().equals(tvdbid)) {
                    show = s;
                }
            }

            if (show != null) {
                items.add(new FutureEpisode(show, title, season, episode, airdate));
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Integer status) {
            if (status == 0) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
            else {
                adapter.notifyDataSetInvalidated();
            }
        }
    }

    private class GetStatusTask extends AsyncTask<Episode, Void, Episode>
    {
        @Override
        protected Episode doInBackground(Episode... episode) {
            Show s = episode[0].getShow();
            int season = episode[0].getSeason();
            int ep = episode[0].getEpisode();
            String cmd = String.format(ApiCommands.EPISODE.toString(), s.getTvdbid(), season, ep);
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(apiurl, cmd);
            JSONObject data = SickbeardJsonUtils.parseObjectFromJson(obj, "data");

            if (data == null)
                return null;

            String status;
            try {
                status = data.getString("status");
            } catch (JSONException e) {
                status = "";
            }
            episode[0].setStatus(status);
            return episode[0];
        }

        @Override
        protected void onPostExecute(Episode episode) {
            showDetails(episode);
        }
    }
}
