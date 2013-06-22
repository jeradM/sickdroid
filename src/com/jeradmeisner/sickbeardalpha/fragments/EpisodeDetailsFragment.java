package com.jeradmeisner.sickbeardalpha.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.jeradmeisner.sickbeardalpha.R;
import com.jeradmeisner.sickbeardalpha.data.Episode;
import com.jeradmeisner.sickbeardalpha.data.Show;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EpisodeDetailsFragment extends SherlockDialogFragment {


    private Episode episode;

    private TextView nameTextView;
    private TextView dateTextView;
    private TextView episodeTextView;
    private TextView descriptionTextView;

    public EpisodeDetailsFragment(Episode episode)
    {
        this.episode = episode;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.episode_dialog, null);

        ImageView dialogHeaderBanner = (ImageView)view.findViewById(R.id.details_dialog_title_banner);
        dialogHeaderBanner.setImageBitmap(episode.getShow().getBannerImage());


        nameTextView = (TextView)view.findViewById(R.id.details_episode_name);
        dateTextView = (TextView)view.findViewById(R.id.details_airdate);
        episodeTextView = (TextView)view.findViewById(R.id.details_season_episode);
        descriptionTextView = (TextView)view.findViewById(R.id.details_description);

        nameTextView.setText(episode.getShow().getTitle());
        String date = (toDateFormat(episode.getDate()));

        if (!date.equals("No Date Found")) {
            date = episode.airString() + date;
        }
        dateTextView.setText(date);
        episodeTextView.setText("Season " + episode.getSeason() + ", Episode " + episode.getEpisode());

        if (episode.getDescription().length() < 1) {
            episode.setDescription("No Description Available");
            descriptionTextView.setGravity(Gravity.CENTER);
        }
        descriptionTextView.setText(episode.getDescription());

        setStyle(SherlockDialogFragment.STYLE_NO_TITLE, R.style.Theme_Sickdroid_LightGreen);
        builder.setView(view)
                .setPositiveButton("Search", null)
                .setNegativeButton("Close", null)
                .setNeutralButton("Status", null)
                ;
        return builder.create();

    }

    private String toDateFormat(String dateString)
    {
        try {
            String[] dateTime = dateString.split(" ");
            String[] dateElements = dateTime[0].split("-");

            int year = Integer.parseInt(dateElements[0]);
            int month = Integer.parseInt(dateElements[1]);
            int day = Integer.parseInt(dateElements[2]);

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);

            Date date = cal.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d");
            String formatted = formatter.format(date);

            return formatted;
        } catch (NumberFormatException e) {
            return "No Date Found";
        }

    }
}
