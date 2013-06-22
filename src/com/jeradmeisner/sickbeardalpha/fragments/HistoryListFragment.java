package com.jeradmeisner.sickbeardalpha.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockDialogFragment;
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

    private String episode;
    private String season;
    private String title;

    BannerCacheManager bcm;

    SherlockDialogFragment showDetails;
    Show show;

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

                }
            } catch (JSONException e) {
                Log.e(TAG, "Error loading history");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }

    /*private class LoadEpisodeDetailsTask extends AsyncTask<String, Void, String[]>
    {
        @Override
        protected String[] doInBackground(String... s) {
            String cmd = String.format(ApiCommands.EPISODE.toString(), s[0], season, episode);
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(apiurl, cmd);
            JSONObject data = SickbeardJsonUtils.parseObjectFromJson(obj, "data");
            try {
                String description = data.getString("description");
                String episodeName = data.getString("name");
                String date = data.getString("airdate");
                String[] str = {title, episodeName, date, season, episode, description};
                return str;
                //String status = data.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] s) {
            showDetails = new EpisodeDetailsFragment(show, s[0], s[1], s[2], s[3], s[4], s[5]);
            showDetails.show(getActivity().getSupportFragmentManager(), "episodeDetails");
        }
    }*/
}
