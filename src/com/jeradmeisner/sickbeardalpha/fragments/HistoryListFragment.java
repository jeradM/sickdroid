package com.jeradmeisner.sickbeardalpha.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.jeradmeisner.sickbeardalpha.*;
import com.jeradmeisner.sickbeardalpha.adapters.HistoryAdapter;
import com.jeradmeisner.sickbeardalpha.data.Episode;
import com.jeradmeisner.sickbeardalpha.data.HistoryEpisode;
import com.jeradmeisner.sickbeardalpha.data.Show;
import com.jeradmeisner.sickbeardalpha.data.Shows;
import com.jeradmeisner.sickbeardalpha.task.LoadEpisodeDetailsTask;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;
import com.jeradmeisner.sickbeardalpha.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HistoryListFragment extends SherlockListFragment {

    private static final String TAG = "HistoryListFragment";

    private List<HistoryEpisode> items;
    private HistoryAdapter adapter;
    private Shows shows;
    private String apiurl;

    BannerCacheManager bcm;

    public HistoryListFragment(Shows shows, String apiurl)
    {
        super();
        this.shows = shows;
        this.apiurl = apiurl;
        items = new ArrayList<HistoryEpisode>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bcm = BannerCacheManager.getInstance(getSherlockActivity());
        refreshHistory();
    }

    public void refreshHistory()
    {
        new LoadHistoryTask().execute(apiurl);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView list = getListView();
        list.setDividerHeight(0);

        adapter = new HistoryAdapter(getSherlockActivity(), R.layout.history_list_item, items);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Episode item = (Episode)l.getAdapter().getItem(position);
        new GetAirDateTask().execute(item);
    }

    private void showDetails(Episode item)
    {
        new LoadEpisodeDetailsTask(getSherlockActivity(), apiurl, getSherlockActivity().getSupportFragmentManager()).execute(item);
    }

    public class LoadHistoryTask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... params) {
            String historyString = ApiCommands.HISTORY.toString();
            String historyCmd = String.format(historyString, "40", "downloaded");
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(params[0], historyCmd);
            JSONArray array = SickbeardJsonUtils.parseArrayFromJson(obj, "data");

            try {
                for(int i = 0; i < array.length(); i++) {
                    JSONObject nextItem = array.getJSONObject(i);
                    String date = nextItem.get("date").toString();
                    int episode = nextItem.getInt("episode");
                    int season = nextItem.getInt("season");
                    String id = nextItem.get("tvdbid").toString();
                    String status = nextItem.get("status").toString();

                    Show newShow = shows.findShow(id);

                    if (newShow != null) {
                        newShow.setPosterImage(bcm.get(newShow.getTvdbid(), BannerCacheManager.BitmapType.POSTER));
                        items.add(new HistoryEpisode(newShow, season, episode, date, status));
                    }
                    publishProgress();

                }
            } catch (JSONException e) {
                Log.e(TAG, "Error loading history");
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
            String airDate;
            try {
                airDate = data.getString("airdate");
            } catch (JSONException e) {
                airDate = "";
            }
            ((HistoryEpisode)episode[0]).setAirDate(airDate);
            return episode[0];
        }

        @Override
        protected void onPostExecute(Episode episode) {
            showDetails(episode);
        }
    }
}
