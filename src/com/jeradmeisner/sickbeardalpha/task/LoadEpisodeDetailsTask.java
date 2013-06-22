package com.jeradmeisner.sickbeardalpha.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import com.jeradmeisner.sickbeardalpha.data.Episode;
import com.jeradmeisner.sickbeardalpha.data.HistoryEpisode;
import com.jeradmeisner.sickbeardalpha.fragments.EpisodeDetailsFragment;
import com.jeradmeisner.sickbeardalpha.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;
import org.json.JSONException;
import org.json.JSONObject;


public class LoadEpisodeDetailsTask extends AsyncTask<Episode, Void, Episode>
{
    private Context context;
    private String apiurl;
    FragmentManager fm;
    Episode i;

    public LoadEpisodeDetailsTask(Context context, String apiurl, FragmentManager fm)
    {
        this.context = context;
        this.apiurl = apiurl;
        this.fm = fm;
    }

    @Override
    protected Episode doInBackground(Episode... s) {
        i = s[0];
        String cmd = String.format(ApiCommands.EPISODE.toString(), i.getShow().getTvdbid(), i.getSeason(), i.getEpisode());
        JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(apiurl, cmd);

        String result;
        try {
            result = obj.getString("result");
        }
        catch (JSONException e) {
            result = "fatal";
        }

        if (result.equals("success")) {
            JSONObject data = SickbeardJsonUtils.parseObjectFromJson(obj, "data");
            try {
                i.setDescription(data.getString("description"));
                //String status = data.getString("status");
            } catch (JSONException e) {
                i.setDescription("");
            }
            return i;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Episode e) {
        if (e != null) {
            EpisodeDetailsFragment showDetails = new EpisodeDetailsFragment(e);
            showDetails.show(fm, "EpisodeDetails");
        }
        else {
            Toast.makeText(context, "Sickbeard encountered an error", 1500).show();
        }
    }
}
