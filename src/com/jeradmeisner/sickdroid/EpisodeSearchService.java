package com.jeradmeisner.sickdroid;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;
import com.jeradmeisner.sickdroid.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickdroid.utils.enumerations.ApiCommands;
import org.json.JSONException;
import org.json.JSONObject;


public class EpisodeSearchService extends Service {

    private String apiurl;
    private String title;
    private String tvdbid;
    private int season;
    private int episode;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        apiurl = intent.getStringExtra("apiurl");
        tvdbid = intent.getStringExtra("tvdbid");
        title = intent.getStringExtra("title");
        season = intent.getIntExtra("season", -1);
        episode = intent.getIntExtra("episode", -1);
        new EpisodeSearchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return START_NOT_STICKY;
    }

    private class EpisodeSearchTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... objects) {
            String cmd = String.format(ApiCommands.EPISODE_SEARCH.toString(), tvdbid, season, episode);
            JSONObject json = SickbeardJsonUtils.getJsonFromUrl(apiurl, cmd);

            if (json == null)
                return null;

            try {
                String result = json.getString("result");
                if (result.equals("success"))
                    return json.getString("message");
                else
                    return null;
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            String message;
            if (s == null) {
                message = "Episode not found";
            }
            else {
                message = title + " Season " + season + ", Episode " + episode + " " + s;
            }

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
