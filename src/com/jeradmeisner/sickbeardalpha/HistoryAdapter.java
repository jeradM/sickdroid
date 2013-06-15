package com.jeradmeisner.sickbeardalpha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HistoryAdapter extends ArrayAdapter<HistoryItem> {

    private Context context;
    private int resource;

    public HistoryAdapter(Context context, int resource, List<HistoryItem> items)
    {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout historyView;
        HistoryItem item = getItem(position);

        if (convertView == null) {
            historyView = new LinearLayout(getContext());
            String inflator = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflator);
            li.inflate(resource, historyView, true);
        }
        else {
            historyView = (LinearLayout)convertView;
        }


        ImageView posterView = (ImageView)historyView.findViewById(R.id.poster_image);
        TextView titleView = (TextView)historyView.findViewById(R.id.history_show_title);
        TextView episodeView = (TextView)historyView.findViewById(R.id.history_episode_number);
        TextView dateView = (TextView)historyView.findViewById(R.id.history_date);

        Show show = item.getShow();

        posterView.setImageBitmap(show.getPosterImage());
        titleView.setText(show.getTitle());
        episodeView.setText(String.format("Season %s, Episode %s", item.getSeason(), item.getEpisode()));

        String date = item.getDate();
        String formattedDate = formatDate(date);

        dateView.setText(formattedDate);

        return historyView;
    }

    private String formatDate(String dateString)
    {
        String[] dateSplit = dateString.split(" ");
        String[] dayInfo = dateSplit[0].split("-");
        String[] timeInfo = dateSplit[1].split(":");

        int year = Integer.parseInt(dayInfo[0]);
        int month = Integer.parseInt(dayInfo[1]);
        int day = Integer.parseInt(dayInfo[2]);
        int hour = Integer.parseInt(timeInfo[0]);
        int minute = Integer.parseInt(timeInfo[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);

        Date date = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, h:m a");
        return formatter.format(date);
    }
}
