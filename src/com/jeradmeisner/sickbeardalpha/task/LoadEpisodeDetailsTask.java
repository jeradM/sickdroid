package com.jeradmeisner.sickbeardalpha.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.jeradmeisner.sickbeardalpha.R;
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

        if (i == null)
            return null;

        String cmd = String.format(ApiCommands.EPISODE.toString(), i.getShow().getTvdbid(), i.getSeason(), i.getEpisode());
        JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(apiurl, cmd);

        if (obj == null)
            return null;

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

            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = li.inflate(R.layout.custom_toast_layout, (ViewGroup)((Activity)context).findViewById(R.id.toast_layout_root));

            TextView tv = (TextView)view.findViewById(R.id.toast_text);
            tv.setText("Sickbeard encountered an error");

            Toast toast = new Toast(context.getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(view);
            toast.show();
            //Toast.makeText(context, "Sickbeard encountered an error", 1500).show();
        }
    }
}
