package com.jeradmeisner.sickbeardalpha.task;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import com.jeradmeisner.sickbeardalpha.data.HistoryEpisode;
import com.jeradmeisner.sickbeardalpha.fragments.EpisodeDetailsFragment;
import com.jeradmeisner.sickbeardalpha.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/19/13
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoadEpisodeDetailsTask extends AsyncTask<HistoryEpisode, Void, String[]>
{
    private String apiurl;
    FragmentManager fm;
    HistoryEpisode i;

    public LoadEpisodeDetailsTask(String apiurl, FragmentManager fm)
    {
        this.apiurl = apiurl;
        this.fm = fm;
    }

    @Override
    protected String[] doInBackground(HistoryEpisode... s) {
        i = s[0];
        String cmd = String.format(ApiCommands.EPISODE.toString(), i.getShow().getTvdbid(), i.getSeason(), i.getEpisode());
        JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(apiurl, cmd);
        JSONObject data = SickbeardJsonUtils.parseObjectFromJson(obj, "data");
        try {
            String description = data.getString("description");
            String episodeName = data.getString("name");
            String date = data.getString("airdate");
            String[] str = {i.getShow().getTitle(), episodeName, date, i.getSeason(), i.getEpisode(), description};
            return str;
            //String status = data.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] s) {
        EpisodeDetailsFragment showDetails = new EpisodeDetailsFragment(i.getShow(), s[0], s[1], s[2], s[3], s[4], s[5]);
        showDetails.show(fm, "EpisodeDetails");
    }
}
