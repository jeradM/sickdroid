package com.jeradmeisner.sickdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jeradmeisner.sickdroid.R;
import com.jeradmeisner.sickdroid.data.FutureEpisode;
import com.jeradmeisner.sickdroid.data.Show;
import com.jeradmeisner.sickdroid.interfaces.FutureListItem;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;
import com.jeradmeisner.sickdroid.widgets.FutureSectionHeader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class FutureAdapter extends ArrayAdapter<FutureListItem> {

    private Context context;
    private List<FutureListItem> items;

    public FutureAdapter(Context context, int resource, List<FutureListItem> items)
    {
        super(context, resource, items);

        this.context = context;
        this.items = items;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).isHeader()) {
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        if (getItemViewType(position) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout futureView;
        FutureListItem item = getItem(position);
        BannerCacheManager bcm = BannerCacheManager.getInstance(context);


        if (getItemViewType(position) == 0) {

            if (convertView == null) {
                futureView = new LinearLayout(getContext());
                LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                li.inflate(R.layout.future_section_header, futureView, true);
            }
            else {
                futureView = (LinearLayout)convertView;
            }

            TextView tv = (TextView)futureView.findViewById(R.id.future_section);
            tv.setText(((FutureSectionHeader) item).getTitle());

        }
        else {

            if (convertView == null) {
                futureView = new LinearLayout(getContext());
                LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                li.inflate(R.layout.history_list_item, futureView, true);
            }
            else {
                futureView = (LinearLayout)convertView;
            }

            ImageView posterView = (ImageView)futureView.findViewById(R.id.poster_image);
            TextView titleView = (TextView)futureView.findViewById(R.id.history_show_title);
            TextView episodeView = (TextView)futureView.findViewById(R.id.history_episode_number);
            TextView dateView = (TextView)futureView.findViewById(R.id.history_date);

            if ((position % 2) == 1) {
                futureView.setBackgroundResource(R.color.history_list_bg_even);
            }
            else {
                futureView.setBackgroundResource(R.color.history_list_bg_odd);
            }

            FutureEpisode ep = (FutureEpisode)item;
            Show show = ep.getShow();

            posterView.setImageBitmap(bcm.get(show.getTvdbid(), BannerCacheManager.BitmapType.POSTER));
            titleView.setText(show.getTitle());
            episodeView.setText(String.format("Season %s, Episode %s", ep.getSeason(), ep.getEpisode()));

            String date = ep.getDate();
            String formattedDate = formatDate(date);

            dateView.setText(formattedDate);

        }

        return futureView;
    }

    private String formatDate(String dateString)
    {
        String[] dayInfo = dateString.split("-");

        int year = Integer.parseInt(dayInfo[0]);
        int month = Integer.parseInt(dayInfo[1]) - 1;
        int day = Integer.parseInt(dayInfo[2]);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        Date date = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d");
        return formatter.format(date);
    }
}
