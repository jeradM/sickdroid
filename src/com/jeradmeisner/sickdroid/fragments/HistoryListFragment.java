package com.jeradmeisner.sickdroid.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.jeradmeisner.sickdroid.*;
import com.jeradmeisner.sickdroid.adapters.HistoryAdapter;
import com.jeradmeisner.sickdroid.data.Episode;
import com.jeradmeisner.sickdroid.data.HistoryEpisode;
import com.jeradmeisner.sickdroid.data.Show;
import com.jeradmeisner.sickdroid.data.Shows;
import com.jeradmeisner.sickdroid.task.LoadEpisodeDetailsTask;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;
import com.jeradmeisner.sickdroid.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickdroid.utils.enumerations.ApiCommands;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HistoryListFragment extends SherlockListFragment implements ShowsActivity.SickFragment {

    private static final String TAG = "HistoryListFragment";

    private List<HistoryEpisode> items;
    private HistoryAdapter adapter;
    private List<Show> shows;
    private String apiurl;

    BannerCacheManager bcm;

    public static HistoryListFragment getInstance(List<Show> shows, String apiurl) {
        HistoryListFragment frag = new HistoryListFragment();
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
        refreshHistory(apiurl);
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
        new GetAirDateTask().execute(item);
    }

    public void refreshHistory(String apiurl)
    {
        if (items == null) {
            items = new ArrayList<HistoryEpisode>();
        }

        if (adapter == null) {
            adapter = new HistoryAdapter(getSherlockActivity(), R.layout.history_list_item, items);
            setListAdapter(adapter);
        }

        bcm = BannerCacheManager.getInstance(getSherlockActivity());
        items.clear();
        adapter.notifyDataSetInvalidated();
        new LoadHistoryTask().execute(apiurl);
    }

    public void update()
    {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void showDetails(Episode item)
    {
        new LoadEpisodeDetailsTask(getSherlockActivity(), apiurl, getSherlockActivity().getSupportFragmentManager()).execute(item);
    }

    public class LoadHistoryTask extends AsyncTask<String, Void, List<HistoryEpisode>>
    {

        @Override
        protected List<HistoryEpisode> doInBackground(String... params) {
            List<HistoryEpisode> itemstemp = new ArrayList<HistoryEpisode>();

            String historyString = ApiCommands.HISTORY.toString();
            String historyCmd = String.format(historyString, "40", "downloaded");
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(params[0], historyCmd);

            if (obj == null) {
                return null;
            }

            JSONArray array = SickbeardJsonUtils.parseArrayFromJson(obj, "data");

            try {
                for(int i = 0; i < array.length(); i++) {
                    JSONObject nextItem = array.getJSONObject(i);
                    String date = nextItem.get("date").toString();
                    int episode = nextItem.getInt("episode");
                    int season = nextItem.getInt("season");
                    String id = nextItem.get("tvdbid").toString();
                    String status = nextItem.get("status").toString();

                    Show newShow = null;
                    for (Show s : shows) {
                        if (s.getTvdbid().equals(id)) {
                            newShow = s;
                        }
                    }

                    if (newShow != null) {
                        //newShow.setPosterImage(bcm.get(newShow.getTvdbid(), BannerCacheManager.BitmapType.POSTER));
                        itemstemp.add(new HistoryEpisode(newShow, null, season, episode, date, status));
                    }
                    //publishProgress();

                }
            } catch (JSONException e) {
                Log.e(TAG, "Error loading history");
            }

            return itemstemp;
        }

       /* @Override
        protected void onProgressUpdate(Void... values) {
            adapter.notifyDataSetChanged();
        }*/

        @Override
        protected void onPostExecute(List<HistoryEpisode> itemsList) {
            if (itemsList != null) {
                if (adapter != null) {
                    for (HistoryEpisode ep : itemsList) {
                        items.add(ep);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            else {
                if (adapter != null) {
                    adapter.notifyDataSetInvalidated();
                }
            }
        }
    }

    private class GetAirDateTask extends AsyncTask<Episode, Void, Episode>
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

            String airDate;
            String title;
            try {
                airDate = data.getString("airdate");
                title = data.getString("name");
            } catch (JSONException e) {
                airDate = "";
                title = "Title unavailable";
            }
            ((HistoryEpisode)episode[0]).setAirDate(airDate);
            episode[0].setTitle(title);
            return episode[0];
        }

        @Override
        protected void onPostExecute(Episode episode) {

            showDetails(episode);
        }
    }
}
