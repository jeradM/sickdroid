package com.jeradmeisner.sickbeardalpha.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.jeradmeisner.sickbeardalpha.R;
import com.jeradmeisner.sickbeardalpha.Show;
import com.jeradmeisner.sickbeardalpha.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/15/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class EpisodeDetailsFragment extends SherlockDialogFragment {

    private Show show;

    private String title;
    private String name;
    private String date;
    private String season;
    private String episode;
    private String description;

    private TextView nameTextView;
    private TextView dateTextView;
    private TextView episodeTextView;
    private TextView descriptionTextView;

    public EpisodeDetailsFragment(Show show, String title, String name, String date, String season, String episode, String description)
    {
        this.show = show;
        this.title = title;
        this.name = name;
        this.date = date;
        this.season = season;
        this.episode = episode;
        this.description = description;

        //new LoadEpisodeDetailsTask().execute(null);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.episode_dialog, null);


        nameTextView = (TextView)view.findViewById(R.id.details_episode_name);
        dateTextView = (TextView)view.findViewById(R.id.details_airdate);
        episodeTextView = (TextView)view.findViewById(R.id.details_season_episode);
        descriptionTextView = (TextView)view.findViewById(R.id.details_description);

        nameTextView.setText(name);
        dateTextView.setText(date);
        episodeTextView.setText("Season " + season + ", Episode " + episode);
        descriptionTextView.setText(description);

        ImageView titleView = (ImageView)inflator.inflate(R.layout.episode_dialog_title, null);
        Drawable banner = new BitmapDrawable(getActivity().getResources(), show.getBannerImage());
        titleView.setBackground(banner);

        builder.setCustomTitle(titleView);
        builder.setView(view)
                .setPositiveButton("Search", null)
                .setNegativeButton("Close", null)
                .setNeutralButton("Status", null)
                ;
        return builder.create();

    }

    /*private class LoadEpisodeDetailsTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            String cmd = String.format(ApiCommands.EPISODE.toString(), tvdbid, season, episode);
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(apiUrl, cmd);
            JSONObject data = SickbeardJsonUtils.parseObjectFromJson(obj, "data");
            try {
                description = data.getString("description");
                episodeName = data.getString("name");
                date = data.getString("airdate");
                //String status = data.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return null;
        }
    }*/
}
