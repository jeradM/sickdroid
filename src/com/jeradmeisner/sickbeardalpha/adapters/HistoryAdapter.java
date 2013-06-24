package com.jeradmeisner.sickbeardalpha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jeradmeisner.sickbeardalpha.R;
import com.jeradmeisner.sickbeardalpha.data.HistoryEpisode;
import com.jeradmeisner.sickbeardalpha.data.Show;
import com.jeradmeisner.sickbeardalpha.utils.BannerCacheManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HistoryAdapter extends ArrayAdapter<HistoryEpisode> {

    private Context context;
    private int resource;

    public HistoryAdapter(Context context, int resource, List<HistoryEpisode> items)
    {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout historyView;
        HistoryEpisode item = getItem(position);
        BannerCacheManager bcm = BannerCacheManager.getInstance(context);

        if (convertView == null) {
            historyView = new LinearLayout(getContext());
            String inflator = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflator);
            li.inflate(resource, historyView, true);
        }
        else {
            historyView = (LinearLayout)convertView;
        }

        if ((position % 2) == 0) {
            historyView.setBackgroundResource(R.color.history_list_bg_even);
        }
        else {
            historyView.setBackgroundResource(R.color.history_list_bg_odd);
        }


        ImageView posterView = (ImageView)historyView.findViewById(R.id.poster_image);
        TextView titleView = (TextView)historyView.findViewById(R.id.history_show_title);
        TextView episodeView = (TextView)historyView.findViewById(R.id.history_episode_number);
        TextView dateView = (TextView)historyView.findViewById(R.id.history_date);

        Show show = item.getShow();

        posterView.setImageBitmap(bcm.get(show.getTvdbid(), BannerCacheManager.BitmapType.POSTER));
        titleView.setText(show.getTitle());
        episodeView.setText(String.format("Season %s, Episode %s", item.getSeason(), item.getEpisode()));

        String date = item.getDownloadedDate();
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
        int month = Integer.parseInt(dayInfo[1]) - 1;
        int day = Integer.parseInt(dayInfo[2]);
        int hour = Integer.parseInt(timeInfo[0]);
        int minute = Integer.parseInt(timeInfo[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);

        Date date = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, h:mm a");
        return formatter.format(date);
    }
}
