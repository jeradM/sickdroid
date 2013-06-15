package com.jeradmeisner.sickbeardalpha.fragments;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.jeradmeisner.sickbeardalpha.HistoryItem;
import com.jeradmeisner.sickbeardalpha.Show;
import com.jeradmeisner.sickbeardalpha.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/14/13
 * Time: 10:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryListFragment extends SherlockListFragment {

    private String apiurl;
    private String episode;
    private String season;
    private String title;
    SherlockDialogFragment showDetails;
    Show show;

    public HistoryListFragment(String apiurl)
    {
        super();
        this.apiurl = apiurl;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        HistoryItem item = (HistoryItem)l.getAdapter().getItem(position);
        show = item.getShow();
        title = show.getTitle();
        episode = item.getEpisode();
        season = item.getSeason();
        new LoadEpisodeDetailsTask().execute(show.getTvdbid());
    }

    private class LoadEpisodeDetailsTask extends AsyncTask<String, Void, String[]>
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
    }
}
